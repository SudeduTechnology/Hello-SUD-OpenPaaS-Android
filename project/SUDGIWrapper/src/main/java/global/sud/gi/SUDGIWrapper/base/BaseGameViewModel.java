package global.sud.gi.SUDGIWrapper.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.SUDOPGameTask;
import global.sud.op.runtime.core.SUDRTGameAudioSession;
import global.sud.op.runtime.core.SUDRTGameHandle;
import global.sud.op.runtime.core.listener.SUDOPAuthListener;
import global.sud.op.runtime.core.listener.SUDOPStartGameListener;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPGameInfo;
import global.sud.op.runtime.core.model.SUDOPGamePackageParams;
import global.sud.op.runtime.core.model.SUDOPGamePathType;
import global.sud.op.runtime.core.model.SUDOPPrivacyConfig;

public abstract class BaseGameViewModel {

    public Handler handler = new Handler(Looper.getMainLooper());
    protected String gameId;
    protected String gameUrl;
    private SUDRTGameHandle gameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = SUDRTGameHandle.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;
    private boolean isGameStarted;
    private AudioManager _audioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    public MutableLiveData<String> gameStartedLiveData = new MutableLiveData<>();
    public MutableLiveData<ProgressModel> progressLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> queryExitLiveData = new MutableLiveData<>();
    protected SUDOPGamePathType pathType;
    protected String manifestJson;
    private SUDOPWrappedClient sudOPWrappedClient;
    private SUDOPGameTask sudOPGameTask;

    /**
     * 启动游戏
     *
     * @param activity 页面
     * @param gameId   游戏id
     */
    public void switchGame(Activity activity, String gameId) {
        switchGame(activity, gameId, SUDOPGamePathType.GAME_ID, null, null, null);
    }

