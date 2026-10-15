/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package global.sud.gi.SUDGIWrapper.gi.decorator;

import global.sud.gi.SUDGIWrapper.gi.state.GIMGState;
import global.sud.gi.SUDGIWrapper.gi.utils.GIFSMStateHandleUtils;
import global.sud.gi.SUDGIWrapper.gi.utils.GIJsonUtils;
import global.sud.gi.core.ISUDFSMMG;
import global.sud.gi.core.ISUDFSMStateHandle;

/**
 * ISUDFSMMG 游戏调APP回调装饰类
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISUDFSMMG.html
 */
public class GIFSMMGDecorator implements ISUDFSMMG {

    // 回调
    private GIFSMMGListener sudFSMMGListener;

    // 数据状态封装
    private final GIFSMMGCache sudFSMMGCache = new GIFSMMGCache();

    /**
     * 设置回调
     *
     * @param listener 监听器
     */
    public void setGIFSMMGListener(GIFSMMGListener listener) {
        sudFSMMGListener = listener;
    }

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameLog(String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLog(dataJson);
        }
    }

    /**
     * 游戏加载进度
     *
     * @param stage    阶段：start=1,loading=2,end=3
     * @param retCode  错误码：0成功
     * @param progress 进度：[0, 100]
     */
    @Override
    public void onGameLoadingProgress(int stage, int retCode, int progress) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLoadingProgress(stage, retCode, progress);
        }
    }

    /**
     * 游戏开始
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameStarted() {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameStarted();
        }
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameDestroyed();
        }
    }

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    @Override
    public void onExpireCode(ISUDFSMStateHandle handle, String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onExpireCode(handle, dataJson);
        }
    }

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     */
    @Override
    public void onGetGameViewInfo(ISUDFSMStateHandle handle, String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameViewInfo(handle, dataJson);
        }
    }

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    @Override
    public void onGetGameCfg(ISUDFSMStateHandle handle, String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameCfg(handle, dataJson);
        }
    }

    /**
     * 游戏状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onGameStateChange(ISUDFSMStateHandle handle, String state, String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onGameStateChange(handle, state, dataJson)) {
            return;
        }
        switch (state) {
            case GIMGState.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                GIMGState.MGCommonPublicMessage mgCommonPublicMessage = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPublicMessage.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case GIMGState.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                GIMGState.MGCommonKeyWordToHit mgCommonKeyWordToHit = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                GIMGState.MGCommonGameSettle mgCommonGameSettle = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSettle.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                GIMGState.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                GIMGState.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                GIMGState.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                GIMGState.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                GIMGState.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                GIMGState.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case GIMGState.MG_COMMON_GAME_STATE: // 10. 游戏状态
                GIMGState.MGCommonGameState mgCommonGameState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                GIMGState.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                GIMGState.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                GIMGState.MGCommonGameSoundList mgCommonGameSoundList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSoundList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                GIMGState.MGCommonGameSound mgCommonGameSound = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSound.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case GIMGState.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                GIMGState.MGCommonGameBgMusicState mgCommonGameBgMusicState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                GIMGState.MGCommonGameSoundState mgCommonGameSoundState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSoundState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                GIMGState.MGCommonGameASR mgCommonGameASR = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameASR.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case GIMGState.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                GIMGState.MGCommonSelfMicrophone mgCommonSelfMicrophone = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case GIMGState.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                GIMGState.MGCommonSelfHeadphone mgCommonSelfHeadphone = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case GIMGState.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                GIMGState.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case GIMGState.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                GIMGState.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
                }
                break;
            case GIMGState.MG_COMMON_GAME_NETWORK_STATE: // 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
                GIMGState.MGCommonGameNetworkState mgCommonGameNetworkState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameNetworkState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameNetworkState(handle, mgCommonGameNetworkState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_GET_SCORE: // 23. 游戏通知app获取积分
                GIMGState.MGCommonGameGetScore mgCommonGameScore = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameGetScore.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameGetScore(handle, mgCommonGameScore);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SET_SCORE: // 24. 游戏通知app带入积分
                GIMGState.MGCommonGameSetScore mgCommonGameSetScore = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSetScore.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSetScore(handle, mgCommonGameSetScore);
                }
                break;
            case GIMGState.MG_COMMON_GAME_CREATE_ORDER: // 25. 创建订单
                GIMGState.MGCommonGameCreateOrder mgCommonGameCreateOrder = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameCreateOrder.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameCreateOrder(handle, mgCommonGameCreateOrder);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_ROLE_ID: // 26. 游戏通知app玩家角色(仅对狼人杀有效)
                GIMGState.MGCommonPlayerRoleId mgCommonPlayerRoleId = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerRoleId.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPlayerRoleId(handle, mgCommonPlayerRoleId);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_POOP: // 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
                GIMGState.MGCommonSelfClickPoop mgCommonSelfClickPoop = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickPoop.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickPoop(handle, mgCommonSelfClickPoop);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_GOOD: // 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
                GIMGState.MGCommonSelfClickGood mgCommonSelfClickGood = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickGood.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGood(handle, mgCommonSelfClickGood);
                }
                break;
            case GIMGState.MG_COMMON_GAME_FPS: // 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
                GIMGState.MGCommonGameFps mgCommonGameFps = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameFps.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameFps(handle, mgCommonGameFps);
                }
                break;
            case GIMGState.MG_COMMON_ALERT: // 30. 游戏通知app游戏弹框
                GIMGState.MGCommonAlert mgCommonAlert = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonAlert.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAlert(handle, mgCommonAlert);
                }
                break;
            case GIMGState.MG_COMMON_WORST_TEAMMATE: // 31. 游戏通知app最坑队友（只支持友尽闯关）
                GIMGState.MGCommonWorstTeammate mgCommonWorstTeammate = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonWorstTeammate.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonWorstTeammate(handle, mgCommonWorstTeammate);
                }
                break;
            case GIMGState.MG_COMMON_GAME_OVER_TIP: // 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
                GIMGState.MGCommonGameOverTip mgCommonGameOverTip = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameOverTip.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameOverTip(handle, mgCommonGameOverTip);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_COLOR: // 33. 游戏通知app玩家颜色（只支持友尽闯关）
                GIMGState.MGCommonGamePlayerColor mgCommonGamePlayerColor = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerColor.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerColor(handle, mgCommonGamePlayerColor);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_ICON_POSITION: // 34. 游戏通知app玩家头像的坐标（只支持ludo）
                GIMGState.MGCommonGamePlayerIconPosition mgCommonGamePlayerIconPosition = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerIconPosition.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerIconPosition(handle, mgCommonGamePlayerIconPosition);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_EXIT_GAME_BTN: // 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
                GIMGState.MGCommonSelfClickExitGameBtn mgCommonSelfClickExitGameBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickExitGameBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickExitGameBtn(handle, mgCommonSelfClickExitGameBtn);
                }
                break;
            case GIMGState.MG_COMMON_GAME_IS_APP_CHIP: // 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
                GIMGState.MGCommonGameIsAppChip mgCommonGameIsAppChip = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameIsAppChip.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameIsAppChip(handle, mgCommonGameIsAppChip);
                }
                break;
            case GIMGState.MG_COMMON_GAME_RULE: // 37. 游戏通知app当前游戏的设置信息
                GIMGState.MGCommonGameRule mgCommonGameRule = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameRule.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameRule(handle, mgCommonGameRule);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SETTINGS: // 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
                GIMGState.MGCommonGameSettings mgCommonGameSettings = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSettings.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettings(handle, mgCommonGameSettings);
                }
                break;
            case GIMGState.MG_COMMON_GAME_MONEY_NOT_ENOUGH: // 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
                GIMGState.MGCommonGameMoneyNotEnough mgCommonGameMoneyNotEnough = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameMoneyNotEnough.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameMoneyNotEnough(handle, mgCommonGameMoneyNotEnough);
                }
                break;
            case GIMGState.MG_COMMON_GAME_UI_CUSTOM_CONFIG: // 40. 游戏通知app下发定制ui配置表（只支持ludo）
                GIMGState.MGCommonGameUiCustomConfig mgCommonGameUiCustomConfig = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameUiCustomConfig.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameUiCustomConfig(handle, mgCommonGameUiCustomConfig);
                }
                break;
            case GIMGState.MG_COMMON_SET_CLICK_RECT: // 41. 设置app提供给游戏可点击区域(赛车)
                GIMGState.MGCommonSetClickRect mgCommonSetClickRect = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSetClickRect.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSetClickRect(handle, mgCommonSetClickRect);
                }
                break;
            case GIMGState.MG_COMMON_USERS_INFO: // 42. 通知app提供对应uids列表玩家的数据(赛车)
                GIMGState.MGCommonUsersInfo mgCommonUsersInfo = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonUsersInfo.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonUsersInfo(handle, mgCommonUsersInfo);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PREPARE_FINISH: // 43. 通知app游戏前期准备完成(赛车)
                GIMGState.MGCommonGamePrepareFinish mgCommonGamePrepareFinish = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePrepareFinish.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePrepareFinish(handle, mgCommonGamePrepareFinish);
                }
                break;
            case GIMGState.MG_COMMON_SHOW_GAME_SCENE: // 44. 通知app游戏主界面已显示(赛车)
                GIMGState.MGCommonShowGameScene mgCommonShowGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonShowGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonShowGameScene(handle, mgCommonShowGameScene);
                }
                break;
            case GIMGState.MG_COMMON_HIDE_GAME_SCENE: // 45. 通知app游戏主界面已隐藏(赛车)
                GIMGState.MGCommonHideGameScene mgCommonHideGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonHideGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonHideGameScene(handle, mgCommonHideGameScene);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_GOLD_BTN: // 46. 通知app点击了游戏的金币按钮(赛车)
                GIMGState.MGCommonSelfClickGoldBtn mgCommonSelfClickGoldBtn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickGoldBtn.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGoldBtn(handle, mgCommonSelfClickGoldBtn);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PIECE_ARRIVE_END: // 47. 通知app棋子到达终点(ludo)
                GIMGState.MGCommonGamePieceArriveEnd mgCommonGamePieceArriveEnd = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePieceArriveEnd.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePieceArriveEnd(handle, mgCommonGamePieceArriveEnd);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_MANAGED_STATE: // 48. 通知app玩家是否托管
                GIMGState.MGCommonGamePlayerManagedState mgCommonGamePlayerManagedState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerManagedState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerManagedState(handle, mgCommonGamePlayerManagedState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_SEND_BURST_WORD: // 49. 游戏向app发送爆词
                GIMGState.MGCommonGameSendBurstWord mgCommonGameSendBurstWord = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameSendBurstWord.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSendBurstWord(handle, mgCommonGameSendBurstWord);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_RANKS: // 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
                GIMGState.MGCommonGamePlayerRanks mgCommonGamePlayerRanks = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerRanks.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerRanks(handle, mgCommonGamePlayerRanks);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_PAIR_SINGULAR: // 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
                GIMGState.MGCommonGamePlayerPairSingular mgCommonGamePlayerPairSingular = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerPairSingular.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPairSingular(handle, mgCommonGamePlayerPairSingular);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_MONOPOLY_CARDS: // 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
                GIMGState.MGCommonGamePlayerMonopolyCards mgCommonGamePlayerMonopolyCards = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerMonopolyCards.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMonopolyCards(handle, mgCommonGamePlayerMonopolyCards);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_SCORES: // 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
                GIMGState.MGCommonGamePlayerScores mgCommonGamePlayerScores = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerScores.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerScores(handle, mgCommonGamePlayerScores);
                }
                break;
            case GIMGState.MG_COMMON_DESTROY_GAME_SCENE: // 54. 游戏通知app销毁游戏（只支持部分概率类游戏）
                GIMGState.MGCommonDestroyGameScene mgCommonDestroyGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonDestroyGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonDestroyGameScene(handle, mgCommonDestroyGameScene);
                }
                break;
            case GIMGState.MG_COMMON_GAME_BILLIARDS_HIT_STATE: // 55. 游戏通知app击球状态（只支持桌球）
                GIMGState.MGCommonGameBilliardsHitState mgCommonGameBilliardsHitState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameBilliardsHitState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBilliardsHitState(handle, mgCommonGameBilliardsHitState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_PROPS_CARDS: // 56. 游戏向app发送获取玩家持有的指定点数道具卡（只支持飞行棋）
                GIMGState.MGCommonGamePlayerPropsCards mgCommonGamePlayerPropsCards = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerPropsCards.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPropsCards(handle, mgCommonGamePlayerPropsCards);
                }
                break;
            case GIMGState.MG_COMMON_GAME_INFO_X: // 57. 游戏向app发送获游戏通用数据
                GIMGState.MGCommonGameInfoX mgCommonGameInfoX = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameInfoX.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameInfoX(handle, mgCommonGameInfoX);
                }
                break;
            case GIMGState.MG_COMMON_AI_MODEL_MESSAGE: // 通知app开启ai大模型
                GIMGState.MGCommonAiModelMessage mgCommonAiModelMessage = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonAiModelMessage.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiModelMessage(handle, mgCommonAiModelMessage);
                }
                break;
            case GIMGState.MG_COMMON_AI_MESSAGE: // 通知app ai消息
                GIMGState.MGCommonAiMessage mgCommonAiMessage = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonAiMessage.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiMessage(handle, mgCommonAiMessage);
                }
                break;
            case GIMGState.MG_COMMON_AI_LARGE_SCALE_MODEL_MSG: // 64. 通知app ai大模型消息
                GIMGState.MGCommonAiLargeScaleModelMsg mgCommonAiLargeScaleModelMsg = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonAiLargeScaleModelMsg.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiLargeScaleModelMsg(handle, mgCommonAiLargeScaleModelMsg);
                }
                break;
            case GIMGState.MG_COMMON_GAME_PLAYER_MIC_STATE: // 65. 通知app可以开始推送麦克说话状态
                GIMGState.MGCommonGamePlayerMicState mgCommonGamePlayerMicState = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGamePlayerMicState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMicState(handle, mgCommonGamePlayerMicState);
                }
                break;
            case GIMGState.MG_COMMON_GAME_DISCO_ACTION: // 1. 元宇宙砂砂舞指令回调
                GIMGState.MGCommonGameDiscoAction mgCommonGameDiscoAction = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameDiscoAction.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoAction(handle, mgCommonGameDiscoAction);
                }
                break;
            case GIMGState.MG_COMMON_GAME_DISCO_ACTION_END: // 2. 元宇宙砂砂舞指令动作结束通知
                GIMGState.MGCommonGameDiscoActionEnd mgCommonGameDiscoActionEnd = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameDiscoActionEnd.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoActionEnd(handle, mgCommonGameDiscoActionEnd);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_CONFIG: // 1. 礼物配置文件(火箭)
                GIMGState.MGCustomRocketConfig mgCustomRocketConfig = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketConfig.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketConfig(handle, mgCustomRocketConfig);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_MODEL_LIST: // 2. 拥有模型列表(火箭)
                GIMGState.MGCustomRocketModelList mgCustomRocketModelList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketModelList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketModelList(handle, mgCustomRocketModelList);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_COMPONENT_LIST: // 3. 拥有组件列表(火箭)
                GIMGState.MGCustomRocketComponentList mgCustomRocketComponentList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketComponentList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketComponentList(handle, mgCustomRocketComponentList);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_USER_INFO: // 4. 获取用户信息(火箭)
                GIMGState.MGCustomRocketUserInfo mgCustomRocketUserInfo = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketUserInfo.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserInfo(handle, mgCustomRocketUserInfo);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_ORDER_RECORD_LIST: // 5. 订单记录列表(火箭)
                GIMGState.MGCustomRocketOrderRecordList mgCustomRocketOrderRecordList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketOrderRecordList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketOrderRecordList(handle, mgCustomRocketOrderRecordList);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_ROOM_RECORD_LIST: // 6. 展馆内列表(火箭)
                GIMGState.MGCustomRocketRoomRecordList mgCustomRocketRoomRecordList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketRoomRecordList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketRoomRecordList(handle, mgCustomRocketRoomRecordList);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_USER_RECORD_LIST: // 7. 展馆内玩家送出记录(火箭)
                GIMGState.MGCustomRocketUserRecordList mgCustomRocketUserRecordList = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketUserRecordList.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserRecordList(handle, mgCustomRocketUserRecordList);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_SET_DEFAULT_MODEL: // 8. 设置默认模型(火箭)
                GIMGState.MGCustomRocketSetDefaultModel mgCustomRocketSetDefaultSeat = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketSetDefaultModel.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetDefaultModel(handle, mgCustomRocketSetDefaultSeat);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE: // 9. 动态计算一键发送价格(火箭)
                GIMGState.MGCustomRocketDynamicFirePrice mgCustomRocketDynamicFirePrice = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketDynamicFirePrice.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketDynamicFirePrice(handle, mgCustomRocketDynamicFirePrice);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_FIRE_MODEL: // 10. 一键发送(火箭)
                GIMGState.MGCustomRocketFireModel mGCustomRocketFireModel = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketFireModel.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFireModel(handle, mGCustomRocketFireModel);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_CREATE_MODEL: // 11. 新组装模型(火箭)
                GIMGState.MGCustomRocketCreateModel mgCustomRocketCreateModel = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketCreateModel.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketCreateModel(handle, mgCustomRocketCreateModel);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_REPLACE_COMPONENT: // 12. 模型更换组件(火箭)
                GIMGState.MGCustomRocketReplaceComponent mgCustomRocketReplaceComponent = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketReplaceComponent.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketReplaceComponent(handle, mgCustomRocketReplaceComponent);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_BUY_COMPONENT: // 13. 购买组件(火箭)
                GIMGState.MGCustomRocketBuyComponent mgCustomRocketBuyComponent = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketBuyComponent.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketBuyComponent(handle, mgCustomRocketBuyComponent);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_START: // 14. 播放效果开始(火箭)
                GIMGState.MGCustomRocketPlayEffectStart mgCustomRocketPlayEffectStart = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketPlayEffectStart.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectStart(handle, mgCustomRocketPlayEffectStart);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_FINISH: // 15. 播放效果完成(火箭)
                GIMGState.MGCustomRocketPlayEffectFinish mgCustomRocketPlayEffectFinish = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketPlayEffectFinish.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectFinish(handle, mgCustomRocketPlayEffectFinish);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_VERIFY_SIGN: // 16. 验证签名合规(火箭)
                GIMGState.MGCustomRocketVerifySign mgCustomRocketVerifySign = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketVerifySign.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketVerifySign(handle, mgCustomRocketVerifySign);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_UPLOAD_MODEL_ICON: // 17. 上传icon(火箭)
                GIMGState.MGCustomRocketUploadModelIcon mgCustomRocketUploadModelIcon = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketUploadModelIcon.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUploadModelIcon(handle, mgCustomRocketUploadModelIcon);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_PREPARE_FINISH: // 18. 前期准备完成(火箭)
                GIMGState.MGCustomRocketPrepareFinish mgCustomRocketPrepareFinish = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketPrepareFinish.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPrepareFinish(handle, mgCustomRocketPrepareFinish);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_SHOW_GAME_SCENE: // 19. 火箭主界面已显示(火箭)
                GIMGState.MGCustomRocketShowGameScene mgCustomRocketShowGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketShowGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketShowGameScene(handle, mgCustomRocketShowGameScene);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_HIDE_GAME_SCENE: // 20. 火箭主界面已隐藏(火箭)
                GIMGState.MGCustomRocketHideGameScene mgCustomRocketHideGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketHideGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketHideGameScene(handle, mgCustomRocketHideGameScene);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_CLICK_LOCK_COMPONENT: // 21. 点击锁住组件(火箭)
                GIMGState.MGCustomRocketClickLockComponent mgCustomRocketClickLockComponent = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketClickLockComponent.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketClickLockComponent(handle, mgCustomRocketClickLockComponent);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_FLY_CLICK: // 22. 火箭效果飞行点击(火箭)
                GIMGState.MGCustomRocketFlyClick mgCustomRocketFlyClick = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketFlyClick.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyClick(handle, mgCustomRocketFlyClick);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_FLY_END: // 23. 火箭效果飞行结束(火箭)
                GIMGState.MGCustomRocketFlyEnd mgCustomRocketFlyEnd = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketFlyEnd.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyEnd(handle, mgCustomRocketFlyEnd);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_SET_CLICK_RECT: // 24. 设置点击区域(火箭)
                GIMGState.MGCustomRocketSetClickRect mgCustomRocketSetClickRect = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketSetClickRect.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetClickRect(handle, mgCustomRocketSetClickRect);
                }
                break;
            case GIMGState.MG_CUSTOM_ROCKET_SAVE_SIGN_COLOR: // 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名 模型
                GIMGState.MGCustomRocketSaveSignColor mgCustomRocketSaveSignColor = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomRocketSaveSignColor.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSaveSignColor(handle, mgCustomRocketSaveSignColor);
                }
                break;
            case GIMGState.MG_BASEBALL_DEFUALT_STATE: // 1. 设置界面默认状态(棒球)
                GIMGState.MGBaseballDefaultState mgBaseballDefaultState = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballDefaultState.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballDefaultState(handle, mgBaseballDefaultState);
                }
                break;
            case GIMGState.MG_BASEBALL_PREPARE_FINISH: // 2. 前期准备完成(棒球)
                GIMGState.MGBaseballPrepareFinish mgBaseballPrepareFinish = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballPrepareFinish.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballPrepareFinish(handle, mgBaseballPrepareFinish);
                }
                break;
            case GIMGState.MG_BASEBALL_SHOW_GAME_SCENE: // 3. 主界面已显示(棒球)
                GIMGState.MGBaseballShowGameScene mgBaseballShowGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballShowGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballShowGameScene(handle, mgBaseballShowGameScene);
                }
                break;
            case GIMGState.MG_BASEBALL_HIDE_GAME_SCENE: // 4. 主界面已隐藏(棒球)
                GIMGState.MGBaseballHideGameScene mgBaseballHideGameScene = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballHideGameScene.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballHideGameScene(handle, mgBaseballHideGameScene);
                }
                break;
            case GIMGState.MG_BASEBALL_RANKING: // 5. 查询排行榜数据(棒球)
                GIMGState.MGBaseballRanking mgBaseballRanking = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballRanking.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRanking(handle, mgBaseballRanking);
                }
                break;
            case GIMGState.MG_BASEBALL_MY_RANKING: // 6. 查询我的排名(棒球)
                GIMGState.MGBaseballMyRanking mgBaseballMyRanking = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballMyRanking.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballMyRanking(handle, mgBaseballMyRanking);
                }
                break;
            case GIMGState.MG_BASEBALL_RANGE_INFO: // 7. 查询当前距离我的前后玩家数据(棒球)
                GIMGState.MGBaseballRangeInfo mgBaseballRangeInfo = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballRangeInfo.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRangeInfo(handle, mgBaseballRangeInfo);
                }
                break;
            case GIMGState.MG_BASEBALL_SET_CLICK_RECT: // 8. 设置app提供给游戏可点击区域(棒球)
                GIMGState.MGBaseballSetClickRect mgBaseballSetClickRect = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballSetClickRect.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSetClickRect(handle, mgBaseballSetClickRect);
                }
                break;
            case GIMGState.MG_BASEBALL_TEXT_CONFIG: // 9. 获取文本配置数据(棒球)
                GIMGState.MGBaseballTextConfig mgBaseballTextConfig = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballTextConfig.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballTextConfig(handle, mgBaseballTextConfig);
                }
                break;
            case GIMGState.MG_BASEBALL_SEND_DISTANCE: // 10. 球落地, 通知距离(棒球)
                GIMGState.MGBaseballSendDistance mgBaseballSendDistance = GIJsonUtils.fromJson(dataJson, GIMGState.MGBaseballSendDistance.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSendDistance(handle, mgBaseballSendDistance);
                }
                break;
            case GIMGState.MG_CUSTOM_CR_ROOM_INIT_DATA: // 1. 请求房间数据
                GIMGState.MGCustomCrRoomInitData mgCustomCrRoomInitData = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomCrRoomInitData.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrRoomInitData(handle, mgCustomCrRoomInitData);
                }
                break;
            case GIMGState.MG_CUSTOM_CR_CLICK_SEAT: // 2. 点击主播位或老板位通知
                GIMGState.MGCustomCrClickSeat mgCustomCrClickSeat = GIJsonUtils.fromJson(dataJson, GIMGState.MGCustomCrClickSeat.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrClickSeat(handle, mgCustomCrClickSeat);
                }
                break;
            case GIMGState.MG_HAPPY_GOAT_CHAT: // 1. 文本/语音聊天结果
                GIMGState.MGHappyGoatChat mgHappyGoatChat = GIJsonUtils.fromJson(dataJson, GIMGState.MGHappyGoatChat.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGHappyGoatChat(handle, mgHappyGoatChat);
                }
                break;
            default:
                GIFSMStateHandleUtils.handleSuccess(handle);
                break;
        }
    }

    /**
     * 游戏玩家状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param userId   用户id
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onPlayerStateChange(ISUDFSMStateHandle handle, String userId, String state, String dataJson) {
        GIFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onPlayerStateChange(handle, userId, state, dataJson)) {
            return;
        }
        switch (state) {
            case GIMGState.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                GIMGState.MGCommonPlayerIn mgCommonPlayerIn = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                GIMGState.MGCommonPlayerReady mgCommonPlayerReady = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                GIMGState.MGCommonPlayerCaptain mgCommonPlayerCaptain = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                GIMGState.MGCommonPlayerPlaying mgCommonPlayerPlaying = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                GIMGState.MGCommonPlayerOnline mgCommonPlayerOnline = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerOnline.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case GIMGState.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                GIMGState.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case GIMGState.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像
                GIMGState.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case GIMGState.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                GIMGState.MGCommonSelfDieStatus mgCommonSelfDieStatus = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case GIMGState.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                GIMGState.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case GIMGState.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                GIMGState.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case GIMGState.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                GIMGState.MGCommonGameCountdownTime mgCommonGameCountdownTime = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case GIMGState.MG_COMMON_SELF_OB_STATUS: // 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
                GIMGState.MGCommonSelfObStatus mgCommonSelfObStatus = GIJsonUtils.fromJson(dataJson, GIMGState.MGCommonSelfObStatus.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfObStatus(handle, userId, mgCommonSelfObStatus);
                }
                break;
            case GIMGState.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                GIMGState.MGDGSelecting mgdgSelecting = GIJsonUtils.fromJson(dataJson, GIMGState.MGDGSelecting.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case GIMGState.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                GIMGState.MGDGPainting mgdgPainting = GIJsonUtils.fromJson(dataJson, GIMGState.MGDGPainting.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case GIMGState.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                GIMGState.MGDGErroranswer mgdgErroranswer = GIJsonUtils.fromJson(dataJson, GIMGState.MGDGErroranswer.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case GIMGState.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                GIMGState.MGDGTotalscore mgdgTotalscore = GIJsonUtils.fromJson(dataJson, GIMGState.MGDGTotalscore.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case GIMGState.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                GIMGState.MGDGScore mgdgScore = GIJsonUtils.fromJson(dataJson, GIMGState.MGDGScore.class);
                if (listener == null) {
                    GIFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGScore(handle, userId, mgdgScore);
                }
                break;
            default:
                GIFSMStateHandleUtils.handleSuccess(handle);
                break;
        }
    }

    /** 获取队长userId */
    public String getCaptainUserId() {
        return sudFSMMGCache.getCaptainUserId();
    }

    // 返回该玩家是否正在游戏中
    public boolean playerIsPlaying(String userId) {
        return sudFSMMGCache.playerIsPlaying(userId);
    }

    // 返回该玩家是否已准备
    public boolean playerIsReady(String userId) {
        return sudFSMMGCache.playerIsReady(userId);
    }

    // 返回该玩家是否已加入了游戏
    public boolean playerIsIn(String userId) {
        return sudFSMMGCache.playerIsIn(userId);
    }

    // 获取当前游戏中的人数
    public int getPlayerInNumber() {
        return sudFSMMGCache.getPlayerInNumber();
    }

    // 是否数字炸弹
    public boolean isHitBomb() {
        return sudFSMMGCache.isHitBomb();
    }

    // 销毁游戏
    public void destroyMG() {
        sudFSMMGCache.destroyMG();
        sudFSMMGListener = null;
    }

    /**
     * 返回当前游戏的状态，数值参数{@link GIMGState.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGCache.getGameState();
    }

    /** 获取缓存的状态 */
    public GIFSMMGCache getGIFSMMGCache() {
        return sudFSMMGCache;
    }

}
