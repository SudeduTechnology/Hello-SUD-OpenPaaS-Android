package global.sud.op.hello.service;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import global.sud.op.hello.R;
import global.sud.op.hello.common.http.param.BaseUrlManager;
import global.sud.op.hello.common.http.retrofit.RetrofitManager;
import global.sud.op.hello.common.http.rx.RxCallback;
import global.sud.op.hello.common.http.rx.RxUtils;
import global.sud.op.hello.service.req.GameLoginReq;
import global.sud.op.hello.service.req.GetUserProfileReq;
import global.sud.op.hello.service.req.MockPaymentReq;
import global.sud.op.hello.service.req.ValidatePaymentReq;
import global.sud.op.hello.service.resp.GameLoginResp;
import global.sud.op.hello.service.resp.GetUserProfileResp;
import global.sud.op.hello.service.resp.MockPaymentResp;
import global.sud.op.hello.service.resp.ValidatePaymentResp;
import global.sud.op.hello.ui.main.GameModel;

public class MainRepository {

    private static final MainRequestMethod method = RetrofitManager.createMethod(MainRequestMethod.class);

    /**
     * 获取游戏列表
     */
    public static List<GameModel> getRuntimeGameList() {
        ArrayList<GameModel> list = new ArrayList<>();

        list.add(buildGameModel("sud.game.flappy.bird", "Runtime-FlappyBird", "https://hello-sud-plus.sudden.ltd/ad/resource/game/FlappyBird.cpk",
                "1.1", R.drawable.fbdr, R.drawable.ic_fbdr));

        list.add(buildGameModel("sud.game.flappy.linkclear", "Runtime-linkclear", "https://hello-sud-plus.sudden.ltd/ad/resource/game/linkclear.cpk",
                "1.6", R.drawable.nhwc, R.drawable.ic_nhwc));

//        list.add(buildGameModel("game.runtime_assets", "Runtime_assets", "ass_FlappyBird.cpk",
//                "1.1", R.drawable.ddsh, R.drawable.ic_ddsh));

//        list.add(buildGameModel("2017065825404788738", "gameIdTest", null,
//                "1.1", R.drawable.fxq, R.drawable.ic_fxq));
        return list;
    }

    /**
     * 构建GameModel
     */
    public static GameModel buildGameModel(String gameId, String gameName, String gameUrl, String gamePkgVersion, int homeGamePic, int gamePic) {
        GameModel model = new GameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.gameUrl = gameUrl;
        model.gamePkgVersion = gamePkgVersion;
        model.homeGamePic = homeGamePic;
        model.gamePic = gamePic;
        return model;
    }

    /**
     * 接入方客户端调用接入方服务端获取短期令牌code（getCode）
     * { 接入方服务端仓库：https://github.com/SudTechnology/hello-sud-java }
     * ------ 暂时不使用此方法，改为使用okhttp直接请求数据
     *
     * @param owner    生命周期对象
     * @param appId    SudMGP appId
     * @param userId   用户id
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String appId, String userId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.app_id = appId;
        req.user_id = userId;
        method.gameLogin(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void getUserProfile(LifecycleOwner owner, String appId, String userId, String encrypted_data, RxCallback<GetUserProfileResp> callback) {
        GetUserProfileReq req = new GetUserProfileReq();
        req.app_id = appId;
        req.user_id = userId;
        req.encrypted_data = encrypted_data;
        method.getUserProfile(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void mockPayment(LifecycleOwner owner, String appId, String userId, String sud_trade_no, String action, RxCallback<MockPaymentResp> callback) {
        MockPaymentReq req = new MockPaymentReq();
        req.app_id = appId;
        req.user_id = userId;
        req.sud_trade_no = sud_trade_no;
        req.action = action;
        method.mockPayment(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void validatePayment(LifecycleOwner owner, String appId, String userId, String sign_data, String signature, RxCallback<ValidatePaymentResp> callback) {
        ValidatePaymentReq req = new ValidatePaymentReq();
        req.app_id = appId;
        req.user_id = userId;
        req.sign_data = sign_data;
        req.signature = signature;
        method.validatePayment(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
