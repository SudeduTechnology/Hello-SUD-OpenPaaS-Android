package global.sud.gi.hello.app.game.op.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.game.op.data.OPSession;
import global.sud.gi.hello.app.game.op.data.OPSessionStore;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OPApiClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final RequestBody EMPTY_JSON_BODY = RequestBody.create("{}", JSON);
    private static final Gson GSON = new Gson();

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final OPSessionStore sessionStore;
    private final Set<Call> calls = Collections.synchronizedSet(new HashSet<>());

    public OPApiClient(Context context) {
        sessionStore = new OPSessionStore(context);
    }

    public OPSessionStore getSessionStore() {
        return sessionStore;
    }

    public void ensureSession(SessionCallback callback) {
        OPSession session = sessionStore.getValidSession();
        if (session != null) {
            postSuccess(callback, session);
            return;
        }
        loginAnonymous(callback);
    }

    public void requestUserSignature(SignatureCallback callback) {
        ensureSession(new SessionCallback() {
            @Override
            public void onSuccess(OPSession session) {
                requestUserSignature(session, false, callback);
            }

            @Override
            public void onFailure(int code, String message) {
                callback.onFailure(code, message);
            }
        });
    }

    private void loginAnonymous(SessionCallback callback) {
        String idempotencyKey = sessionStore.getOrCreateIdempotencyKey();
        if (idempotencyKey == null) {
            postFailure(callback, -1, "Unable to persist login request identity");
            return;
        }
        Request request;
        try {
            request = new Request.Builder()
                    .url(buildUrl("/api/auth/login"))
                    .header("Idempotency-Key", idempotencyKey)
                    .post(EMPTY_JSON_BODY)
                    .build();
        } catch (IllegalArgumentException e) {
            postFailure(callback, -1, "Invalid Game server address");
            return;
        }
        enqueue(request, new ResponseHandler() {
            @Override
            public void onResponse(int statusCode, String body) {
                if (statusCode < 200 || statusCode >= 300) {
                    postFailure(callback, statusCode, "Login failed with HTTP " + statusCode);
                    return;
                }
                try {
                    OPLoginResponse response = parseLoginResponse(body);
                    long expiresAt = safeExpiresAt(response.appTokenExpiresIn);
                    OPSession session = new OPSession(
                            response.appToken,
                            expiresAt,
                            response.uid,
                            response.nickname,
                            response.avatarUrl,
                            response.casdoorBound
                    );
                    if (!sessionStore.save(session)) {
                        postFailure(callback, -1, "Unable to save login session");
                        return;
                    }
                    postSuccess(callback, session);
                } catch (Exception e) {
                    postFailure(callback, -1, "Invalid login response");
                }
            }

            @Override
            public void onFailure(IOException error) {
                postFailure(callback, -1, "Login request failed");
            }
        });
    }

    private void requestUserSignature(OPSession session,
                                      boolean alreadyRecovered,
                                      SignatureCallback callback) {
        Request request;
        try {
            request = new Request.Builder()
                    .url(buildUrl("/api/openpaas/user-signature"))
                    .header("Authorization", "Bearer " + session.appToken)
                    .post(EMPTY_JSON_BODY)
                    .build();
        } catch (IllegalArgumentException e) {
            postFailure(callback, -1, "Invalid Game server address");
            return;
        }
        enqueue(request, new ResponseHandler() {
            @Override
            public void onResponse(int statusCode, String body) {
                if (statusCode == 401 && !alreadyRecovered) {
                    sessionStore.clearSession();
                    loginAnonymous(new SessionCallback() {
                        @Override
                        public void onSuccess(OPSession recoveredSession) {
                            requestUserSignature(recoveredSession, true, callback);
                        }

                        @Override
                        public void onFailure(int code, String message) {
                            callback.onFailure(code, message);
                        }
                    });
                    return;
                }
                if (statusCode < 200 || statusCode >= 300) {
                    postFailure(callback, statusCode, "User signature failed with HTTP " + statusCode);
                    return;
                }
                try {
                    OPUserSignatureResponse response = parseUserSignatureResponse(body);
                    if (!AppConfig.OP_APP_ID.equals(response.appId)
                            || session.uid != response.uid
                            || response.userSignature == null
                            || response.userSignature.trim().isEmpty()) {
                        postFailure(callback, -1, "User signature identity mismatch");
                        return;
                    }
                    postSignatureSuccess(callback, response);
                } catch (Exception e) {
                    postFailure(callback, -1, "Invalid user signature response");
                }
            }

            @Override
            public void onFailure(IOException error) {
                postFailure(callback, -1, "User signature request failed");
            }
        });
    }

    private void enqueue(Request request, ResponseHandler responseHandler) {
        Call call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                calls.remove(call);
                if (!call.isCanceled()) {
                    responseHandler.onFailure(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                calls.remove(call);
                try (Response closeable = response) {
                    String body = closeable.body() == null ? "" : closeable.body().string();
                    responseHandler.onResponse(closeable.code(), body);
                } catch (IOException e) {
                    responseHandler.onFailure(e);
                }
            }
        });
    }

    public void cancelAll() {
        synchronized (calls) {
            for (Call call : calls) {
                call.cancel();
            }
            calls.clear();
        }
    }

    static OPLoginResponse parseLoginResponse(String body) throws Exception {
        OPLoginResponse response = GSON.fromJson(body, OPLoginResponse.class);
        if (response == null
                || response.appToken == null
                || response.appToken.trim().isEmpty()
                || response.appTokenExpiresIn <= 0
                || response.uid <= 0
                || response.nickname == null) {
            throw new IllegalArgumentException("Missing login fields");
        }
        if (response.avatarUrl == null) {
            response.avatarUrl = "";
        }
        return response;
    }

    static OPUserSignatureResponse parseUserSignatureResponse(String body) throws Exception {
        UserSignatureEnvelope envelope = GSON.fromJson(body, UserSignatureEnvelope.class);
        if (envelope == null || envelope.user == null) {
            throw new IllegalArgumentException("Missing user signature fields");
        }
        OPUserSignatureResponse response = new OPUserSignatureResponse();
        response.appId = envelope.appId;
        response.uid = envelope.user.id;
        response.nickname = envelope.user.name == null ? "" : envelope.user.name;
        response.avatarUrl = envelope.user.avatar == null ? "" : envelope.user.avatar;
        response.userSignature = envelope.userSignature;
        response.expiresAt = envelope.expiresAt == null ? "" : envelope.expiresAt;
        return response;
    }

    private static class UserSignatureEnvelope {
        String appId;
        User user;
        String userSignature;
        String expiresAt;
    }

    private static class User {
        long id;
        String name;
        String avatar;
    }

    private long safeExpiresAt(long expiresInSeconds) {
        long now = System.currentTimeMillis();
        long maxSeconds = (Long.MAX_VALUE - now) / 1000L;
        return now + Math.min(expiresInSeconds, maxSeconds) * 1000L;
    }

    private String buildUrl(String path) {
        String baseUrl = AppConfig.APP_BASE_URL == null ? "" : AppConfig.APP_BASE_URL.trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (!baseUrl.startsWith("https://") && !baseUrl.startsWith("http://")) {
            throw new IllegalArgumentException("Invalid base URL");
        }
        return baseUrl + path;
    }

    private void postSuccess(SessionCallback callback, OPSession session) {
        mainHandler.post(() -> callback.onSuccess(session));
    }

    private void postSignatureSuccess(SignatureCallback callback,
                                      OPUserSignatureResponse response) {
        mainHandler.post(() -> callback.onSuccess(response));
    }

    private void postFailure(SessionCallback callback, int code, String message) {
        mainHandler.post(() -> callback.onFailure(code, message));
    }

    private void postFailure(SignatureCallback callback, int code, String message) {
        mainHandler.post(() -> callback.onFailure(code, message));
    }

    private interface ResponseHandler {
        void onResponse(int statusCode, String body);

        void onFailure(IOException error);
    }

    public interface SessionCallback {
        void onSuccess(OPSession session);

        void onFailure(int code, String message);
    }

    public interface SignatureCallback {
        void onSuccess(OPUserSignatureResponse response);

        void onFailure(int code, String message);
    }
}
