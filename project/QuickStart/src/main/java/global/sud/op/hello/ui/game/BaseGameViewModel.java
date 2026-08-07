package global.sud.op.hello.ui.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import global.sud.gi.core.ISUDAPPD;
import global.sud.op.hello.ui.game.sudedu.SeduExtendedClient;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.SUDOPGameTask;
import global.sud.op.runtime.core.SUDRTFSTAPP;
import global.sud.op.runtime.core.SUDRTGameAudioSession;
import global.sud.op.runtime.core.SUDRTGameConfig;
import global.sud.op.runtime.core.SUDRTGameHandle;
import global.sud.op.runtime.core.SUDRTGamePackageManager;
import global.sud.op.runtime.core.listener.SUDOPAuthListener;
import global.sud.op.runtime.core.listener.SUDOPInitSDKListener;
import global.sud.op.runtime.core.listener.SUDOPStartGameListener;
import global.sud.op.runtime.core.listener.SUDOPUninitSDKListener;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPGameInfo;
import global.sud.op.runtime.core.model.SUDOPGamePackageParams;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public abstract class BaseGameViewModel {

    private String TAG = "BaseGameViewModel";
    private Activity mActivity;

    public Handler handler = new Handler(Looper.getMainLooper());
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private SUDRTGameHandle mGameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;
    private boolean isGameStarted;
    private AudioManager _audioManager;
    private SUDRTFSTAPP mSUDRTFSTAPP;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    public MutableLiveData<String> gameStartedLiveData = new MutableLiveData<>();
    public MutableLiveData<ProgressModel> progressLiveData = new MutableLiveData<>();
    private SUDOPGamePathType pathType;
    private String manifestJson;
    public SUDOPWrappedClient sudOPWrappedClient;
    private SUDOPGameTask sudOPGameTask;

    SeduExtendedClient seduExtendedClient;

    /**
     * 启动游戏
     *
     * @param activity       页面
     * @param gameId         游戏id
     * @param gameUrl        游戏包的url
     * @param gamePkgVersion 游戏包版本
     */
    public void switchGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion, SUDOPGamePathType pathType, String manifestJson) {
        mActivity = activity;
        if (Objects.equals(gameId, mGameId)) {
            return;
        }
        destroyGame();
        if (_audioManager == null) {
            _audioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        this.pathType = pathType;
        this.manifestJson = manifestJson;
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        login(activity, gameId, gameUrl, gamePkgVersion);
        onResume();
    }

    private void login(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed() || TextUtils.isEmpty(mGameId)) {
            logD("login end idDestroyed:" + activity.isDestroyed() + " gameId:" + mGameId);
            return;
        }
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                logD("login.getCode onSuccess gameId:" + gameId + " mGameId:" + mGameId);
                if (!gameId.equals(mGameId)) {
                    return;
                }
                initSdk(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailed(int retCode, String retMsg) {
                logE("login.getCode onFailed:(" + retCode + ")" + retMsg);
                toastMsg("getCode onFailed:(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLogin(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void initSdk(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        ISUDAPPD.d();
        ISUDAPPD.e(3);
        SUDOP.initSDK(activity.getApplicationContext(), getAppId(), getAppKey(), new SUDOPInitSDKListener() {
            @Override
            public void onSuccess() {
                auth(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                logE("SUDOP.initSDK fail(" + retCode + ")" + retMsg);
                toastMsg("SUDOP.initSDK fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void auth(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        SUDOP.auth(code, new SUDOPAuthListener() {
            @Override
            public void onSuccess() {
                loadGame(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                logE("SUDOP.auth fail(" + retCode + ")" + retMsg);
                toastMsg("SUDOP.auth fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void loadGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
//        if (TextUtils.isEmpty(mGameId)) {
//            return;
//        }
        if (pathType == SUDOPGamePathType.GAME_ID) {
            sudOPGameTask = SUDOP.startGame(gameId, getSUDOPStartGameListener(activity, gameId, gameUrl, gamePkgVersion));
            return;
        }
        if (pathType == SUDOPGamePathType.DIR) {
            SUDOPGamePackageParams params = new SUDOPGamePackageParams();
            params.version = gamePkgVersion;
            params.appGameID = gameId;
            sudOPGameTask = SUDOP.startGameByDirectoryPath(gameUrl, params, getSUDOPStartGameListener(activity, gameId, gameUrl, gamePkgVersion));
            return;
        }
        if (pathType == SUDOPGamePathType.SIGNATURE) {
            sudOPGameTask = SUDOP.startGameBySignature(gameUrl, getSUDOPStartGameListener(activity, gameId, gameUrl, gamePkgVersion));
            return;
        }
    }

    private SUDOPStartGameListener getSUDOPStartGameListener(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        return new SUDOPStartGameListener() {
            @Override
            public void onGameViewCreated(View var1){

            }

            @Override
            public void onProgress(int var1){

            }
            @Override
            public void onGamePkgDecrypt(GamePkgDecryptHandle handle, String filePath) {
            }

            @Override
            public void onCreated(SUDRTGameHandle handle, SUDOPGameInfo gameInfo) {
                logD("getSUDOPStartGameListener.onCreated， gameId:" + gameId + " mGameId:" + mGameId);
                if (!gameId.equals(mGameId)) {
                    return;
                }
                if (gameInfo != null) {
                    if ("landscape".equals(gameInfo.deviceOrientation)) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else if ("portrait".equals(gameInfo.deviceOrientation)) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    } else if ("auto".equals(gameInfo.deviceOrientation)) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    }
                }

                setGameStartOptions(handle);
                if (sudOPWrappedClient != null) {
                    SUDOP.registerWrappedClient(handle, sudOPWrappedClient);
                }
                handle.registerExtendedClient("qg", new QgExtendedClient());
                seduExtendedClient = new SeduExtendedClient(activity);
                handle.registerExtendedClient("sedu", seduExtendedClient);
                mGameHandle = handle;
                onAddGameView(handle.getGameView());
                handle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                initListener(handle);
                setMute(isMute);
                handle.getGameAudioSession().setGameQueryAudioOptionsListener(_audioListener);
                handle.setGameDrawFrameListener(new SUDRTGameHandle.GameDrawFrameListener() {
                    @Override
                    public void onDrawFrame(long l) {
                        handle.setGameDrawFrameListener(null);
                    }
                });
            }

            @Override
            public void onSuccess(String gameId, SUDRTGameHandle gameHandle) {
                progressLiveData.setValue(new ProgressModel(3, 0, "load success", 100));
                _isGameInstalled = true;
                _changeGameState(_expectGameState);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                progressLiveData.setValue(new ProgressModel(2, retCode, retMsg, 0));
                toastMsg("loadGame fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        };
    }

    private void setGameStartOptions(SUDRTGameHandle handle) {
        Bundle bundle = new Bundle();
        if (pathType != null && pathType == SUDOPGamePathType.DIR) {
            bundle.putString(SUDRTGamePackageManager.KEY_PACKAGE_CONTENT_PATH, mGameUrl);
        }
//        bundle.putBoolean(SUDRTGameHandle.KEY_GAME_DEBUG_OPTION_ENABLE_DEBUGGER, true);
//        bundle.putBoolean(SUDRTGameHandle.KEY_GAME_DEBUG_OPTION_ENABLE_V_CONSOLE, true);
        if (manifestJson != null) {
            bundle.putString(SUDRTGameHandle.KEY_GAME_START_OPTIONS_CUSTOM_CONFIG, manifestJson);
        }
        handle.setGameStartOptions(mGameId, bundle);
    }

    private final SUDRTGameAudioSession.GameQueryAudioOptionsListener _audioListener = new SUDRTGameAudioSession.GameQueryAudioOptionsListener() {
        @Override
        public void onQueryAudioOptions(SUDRTGameAudioSession.GameQueryAudioOptionsHandle gameQueryAudioOptionsHandle, Bundle bundle) {
            // bundle 中参数
            // bundle.getBoolean(SUDRTGameAudioSession.KEY_AUDIO_MIX_WITH_OTHER); 是否用扬声器播放，true 默认输出设备优先级：耳机 > 蓝牙 > 扬声器；false 用听筒播放
            // bundle.getBoolean(SUDRTGameAudioSession.KEY_AUDIO_SPEAKER_ON); 音频是否支持与其他音频混播（包含其他应用、其他游戏实例的音频）
            if (afChangeListener == null) {
                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        // 不自动暂停音频
                    }
                };
                _audioManager.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                );
            }
        }
    };

    private void initListener(SUDRTGameHandle handle) {
        handle.setGameLoadSubpackageListener(new SUDRTGameHandle.GameLoadSubpackageListener() {
            /**
             * 当小游戏请求加载分包时回调
             *
             * @param handle 通过此 handle<br/>把下载并安装分包的结果返回给小游戏
             * @param root   根据该值计算分包的 URL
             */
            @Override
            public void onLoadSubpackage(SUDRTGameHandle.GameLoadSubpackageHandle handle, String name, String root) {
                logD("onLoadSubpackage:" + root);
                if ("SubJs/".equals(root) || "SubRes/".equals(root)) {
                    handle.success("/sdcard/Download/api-subpackage/subpackages/");
                } else {
                    handle.success(null);
                }
            }
        });
        handle.setGameQueryPermissionListener(new SUDRTGameHandle.GameQueryPermissionListener() {
            @Override
            public void onQueryPermission(SUDRTGameHandle.GameQueryPermissionHandle handle, String permission, String appId) {
                logD("getSUDOPStartGameListener.onQueryPermission permission:" + permission + " appId:" + appId);
                handle.complete(permission, true);
            }
        });
    }

    public void onStart() {
        _changeGameState(SUDRTGameHandle.GAME_STATE_RUNNING);
    }

    public void onResume() {
        _changeGameState(SUDRTGameHandle.GAME_STATE_PLAYING);
    }

    public void onPause() {
        _changeGameState(SUDRTGameHandle.GAME_STATE_RUNNING);
    }

    public void onStop() {
        _changeGameState(SUDRTGameHandle.GAME_STATE_WAITING);
    }

    /**
     * 销毁游戏
     */
    public void destroyGame() {
        logD("destroyGameGame gameId:" + mGameId);
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        if (mGameHandle != null) {
            mGameHandle.destroy();
            seduExtendedClient.destroy();
        }
        onRemoveGameView();
        mSUDRTFSTAPP = null;
        mGameId = null;
        mGameUrl = null;
        mGamePkgVersion = null;
        mGameHandle = null;
        seduExtendedClient = null;
        _isGameStateChanging = false;
        _currentGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
        _expectGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
        _isGameInstalled = false;
        isMute = false;
        isGameStarted = false;
        handler.removeCallbacksAndMessages(null);
        if (afChangeListener != null) {
            _audioManager.abandonAudioFocus(afChangeListener);
            afChangeListener = null;
        }
        manifestJson = null;
        if (sudOPGameTask != null) {
            sudOPGameTask.destroy();
            sudOPGameTask = null;
            SUDOP.uninitSDK(new SUDOPUninitSDKListener() {
                @Override
                public void onSuccess() {
                    // SDK 反初始化成功
                    Log.i("SUDOP", "SDK uninitialized successfully.");
                }

                @Override
                public void onFailure(int retCode, String retMsg) {
                    // 反初始化失败
                    Log.e("SUDOP", "SDK uninit failed: " + retMsg);
                }
            });
        }

    }

    private final SUDRTGameHandle.GameStateChangeListener _gameStateListener = new SUDRTGameHandle.GameStateChangeListener() {
        @Override
        public void preStateChange(int fromState, int state) {
        }

        @Override
        public void onStateChanged(int fromState, int state) {
            logD("状态变化 gameId:" + mGameId + " 状态为：" + getStringState(state));
            if (!isGameStarted && state == SUDRTGameHandle.GAME_STATE_PLAYING) {
                isGameStarted = true;
                gameStartedLiveData.setValue(null);
            }
            _currentGameState = state;
            _isGameStateChanging = false;
            _changeGameState(_expectGameState);
        }

        private String getStringState(int state) {
            switch (state) {
                case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                    return "UNAVAILABLE";
                case SUDRTGameHandle.GAME_STATE_WAITING:
                    return "WAITING";
                case SUDRTGameHandle.GAME_STATE_RUNNING:
                    return "RUNNING";
                case SUDRTGameHandle.GAME_STATE_PLAYING:
                    return "PLAYING";
                default:
                    return "UNKNOW:" + state;
            }
        }

        @Override
        public void onFailure(int fromState, int toState, Throwable error) {
            logE("game state change failed:" + " from=" + fromState + " to=" + toState + " error=" + error.getMessage());
        }
    };

    private void _changeGameState(int newState) {
        _expectGameState = newState;
        if (mGameHandle == null || !_isGameInstalled || _isGameStateChanging) {
            return;
        }
        logD("_changeGameState success: _currentGameState " + _currentGameState + " to " + newState);
        switch (_currentGameState) {
            case SUDRTGameHandle.GAME_STATE_UNAVAILABLE: {
                switch (newState) {
                    case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                        break;
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        break;
                }
                break;
            }
            case SUDRTGameHandle.GAME_STATE_WAITING: {
                switch (newState) {
                    case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                        _isGameStateChanging = true;
                        mGameHandle.destroy();
                        break;
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                        break;
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.start(null);
                        break;
                }
                break;
            }
            case SUDRTGameHandle.GAME_STATE_RUNNING: {
                switch (newState) {
                    case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                        _isGameStateChanging = true;
                        mGameHandle.stop(null);
                        break;
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                        break;
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.play();
                        break;
                }
                break;
            }
            case SUDRTGameHandle.GAME_STATE_PLAYING: {
                switch (newState) {
                    case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                        _isGameStateChanging = true;
                        mGameHandle.pause();
                        break;
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        break;
                }
                break;
            }
            default: {
                logE("_changeGameState fail: _currentGameState " + _currentGameState + " to " + newState);
            }
        }
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
        if (mGameHandle != null) {
            SUDRTGameAudioSession gameAudioSession = mGameHandle.getGameAudioSession();
            if (gameAudioSession != null) {
                gameAudioSession.mute(isMute);
            }
        }
    }

    public void onRequestRecordPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        seduExtendedClient.onRequestRecordPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onKeyDown(int keyCode) {
        seduExtendedClient.invokeKeyDownCallback(keyCode);
    }

    /**
     * 向接入方服务器获取code
     * Get the code from the integration party server.
     */
    protected abstract void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener);

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the integration party).
     *
     * @return 返回用户id
     * Returns the user ID.
     */
    protected abstract String getUserId();

    protected abstract String getNickName();

    protected abstract String getAvatar();

    /**
     * 设置游戏所用的appId
     * Set the appId used by the game.
     *
     * @return 返回游戏服务appId
     * Returns the game service appId.
     */
    protected abstract String getAppId();

    /**
     * 设置游戏所用的appKey
     * Set the appKey used by the game.
     *
     * @return 返回游戏服务appKey
     * Returns the game service appKey.
     */
    protected abstract String getAppKey();

    /**
     * 将游戏View添加到页面中
     * Add the game view to the page.
     */
    protected abstract void onAddGameView(View gameView);

    /**
     * 将页面中的游戏View移除
     * Remove the game view from the page.
     */
    protected abstract void onRemoveGameView();

    /**
     * 游戏login(getCode)监听
     * Game login (getCode) listener
     */
    public interface GameGetCodeListener {
        void onSuccess(String code);

        void onFailed(int retCode, String retMsg);
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
    }

    private void logW(String msg) {
        Log.w(TAG, msg);
    }

    private void logE(String msg) {
        Log.e(TAG, msg);
    }

    private void toastMsg(String msg) {
        if (mActivity != null) {
            Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static class ProgressModel {
        public int stage; // 阶段：start=1,loading=2,end=3
        public int retCode; // 错误码：0成功
        public String retMsg; // 错误信息
        public int progress; // 进度：[0, 100]

        public ProgressModel(int stage, int retCode, String retMsg, int progress) {
            this.stage = stage;
            this.retCode = retCode;
            this.retMsg = retMsg;
            this.progress = progress;
        }
    }

}
