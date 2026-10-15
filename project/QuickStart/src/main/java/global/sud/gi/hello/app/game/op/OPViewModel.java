package global.sud.gi.hello.app.game.op;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.utils.LanguageUtils;
import global.sud.gi.hello.app.game.op.data.OPSession;
import global.sud.gi.hello.app.game.op.net.OPApiClient;
import global.sud.gi.hello.app.game.op.net.OPUserSignatureResponse;
import global.sud.op.runtime.core.SUDRTGameHandle;
import global.sud.op.runtime.core.model.SUDOPGameInfo;

public class OPViewModel extends BaseGameViewModel {

    private static final String TAG = "OP";

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();
    private final OPApiClient apiClient;
    private final Context appContext;

    public OPViewModel(Activity activity) {
        appContext = activity.getApplicationContext();
        apiClient = new OPApiClient(appContext);
    }

    @Override
    protected void getCode(Activity activity,
                           String userId,
                           String appId,
                           GameGetCodeListener listener) {
        apiClient.requestUserSignature(new OPApiClient.SignatureCallback() {
            @Override
            public void onSuccess(OPUserSignatureResponse response) {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    listener.onSuccess(response.userSignature);
                }
            }

            @Override
            public void onFailure(int code, String message) {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    progressLiveData.setValue(new ProgressModel(1, code, message, 0));
                    listener.onFailed(code, message);
                }
            }
        });
    }

    @Override
    public String getUserId() {
        OPSession session = apiClient.getSessionStore().readSession();
        return session == null ? "" : String.valueOf(session.uid);
    }

    @Override
    public String getAppId() {
        return AppConfig.OP_APP_ID;
    }

    @Override
    public String getAppKey() {
        return AppConfig.OP_APP_KEY;
    }

    @Override
    protected String getLanguage(Activity activity) {
        return LanguageUtils.getLanguageTag(activity);
    }

    @Override
    protected boolean shouldRetryAfterFailure(int retCode, String retMsg) {
        return false;
    }

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

    @Override
    protected void onGameHandleCreated(SUDRTGameHandle handle, SUDOPGameInfo gameInfo) {
    }

    @Override
    protected void logE(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    protected void toastMsg(String msg) {
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
    }

    public OPApiClient getApiClient() {
        return apiClient;
    }

    public void destroy() {
        apiClient.cancelAll();
        destroyGame();
    }
}
