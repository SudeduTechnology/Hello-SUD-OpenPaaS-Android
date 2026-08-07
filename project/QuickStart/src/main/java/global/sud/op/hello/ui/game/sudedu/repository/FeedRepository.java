package global.sud.op.hello.ui.game.sudedu.repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.ui.game.sudedu.model.GameFeedItem;
import okhttp3.MediaType;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FeedRepository {

    private static final String TAG = "FeedRepository";
    private static final String BASE_URL = "https://fat-saasapi.s00.tech";
    private static final String LINKED_PAGE_PATH = "/v1/app/game/link/linked/page";
    private static final String ALGORITHM = "SUD-HMAC-SHA256";
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final String SUD_APP_ID = AppConfig.SudGIP_APP_ID;
    private static final String SUD_APP_SERVER_KEY = AppConfig.SudGIP_APP_SERVER_KEY;
    private static final String SUD_APP_SERVER_SECRET = AppConfig.SudGIP_APP_SERVER_SECRET;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson GSON = new Gson();
    private static final java.security.spec.PSSParameterSpec RSA_PSS_SHA256 =
            new java.security.spec.PSSParameterSpec("SHA-256", "MGF1", java.security.spec.MGF1ParameterSpec.SHA256, 32, 1);

    private static FeedRepository instance;
    private final OkHttpClient client;
    private List<GameFeedItem> linkedGames;

    public static FeedRepository getInstance() {
        if (instance == null) {
            instance = new FeedRepository();
        }
        return instance;
    }

    public FeedRepository() {
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public GameFeedItem getGameFeedItemByGameId(String gameId){
        if (linkedGames == null) {
            return null;
        }
        for (GameFeedItem item : linkedGames) {
            if (item.gameId.equals(gameId)) {
                return item;
            }
        }
        return null;
    }

    public List<GameFeedItem> fetchLinkedGames() throws Exception {
        if (linkedGames != null) {
            return linkedGames;
        }
        // status_list: 2=已关联,3=未关联(只取已关联的)
        LinkedGamesRequest payload = new LinkedGamesRequest(SUD_APP_ID, 1, 50, Collections.singletonList(2));
        String bodyJson = GSON.toJson(payload);
        long timestamp = System.currentTimeMillis() / 1000L;
        String urlPath = BASE_URL + LINKED_PAGE_PATH;
        String authorization = buildHmacAuthorization("POST", LINKED_PAGE_PATH, bodyJson, timestamp);
        Request request = new Request.Builder()
                .url(urlPath)
                .addHeader("X-sud-timestamp", String.valueOf(timestamp))
                .addHeader("Content-type", JSON.toString())
                .addHeader("Authorization", authorization)
                .post(RequestBody.create(bodyJson, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String raw = responseBodyString(response);
            if (!response.isSuccessful()) {
                throw new IOException(String.format(Locale.US, "HTTP %d: %s", response.code(), raw));
            }
            
            LinkedGamesResponse linkedResponse = GSON.fromJson(raw, LinkedGamesResponse.class);
            if (linkedResponse == null || linkedResponse.errcode != 0) {
                throw new IOException("linked games request failed: " + raw);
            }
            // 响应结构:records 可能在根级别(如 /v1/app/game/link/linked/page),也可能嵌套在 data 下
            List<GameFeedItem> games = linkedResponse.records;
            if (games == null && linkedResponse.data != null) {
                games = linkedResponse.data.records;
            }
            if (games == null) {
                return new ArrayList<>();
            }

            Log.d(TAG, "==================fetchLinkedGames, 可用gameId 如下：");
            linkedGames = new ArrayList<>(games.size());
            for (GameFeedItem game : games) {
                if (game != null && (game.status == null || game.status == 2)) {
                    linkedGames.add(game);
                    Log.d(TAG, game.gameName +" "+ game.gameId);
                }
            }
            Log.d(TAG, "==================fetchLinkedGames, end");
            return linkedGames;
        }
    }

    /**
     * 按 SUD-HMAC-SHA256 算法生成 Authorization 头 (与 sign.py 的 sign() 流程一致):
     * 1. CanonicalRequest = method\nuri\nquery\ncanonicalHeaders\nsignedHeaderNames\nsha256(body)
     * 2. StringToSign      = ALGORITHM\nsha256(CanonicalRequest)
     * 3. Signature          = HMAC-SHA256(secret, StringToSign) → hex
     */
    private String buildHmacAuthorization(String httpMethod, String canonicalUri,
                                          String bodyJson, long timestamp) {
        try {
            String host = HttpUrl.parse(BASE_URL).host();
            String ts = String.valueOf(timestamp);
            // 参与签名的 header: host / content-type / x-sud-* (按 key 排序)
            TreeMap<String, String> headers = new TreeMap<>();
            headers.put("content-type", JSON.toString());
            headers.put("host", host);
            headers.put("x-sud-timestamp", ts);

            StringBuilder canonicalHeaders = new StringBuilder();
            StringBuilder signedHeaderNames = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                canonicalHeaders.append(entry.getKey())
                        .append(':').append(entry.getValue()).append('\n');
                if (!first) signedHeaderNames.append(';');
                signedHeaderNames.append(entry.getKey());
                first = false;
            }

            // token 接口无 query 参数,query string 为空
            String canonicalRequest = String.join("\n",
                    httpMethod, canonicalUri, "",
                    canonicalHeaders.toString(),
                    signedHeaderNames.toString(),
                    sha256Hex(bodyJson));

            String stringToSign = ALGORITHM + "\n" + sha256Hex(canonicalRequest);
            String signature = hmacSha256Hex(SUD_APP_SERVER_SECRET, stringToSign);

            return ALGORITHM + " Credential=" + SUD_APP_SERVER_KEY
                    + ", SignedHeaders=" + signedHeaderNames
                    + ", Signature=" + signature;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("build HMAC authorization failed", e);
        }
    }

    private String sha256Hex(String input) throws GeneralSecurityException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return bytesToHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    private String hmacSha256Hex(String key, String msg) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return bytesToHex(mac.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    private String bytesToHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = HEX_CHARS[v >>> 4];
            out[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(out);
    }

    private String responseBodyString(Response response) throws IOException {
        ResponseBody body = response.body();
        String raw = body != null ? body.string() : "";
        if (!response.isSuccessful()) {
            throw new IOException(String.format(Locale.US, "HTTP %d: %s", response.code(), raw));
        }
        return raw;
    }

    private static final class LinkedGamesRequest {
        @SerializedName("app_id")
        final String appId;
        @SerializedName("page_no")
        final int pageNo;
        @SerializedName("page_size")
        final int pageSize;
        @SerializedName("status_list")
        final List<Integer> statusList;

        LinkedGamesRequest(String appId, int pageNo, int pageSize, List<Integer> statusList) {
            this.appId = appId;
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.statusList = statusList;
        }
    }

    private static final class LinkedGamesResponse {
        @SerializedName("errcode")
        final int errcode;
        @SerializedName("errmsg")
        final String errmsg;
        @SerializedName("data")
        final LinkedGamesData data;
        // /v1/app/game/link/linked/page 直接把 records 平铺在根级别
        @SerializedName("records")
        final List<GameFeedItem> records;

        LinkedGamesResponse(int errcode, String errmsg, LinkedGamesData data, List<GameFeedItem> records) {
            this.errcode = errcode;
            this.errmsg = errmsg;
            this.data = data;
            this.records = records;
        }
    }

    private static final class LinkedGamesData {
        @SerializedName("records")
        final List<GameFeedItem> records;

        LinkedGamesData(List<GameFeedItem> records) {
            this.records = records;
        }
    }

}
