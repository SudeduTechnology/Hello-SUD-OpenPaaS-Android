package global.sud.gi.hello.app.game.op.data;

public class OPSession {

    public final String appToken;
    public final long expiresAtEpochMs;
    public final long uid;
    public final String nickname;
    public final String avatarUrl;
    public final boolean casdoorBound;

    public OPSession(String appToken,
                           long expiresAtEpochMs,
                           long uid,
                           String nickname,
                           String avatarUrl,
                           boolean casdoorBound) {
        this.appToken = appToken;
        this.expiresAtEpochMs = expiresAtEpochMs;
        this.uid = uid;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.casdoorBound = casdoorBound;
    }

    public boolean isValid(long nowEpochMs) {
        return appToken != null
                && !appToken.trim().isEmpty()
                && uid > 0
                && expiresAtEpochMs > nowEpochMs;
    }
}
