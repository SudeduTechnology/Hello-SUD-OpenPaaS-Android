package global.sud.gi.hello.app.game.op.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

@SuppressLint("ApplySharedPref")
public class OPSessionStore {

    private static final String PREFERENCES_NAME = "op_session_v1";
    private static final String KEY_APP_TOKEN = "app_token";
    private static final String KEY_EXPIRES_AT = "expires_at_epoch_ms";
    private static final String KEY_UID = "uid";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AVATAR_URL = "avatar_url";
    private static final String KEY_CASDOOR_BOUND = "casdoor_bound";
    private static final String KEY_IDEMPOTENCY_KEY = "login_idempotency_key";

    private final SharedPreferences preferences;

    public OPSessionStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public OPSession getValidSession() {
        return getValidSession(System.currentTimeMillis());
    }

    OPSession getValidSession(long nowEpochMs) {
        OPSession session = readSession();
        return session != null && session.isValid(nowEpochMs) ? session : null;
    }

    public OPSession readSession() {
        String appToken = preferences.getString(KEY_APP_TOKEN, null);
        long expiresAt = preferences.getLong(KEY_EXPIRES_AT, 0L);
        long uid = preferences.getLong(KEY_UID, 0L);
        String nickname = preferences.getString(KEY_NICKNAME, "");
        String avatarUrl = preferences.getString(KEY_AVATAR_URL, "");
        boolean casdoorBound = preferences.getBoolean(KEY_CASDOOR_BOUND, false);
        OPSession session = new OPSession(
                appToken,
                expiresAt,
                uid,
                nickname == null ? "" : nickname,
                avatarUrl == null ? "" : avatarUrl,
                casdoorBound
        );
        return session.isValid(0L) ? session : null;
    }

    public boolean save(OPSession session) {
        if (session == null || !session.isValid(System.currentTimeMillis())) {
            return false;
        }
        return preferences.edit()
                .putString(KEY_APP_TOKEN, session.appToken)
                .putLong(KEY_EXPIRES_AT, session.expiresAtEpochMs)
                .putLong(KEY_UID, session.uid)
                .putString(KEY_NICKNAME, session.nickname == null ? "" : session.nickname)
                .putString(KEY_AVATAR_URL, session.avatarUrl == null ? "" : session.avatarUrl)
                .putBoolean(KEY_CASDOOR_BOUND, session.casdoorBound)
                .commit();
    }

    public void clearSession() {
        preferences.edit()
                .remove(KEY_APP_TOKEN)
                .remove(KEY_EXPIRES_AT)
                .remove(KEY_UID)
                .remove(KEY_NICKNAME)
                .remove(KEY_AVATAR_URL)
                .remove(KEY_CASDOOR_BOUND)
                .commit();
    }

    public String getOrCreateIdempotencyKey() {
        String value = preferences.getString(KEY_IDEMPOTENCY_KEY, null);
        if (value != null && value.length() >= 8) {
            return value;
        }
        String generated = UUID.randomUUID().toString();
        if (!preferences.edit().putString(KEY_IDEMPOTENCY_KEY, generated).commit()) {
            return null;
        }
        return generated;
    }
}
