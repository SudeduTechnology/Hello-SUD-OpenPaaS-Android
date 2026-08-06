package global.sud.op.hello.ui.game.sudedu;

import java.util.Random;

public class Util {
    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateRandomString(int len) {
        if (len <= 0) {
            return "";
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        int poolLength = CHAR_POOL.length();
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(poolLength);
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

}
