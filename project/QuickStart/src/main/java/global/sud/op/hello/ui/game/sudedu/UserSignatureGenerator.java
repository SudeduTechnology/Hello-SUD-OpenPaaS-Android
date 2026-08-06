package global.sud.op.hello.ui.game.sudedu;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.time.ZonedDateTime;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.utils.GlobalSP;
import java.nio.charset.StandardCharsets;

/**
 * 模拟服务器端生成 SudOP userSignature(AES-256-GCM)。
 *
 * 流程(按 SUD 文档):
 *   payload = {"version":"1","user_id":"xxx","expires_at":<当前时间一个自然月后的 unix 秒>}
 *   aesKey  = 后台「数据加密/解密密钥」明文,Base64 编码,解码后 32 字节
 *   output  = Base64( IV(12B) + ciphertext + tag(16B) )
 *
 * 这个 output 就是 SUDOP.auth 接收的 userSignature。
 *
 * 参考: https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/server/data-security/user-signature-guide.html
 */
public final class UserSignatureGenerator {
    private static final String TAG = "UserSignatureGenerator";
    private static final int IV_LEN = 12;
    private static final int TAG_LEN_BITS = 128;

    private UserSignatureGenerator() {
    }

    // 模拟从服务器获取UserId
    public static String getUserId() {
        String userId = GlobalSP.getSP().getString(GlobalSP.KEY_USER_ID);
        if(userId.isEmpty()){
            userId = Util.generateRandomString(6);
            GlobalSP.getSP().put(GlobalSP.KEY_USER_ID, userId);
        }
        return userId;
    }

    /**
     * 模拟App服务器生成 userSignature。
     *
     * @param userId 业务用户 ID(非空)
     * @return Base64 的 userSignature,失败返回空串
     */
    public static String generateCode(String userId) {
        try {
            return generateInternal(AppConfig.SudGIP_AES_KEY_B64, userId);
        } catch (Throwable t) {
            Log.e(TAG, "generate failed: " + t.getMessage(), t);
            return "";
        }
    }

    /**
     * 供调试/测试用的内部入口(可指定 aesKey)。
     */
    public static String generateInternal(String aesKeyB64, String userId) throws Exception {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId 不能为空");
        }

        // 1. 解 Base64 拿 32 字节密钥
        byte[] keyBytes = Base64.decode(aesKeyB64, Base64.DEFAULT);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key 长度必须是 32 字节(AES-256),实际 " + keyBytes.length);
        }

        // 2. 构造 payload(紧凑 JSON,无空格无换行)
        long expiresAt = ZonedDateTime.now().plusMonths(1).toEpochSecond();
        String payload = new JSONObject()
                .put("version", "1")
                .put("user_id", userId)
                .put("expires_at", expiresAt)
                .toString();

        // 3. 随机 IV(12 字节)
        byte[] iv = new byte[IV_LEN];
        new SecureRandom().nextBytes(iv);

        // 4. AES-256-GCM 加密
        // Android 的 GCM 实现会把 tag 追加到 ciphertext 末尾(共 ciphertext+16 字节)
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(keyBytes, "AES"),
                new GCMParameterSpec(TAG_LEN_BITS, iv)
        );
        byte[] cipherAndTag = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        // 5. 拼接 IV + cipherAndTag,整体 Base64 输出
        byte[] out = new byte[IV_LEN + cipherAndTag.length];
        System.arraycopy(iv, 0, out, 0, IV_LEN);
        System.arraycopy(cipherAndTag, 0, out, IV_LEN, cipherAndTag.length);

        String signature = Base64.encodeToString(out, Base64.NO_WRAP);
        Log.d(TAG, "generated userSignature userId="+userId+", signature="+signature);

        // 测试解密是否能恢复原始 payload
        String decoded = decodeForDebug(aesKeyB64, signature);
        Log.d(TAG, "decoded userSignature: " + decoded);
        
        return signature;
    }

    /**
     * 解码 userSignature 回 payload(调试用)。
     * 用同一个 AES key 解密,返回原 payload JSON 字符串。
     */
    public static String decodeForDebug(String aesKeyB64, String signature) throws Exception {
        byte[] keyBytes = Base64.decode(aesKeyB64, Base64.DEFAULT);
        byte[] data = Base64.decode(signature, Base64.DEFAULT);
        byte[] iv = copyOfRange(data, 0, IV_LEN);
        byte[] cipherAndTag = copyOfRange(data, IV_LEN, data.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(keyBytes, "AES"),
                new GCMParameterSpec(TAG_LEN_BITS, iv)
        );
        byte[] plain = cipher.doFinal(cipherAndTag);
        return new String(plain, StandardCharsets.UTF_8);
    }

    private static byte[] copyOfRange(byte[] src, int from, int to) {
        int length = to - from;
        byte[] result = new byte[length];
        System.arraycopy(src, from, result, 0, length);
        return result;
    }
}
