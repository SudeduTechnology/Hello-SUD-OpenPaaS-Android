package global.sud.op.hello.ui.game;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.sudedu.UserSignatureGenerator;
import global.sud.op.hello.ui.game.sudedu.Util;

public class QuickStartGameViewModel extends BaseGameViewModel {

    // TODO: Sud平台申请的appId
    // TODO: The appId obtained from Sud platform application.
    public String SudGIP_APP_ID = AppConfig.SudGIP_APP_ID;

    // TODO: Sud平台申请的appKey
    // TODO: The appKey obtained from Sud platform application.
    public String SudGIP_APP_KEY = AppConfig.SudGIP_APP_KEY;

    // TODO: 使用的UserId。这里随机生成作演示，开发者将其修改为业务使用的唯一userId
    // TODO: Used UserId. Here it is randomly generated for demonstration purposes. Developers should modify it to the unique userId used for the business.
//    public static String userId = QuickStartUtils.genUserID();

    public static String userId;
    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();

    /**
     * 向接入方服务器获取code
     * Retrieve the code from the partner's server.
     */
    @Override
    protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
        String code = UserSignatureGenerator.generateCode(userId);
        listener.onSuccess(code);
    }

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the partner).
     */
    @Override
    protected String getUserId() {
        if(userId == null){
            userId = UserSignatureGenerator.getUserId();
        }
        return userId;
    }

    @Override
    protected String getNickName() {
        if(userId == null){
            userId = UserSignatureGenerator.getUserId();
        }
        return userId;
    }

    @Override
    protected String getAvatar(){
        return  "http://39.106.210.142:8080/head/0.png";
    }

    /**
     * 设置Sud平台申请的appId
     * Set the appId obtained from the Sud platform.
     */
    @Override
    protected String getAppId() {
        return SudGIP_APP_ID;
    }

    /**
     * 设置Sud平台申请的appKey
     * Set the appKey obtained from the Sud platform.
     */
    @Override
    protected String getAppKey() {
        return SudGIP_APP_KEY;
    }

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

}