    /**
     * 启动游戏
     *
     * @param activity       页面
     * @param gameId         游戏id
     * @param gameUrl        游戏包的url
     * @param gamePkgVersion 游戏包版本
     */
    public void switchGame(Activity activity, String gameId, SUDOPGamePathType pathType, String gameUrl, String gamePkgVersion, String manifestJson) {
        if (pathType == null) {
            logE("pathType cannot be empty");
            toastMsg("pathType cannot be empty");
            return;
        }
        if (TextUtils.isEmpty(gameId)) {
            logE("gameId cannot be empty");
            toastMsg("gameId cannot be empty");
            return;
        }
        if (Objects.equals(gameId, this.gameId)) {
            return;
        }
        destroyGame();
        if (_audioManager == null) {
            _audioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        this.gameId = gameId;
        this.gameUrl = gameUrl;
        this.pathType = pathType;
        this.manifestJson = manifestJson;
        login(activity, gameId, gameUrl, gamePkgVersion);
        onResume();
    }

    private void login(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed() || TextUtils.isEmpty(this.gameId)) {
            return;
        }
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!gameId.equals(BaseGameViewModel.this.gameId)) {
                    return;
                }
                auth(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailed(int retCode, String retMsg) {
                logE("getCode onFailed:(" + retCode + ")" + retMsg);
                toastMsg("getCode onFailed:(" + retCode + ")" + retMsg);
                if (shouldRetryAfterFailure(retCode, retMsg)) {
                    delayLogin(activity, gameId, gameUrl, gamePkgVersion);
                }
            }
        });
    }

    private void delayLogin(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(this.gameId)) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void auth(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        SUDOP.auth(code, new SUDOPAuthListener() {
            @Override
            public void onSuccess() {
                loadGame(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                progressLiveData.setValue(new ProgressModel(1, retCode, retMsg, 0));
                logE("auth fail(" + retCode + ")" + retMsg);
                toastMsg("auth fail(" + retCode + ")" + retMsg);
                if (shouldRetryAfterFailure(retCode, retMsg)) {
                    delayLogin(activity, gameId, gameUrl, gamePkgVersion);
                }
            }
        });
    }

    private void loadGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(this.gameId)) {
            return;
        }
        if (pathType == SUDOPGamePathType.GAME_ID) {
            sudOPGameTask = SUDOP.startGame(gameId, getLanguage(activity), getSUDOPStartGameListener(activity, gameId, gameUrl, gamePkgVersion));
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
            public void onGameViewCreated(View view) {
                onAddGameView(view);
            }

            @Override
            public void onGamePkgDecrypt(GamePkgDecryptHandle handle, String filePath) {
            }

            @Override
            public void onCreated(SUDRTGameHandle handle, SUDOPGameInfo gameInfo) {
                if ("landscape".equals(gameInfo.deviceOrientation)) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else if ("portrait".equals(gameInfo.deviceOrientation)) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                } else if ("auto".equals(gameInfo.deviceOrientation)) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }

                if (sudOPWrappedClient != null) {
                    SUDOP.registerWrappedClient(handle, sudOPWrappedClient);
                }

                onGameHandleCreated(handle, gameInfo);

                gameHandle = handle;
                handle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                // 去除这个监听之后，可以让runtime具备中断音频的能力
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
                setMute(isMute);
            }

            @Override
            public void onProgress(int progress) {
                progressLiveData.setValue(new ProgressModel(2, 0, null, progress));
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                progressLiveData.setValue(new ProgressModel(2, retCode, retMsg, 0));
                toastMsg("loadGame fail(" + retCode + ")" + retMsg);
                if (shouldRetryAfterFailure(retCode, retMsg)) {
                    delayLogin(activity, gameId, gameUrl, gamePkgVersion);
                }
            }
        };
    }

    private final SUDRTGameAudioSession.GameQueryAudioOptionsListener _audioListener = new SUDRTGameAudioSession.GameQueryAudioOptionsListener() {
        @Override
        public void onQueryAudioOptions(SUDRTGameAudioSession.GameQueryAudioOptionsHandle handle, Bundle bundle) {
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
            handle.complete(true, null);
        }
    };

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
        if (TextUtils.isEmpty(gameId)) {
            return;
        }
        SUDRTGameHandle gameHandle = this.gameHandle;
        if (gameHandle != null) {
            this.gameHandle = null;
            gameHandle.destroy();
        }
        onRemoveGameView();
        gameId = null;
        gameUrl = null;
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
        }
    }

    private final SUDRTGameHandle.GameStateChangeListener _gameStateListener = new SUDRTGameHandle.GameStateChangeListener() {
        @Override
        public void preStateChange(int fromState, int state) {
        }

        @Override
        public void onStateChanged(int fromState, int state) {
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
        setAudioInterruption(newState != SUDRTGameHandle.GAME_STATE_PLAYING);
        if (gameHandle == null || !_isGameInstalled || _isGameStateChanging) {
            return;
        }
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
                        gameHandle.destroy();
                        break;
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                        break;
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        gameHandle.start(null);
                        break;
                }
                break;
            }
            case SUDRTGameHandle.GAME_STATE_RUNNING: {
                switch (newState) {
                    case SUDRTGameHandle.GAME_STATE_UNAVAILABLE:
                    case SUDRTGameHandle.GAME_STATE_WAITING:
                        _isGameStateChanging = true;
                        gameHandle.stop(null);
                        break;
                    case SUDRTGameHandle.GAME_STATE_RUNNING:
                        break;
                    case SUDRTGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        gameHandle.play();
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
                        gameHandle.pause();
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
        if (gameHandle != null) {
            SUDRTGameAudioSession gameAudioSession = gameHandle.getGameAudioSession();
            if (gameAudioSession != null) {
                gameAudioSession.mute(isMute);
            }
        }
    }

    /**
     * 设置游戏音频的临时中断状态，不改变用户的静音设置。
     *
     * @param interrupted true 中断音频，false 恢复音频
     */
    private void setAudioInterruption(boolean interrupted) {
        if (gameHandle != null && gameHandle.getGameAudioSession() != null) {
            gameHandle.getGameAudioSession().setAudioInterruption(interrupted);
        }
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
    public abstract String getUserId();

    /**
     * 设置游戏所用的appId
     * Set the appId used by the game.
     *
     * @return 返回游戏服务appId
     * Returns the game service appId.
     */
    public abstract String getAppId();

    /**
     * 设置游戏所用的appKey
     * Set the appKey used by the game.
     *
     * @return 返回游戏服务appKey
     * Returns the game service appKey.
     */
    public abstract String getAppKey();

    /** 返回可选隐私配置；默认不设置各广告 SDK 的隐私状态。 */
    public SUDOPPrivacyConfig getPrivacyConfig() {
        return null;
    }

    /** 返回游戏请求使用的语言；默认沿用 SDK 未指定语言的行为。 */
    protected String getLanguage(Activity activity) {
        return null;
    }

    /** Existing integrations retry automatically; subclasses may disable automatic retry. */
    protected boolean shouldRetryAfterFailure(int retCode, String retMsg) {
        return true;
    }

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
     * gameHandle对象创建完成
     * The gameHandle object has been created successfully.
     *
     * @param handle   gameHandle
     * @param gameInfo gameInfo
     */
    protected abstract void onGameHandleCreated(SUDRTGameHandle handle, SUDOPGameInfo gameInfo);

    protected abstract void logE(String msg);

    protected abstract void toastMsg(String msg);

    /**
     * 游戏login(getCode)监听
     * Game login (getCode) listener
     */
    public interface GameGetCodeListener {
        void onSuccess(String code);

        void onFailed(int retCode, String retMsg);
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

    public String getGameUrl() {
        return gameUrl;
    }

    public void setSUDOPWrappedClient(SUDOPWrappedClient sudOPWrappedClient) {
        this.sudOPWrappedClient = sudOPWrappedClient;
    }
}
