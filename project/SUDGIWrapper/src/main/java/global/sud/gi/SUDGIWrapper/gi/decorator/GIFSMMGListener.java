/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package global.sud.gi.SUDGIWrapper.gi.decorator;

import global.sud.gi.SUDGIWrapper.gi.state.GIMGState;
import global.sud.gi.SUDGIWrapper.gi.utils.GIFSMStateHandleUtils;
import global.sud.gi.core.ISUDFSMStateHandle;

/**
 * {@link GIFSMMGDecorator} 回调定义
 * {@link GIFSMMGDecorator} Callback definition.
 */
public interface GIFSMMGListener {

    /**
     * 游戏日志回调
     * 最低版本：v1.1.30.xx
     * <p>
     * Game log
     * Minimum version: v1.1.30.xx
     */
    default void onGameLog(String str) {
    }

    /**
     * 游戏加载进度回调
     * Game loading progress
     *
     * @param stage    阶段：start=1,loading=2,end=3
     *                 Stage: start=1, loading=2, end=3
     * @param retCode  错误码：0成功
     *                 Error code: 0 for success
     * @param progress 进度：[0, 100]
     *                 Progress: [0, 100]
     */
    default void onGameLoadingProgress(int stage, int retCode, int progress) {
    }

    /**
     * 游戏开始的回调
     * 最低版本：v1.1.30.xx
     * <p>
     * Callback for game start
     * Minimum version: v1.1.30.xx
     */
    void onGameStarted();

    /**
     * 游戏销毁的回调
     * 最低版本：v1.1.30.xx
     * <p>
     * Callback for game destruction
     * Minimum version: v1.1.30.xx
     */
    void onGameDestroyed();

    /**
     * Code过期的回调
     * APP接入方需要调用handle.success或handle.fail
     * <p>
     * Callback for expired code
     * The APP integration partner needs to call handle.success or handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    void onExpireCode(ISUDFSMStateHandle handle, String dataJson);

    /**
     * 获取游戏View信息的回调
     * APP接入方需要调用handle.success或handle.fail
     * <p>
     * Callback for obtaining game View information
     * The APP integration partner needs to call handle.success or handle.fail
     */
    void onGetGameViewInfo(ISUDFSMStateHandle handle, String dataJson);

    /**
     * 获取游戏配置的回调
     * APP接入方需要调用handle.success或handle.fail
     * <p>
     * Callback for obtaining game configuration
     * The APP integration partner needs to call handle.success or handle.fail
     */
    void onGetGameCfg(ISUDFSMStateHandle handle, String dataJson);

    // region 游戏回调APP 通用状态 English: Game callback to APP for general state.
    // 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStateGame.html
    // Reference documentation：https://docs.sud.tech/en-US/app/Client/MGFSM/CommonStateGame.html

    /**
     * 1.游戏公屏消息
     * 1. Public screen messages (modified)
     * mg_common_public_message
     */
    default void onGameMGCommonPublicMessage(ISUDFSMStateHandle handle, GIMGState.MGCommonPublicMessage model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 关键词状态
     * 2. Keyword
     * mg_common_key_word_to_hit
     */
    default void onGameMGCommonKeyWordToHit(ISUDFSMStateHandle handle, GIMGState.MGCommonKeyWordToHit model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 游戏结算状态
     * 3. Post-game
     * mg_common_game_settle
     */
    default void onGameMGCommonGameSettle(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSettle model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 加入游戏按钮点击状态
     * 4. Tapping the Join button
     * mg_common_self_click_join_btn
     */
    default void onGameMGCommonSelfClickJoinBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickJoinBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 取消加入(退出)游戏按钮点击状态
     * 5. Tapping the Cancel Join button
     * mg_common_self_click_cancel_join_btn
     */
    default void onGameMGCommonSelfClickCancelJoinBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickCancelJoinBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 准备按钮点击状态
     * 6. Tapping the Ready button
     * mg_common_self_click_ready_btn
     */
    default void onGameMGCommonSelfClickReadyBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickReadyBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 取消准备按钮点击状态
     * 7. Tapping the Cancel Ready button
     * mg_common_self_click_cancel_ready_btn
     */
    default void onGameMGCommonSelfClickCancelReadyBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickCancelReadyBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 开始游戏按钮点击状态
     * 8. Tapping the Start button
     * mg_common_self_click_start_btn
     */
    default void onGameMGCommonSelfClickStartBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickStartBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 分享按钮点击状态
     * 9. Tapping the Share button
     * mg_common_self_click_share_btn
     */
    default void onGameMGCommonSelfClickShareBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickShareBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 游戏状态
     * 10. Gaming
     * mg_common_game_state
     */
    default void onGameMGCommonGameState(ISUDFSMStateHandle handle, GIMGState.MGCommonGameState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 结算界面关闭按钮点击状态（2021-12-27新增）
     * 11. Tapping the Close button on the post-game screen (added on December 27, 2021)
     * mg_common_self_click_game_settle_close_btn
     */
    default void onGameMGCommonSelfClickGameSettleCloseBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickGameSettleCloseBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
     * 12. Tapping the Play Again button on the post-game screen (added on December 27, 2021)
     * mg_common_self_click_game_settle_again_btn
     */
    default void onGameMGCommonSelfClickGameSettleAgainBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickGameSettleAgainBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
     * 13. Reporting the sound list in a game (added on December 30, 2021)
     * mg_common_game_sound_list
     */
    default void onGameMGCommonGameSoundList(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSoundList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
     * 14. Notifying the app layer of playing sound (added on December 30, 2021)
     * mg_common_game_sound
     */
    default void onGameMGCommonGameSound(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSound model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
     * 15. Notifying the app layer of playing background music (added on January 7, 2022)
     * mg_common_game_bg_music_state
     */
    default void onGameMGCommonGameBgMusicState(ISUDFSMStateHandle handle, GIMGState.MGCommonGameBgMusicState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
     * 16. Notifying the app layer of playing sound effect (added on January 7, 2022)
     * mg_common_game_sound_state
     */
    default void onGameMGCommonGameSoundState(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSoundState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
     * 17. ASR (enabling/disabling voice recognition, added in SUDGI V1.1.45.xx)
     * mg_common_game_asr
     */
    default void onGameMGCommonGameASR(ISUDFSMStateHandle handle, GIMGState.MGCommonGameASR model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 18. 麦克风状态（2022-02-08新增）
     * 18. Microphone (added on March 4, 2022)
     * mg_common_self_microphone
     */
    default void onGameMGCommonSelfMicrophone(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfMicrophone model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
     * 19. Headphone (receiver and speaker) (added on March 4, 2022)
     * mg_common_self_headphone
     */
    default void onGameMGCommonSelfHeadphone(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfHeadphone model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 20. App通用状态操作结果错误码（2022-05-10新增）
     * 20. app_common_self_x response error code（added on May 10, 2022）
     * mg_common_app_common_self_x_resp
     */
    default void onGameMGCommonAPPCommonSelfXResp(ISUDFSMStateHandle handle, GIMGState.MGCommonAPPCommonSelfXResp model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
     * 21. Whether the game notifies the app layer of the success of adding the robot players (added on May 17, 2022)
     * mg_common_game_add_ai_players
     */
    default void onGameMGCommonGameAddAIPlayers(ISUDFSMStateHandle handle, GIMGState.MGCommonGameAddAIPlayers model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
     * 22. The game notifies the app layer to add the current network connection status (added on June 21, 2022)
     * mg_common_game_network_state
     */
    default void onGameMGCommonGameNetworkState(ISUDFSMStateHandle handle, GIMGState.MGCommonGameNetworkState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 23. 游戏通知app获取积分
     * 23. Game notification app to get score
     * mg_common_game_score
     */
    default void onGameMGCommonGameGetScore(ISUDFSMStateHandle handle, GIMGState.MGCommonGameGetScore model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 24. 游戏通知app带入积分
     * 24. score brought in by game notification app
     * mg_common_game_set_score
     */
    default void onGameMGCommonGameSetScore(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSetScore model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 25. 创建订单
     * 25. create order in game
     * mg_common_game_create_order
     */
    default void onGameMGCommonGameCreateOrder(ISUDFSMStateHandle handle, GIMGState.MGCommonGameCreateOrder model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 26. 游戏通知app玩家角色(仅对狼人杀有效)
     * 26. Game notification app player role (only valid for werewolf killing)
     * mg_common_player_role_id
     */
    default void onGameMGCommonPlayerRoleId(ISUDFSMStateHandle handle, GIMGState.MGCommonPlayerRoleId model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
     * 27. The game notifies app players that they are thrown poop (only valid for you to draw, I guess)
     * mg_common_self_click_poop
     */
    default void onGameMGCommonSelfClickPoop(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickPoop model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
     * 28. The game notifies app players that they are liked (only valid for you to draw and guess)
     * mg_common_self_click_good
     */
    default void onGameMGCommonSelfClickGood(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickGood model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
     * 29. Game Notification App Game FPS (Only effective for bumper, Dominoes, and knifeMasters)
     * mg_common_game_fps
     */
    default void onGameMGCommonGameFps(ISUDFSMStateHandle handle, GIMGState.MGCommonGameFps model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 30. 游戏通知app游戏弹框
     * 30. Game Notification App Game Pop-up
     * mg_common_alert
     */
    default void onGameMGCommonAlert(ISUDFSMStateHandle handle, GIMGState.MGCommonAlert model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 31. 游戏通知app最坑队友（只支持友尽闯关）
     * 31. Game Notification App: Most Annoying Teammate (Supports only pickpark)
     * mg_common_worst_teammate
     */
    default void onGameMGCommonWorstTeammate(ISUDFSMStateHandle handle, GIMGState.MGCommonWorstTeammate model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
     * 32. Game Notification App: Game Ended Due to Player Quitting（Supports only pickpark）
     * mg_common_game_over_tip
     */
    default void onGameMGCommonGameOverTip(ISUDFSMStateHandle handle, GIMGState.MGCommonGameOverTip model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 33. 游戏通知app玩家颜色（只支持友尽闯关）
     * 33. Game Notification App: Player Color（Supports pickpark and ludo）
     * mg_common_game_player_color
     */
    default void onGameMGCommonGamePlayerColor(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerColor model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 34. 游戏通知app玩家头像的坐标（只支持ludo）
     * 34. Game Notification App: Player Avatar Coordinates （ludo, knife, umo, dominos, teenpatti, texasholdem, drawAndGuess）
     * mg_common_game_player_icon_position
     */
    default void onGameMGCommonGamePlayerIconPosition(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerIconPosition model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
     * 35. Game Notification App: click exit game button (Only supports Teen Patti Pro and Texas Hold'em Pro)
     * mg_common_self_click_exit_game_btn
     */
    default void onGameMGCommonSelfClickExitGameBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickExitGameBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
     * 36. Game Notification App: Enable Buy-in Points (Only supports Teen Patti Pro and Texas Hold'em Pro)
     * mg_common_game_is_app_chip
     */
    default void onGameMGCommonGameIsAppChip(ISUDFSMStateHandle handle, GIMGState.MGCommonGameIsAppChip model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 37. 游戏通知app当前游戏的设置信息
     * 37. Game Notification App: Current Game Settings Information
     * mg_common_game_rule
     */
    default void onGameMGCommonGameRule(ISUDFSMStateHandle handle, GIMGState.MGCommonGameRule model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
     * 38. Game Notification App: Game Mode Settings (Only supports Texas Hold'em Pro and Teen Patti Pro)
     * mg_common_game_settings
     */
    default void onGameMGCommonGameSettings(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSettings model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
     * 39. Game Notification App: Insufficient Coins (Only supports Texas Hold'em Pro and Teen Patti Pro)
     * mg_common_game_money_not_enough
     */
    default void onGameMGCommonGameMoneyNotEnough(ISUDFSMStateHandle handle, GIMGState.MGCommonGameMoneyNotEnough model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 40. 游戏通知app下发定制ui配置表（只支持ludo）
     * 40. Game Notification App: Send Custom UI Configuration Table (supports Ludo, fir)
     * mg_common_game_ui_custom_config
     */
    default void onGameMGCommonGameUiCustomConfig(ISUDFSMStateHandle handle, GIMGState.MGCommonGameUiCustomConfig model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 41. 设置app提供给游戏可点击区域(赛车)
     * 41. Set clickable areas provided by the app for the game (crazyracing)
     * mg_common_set_click_rect
     */
    default void onGameMGCommonSetClickRect(ISUDFSMStateHandle handle, GIMGState.MGCommonSetClickRect model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 42. 通知app提供对应uids列表玩家的数据(赛车)
     * 42. Notify the app to provide data for the corresponding list of UIDs' players (crazyracing).
     * mg_common_users_info
     */
    default void onGameMGCommonUsersInfo(ISUDFSMStateHandle handle, GIMGState.MGCommonUsersInfo model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 43. 通知app游戏前期准备完成(赛车)
     * 43. Notify the app that the game's preliminary preparations are complete.(crazyracing)
     * mg_common_game_prepare_finish
     */
    default void onGameMGCommonGamePrepareFinish(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePrepareFinish model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 44. 通知app游戏主界面已显示(赛车)
     * 44. Notify the app that the game's main interface has been displayed.(crazyracing)
     * mg_common_show_game_scene
     */
    default void onGameMGCommonShowGameScene(ISUDFSMStateHandle handle, GIMGState.MGCommonShowGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 45. 通知app游戏主界面已隐藏(赛车)
     * 45. Notify the app that the game's main interface has been hidden.(crazyracing)
     * mg_common_hide_game_scene
     */
    default void onGameMGCommonHideGameScene(ISUDFSMStateHandle handle, GIMGState.MGCommonHideGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 46. 通知app点击了游戏的金币按钮(赛车)
     * 46. Notify the app that the game's coin button has been clicked.(crazyracing)
     * mg_common_self_click_gold_btn
     */
    default void onGameMGCommonSelfClickGoldBtn(ISUDFSMStateHandle handle, GIMGState.MGCommonSelfClickGoldBtn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 47. 通知app棋子到达终点(ludo)
     * 47. Notify app reaches the destination (ludo)
     * mg_common_game_piece_arrive_end
     */
    default void onGameMGCommonGamePieceArriveEnd(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePieceArriveEnd model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 48. 通知app玩家是否托管
     * 48. Notify App the player is auto Managed
     * mg_common_game_player_managed_state
     */
    default void onGameMGCommonGamePlayerManagedState(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerManagedState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 49. 游戏向app发送爆词
     * 49. Notify App the baochi （whoisspy）
     * mg_common_game_send_burst_word
     */
    default void onGameMGCommonGameSendBurstWord(ISUDFSMStateHandle handle, GIMGState.MGCommonGameSendBurstWord model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
     * 50. Game sends real-time player rankings to the app (only supported in Monster Smash).
     * mg_common_game_player_ranks
     */
    default void onGameMGCommonGamePlayerRanks(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerRanks model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
     * 51. Game sends real-time changes of odd and even cards to the app (only supported in Okey101).
     * mg_common_game_player_pair_singular
     */
    default void onGameMGCommonGamePlayerPairSingular(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerPairSingular model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
     * 52. Game sends the app a request for obtaining the player's held property cards (only supported in Monopoly).
     * mg_common_game_player_monopoly_cards
     */
    default void onGameMGCommonGamePlayerMonopolyCards(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerMonopolyCards model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
     * 53. Game sends real-time player scores to the app (only supported in Monster Smash).
     * mg_common_game_player_scores
     */
    default void onGameMGCommonGamePlayerScores(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerScores model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 54. 游戏通知app销毁游戏（只支持部分概率类游戏）
     * 54. The game informs the app to destroy the game
     * mg_common_destroy_game_scene
     */
    default void onGameMGCommonDestroyGameScene(ISUDFSMStateHandle handle, GIMGState.MGCommonDestroyGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 55. 游戏通知app击球状态（只支持桌球）
     * 55. Game notification app Batting status (only table tennis is supported)
     * mg_common_game_billiards_hit_state
     */
    default void onGameMGCommonGameBilliardsHitState(ISUDFSMStateHandle handle, GIMGState.MGCommonGameBilliardsHitState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 56. 游戏向app发送获取玩家持有的指定点数道具卡（只支持飞行棋）
     * 56. The game sends the item card to the app to obtain the specified points held by the player (only flying chess is supported)
     * mg_common_game_player_props_cards
     */
    default void onGameMGCommonGamePlayerPropsCards(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerPropsCards model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 57. 游戏向app发送获游戏通用数据
     * 57. The game sends general game data to the app.
     */
    default void onGameMGCommonGameInfoX(ISUDFSMStateHandle handle, GIMGState.MGCommonGameInfoX model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 通知app开启ai大模型
     * Notify app to open ai large model
     */
    default void onGameMGCommonAiModelMessage(ISUDFSMStateHandle handle, GIMGState.MGCommonAiModelMessage model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 通知app ai消息
     * Notify app ai of the message
     */
    default void onGameMGCommonAiMessage(ISUDFSMStateHandle handle, GIMGState.MGCommonAiMessage model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 64. 通知app ai大模型消息
     */
    default void onGameMGCommonAiLargeScaleModelMsg(ISUDFSMStateHandle handle, GIMGState.MGCommonAiLargeScaleModelMsg model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 65. 通知app可以开始推送麦克说话状态
     * mg_common_game_player_mic_state
     */
    default void onGameMGCommonGamePlayerMicState(ISUDFSMStateHandle handle, GIMGState.MGCommonGamePlayerMicState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    // endregion 游戏回调APP 通用状态 English: Game callback to APP for general state.

    // region 游戏回调APP 玩家状态 English: Game callback to APP for player state.
    // 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/CommonStatePlayer.html
    // Reference documentation：https://docs.sud.tech/en-US/app/Client/MGFSM/CommonStatePlayer.html

    /**
     * 1.加入状态（已修改）
     * 1. Joining (modified)
     * mg_common_player_in
     */
    default void onPlayerMGCommonPlayerIn(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerIn model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2.准备状态（已修改）
     * 2. Ready (modified)
     * mg_common_player_ready
     */
    default void onPlayerMGCommonPlayerReady(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerReady model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3.队长状态（已修改）
     * 3. Captain (modified)
     * mg_common_player_captain
     */
    default void onPlayerMGCommonPlayerCaptain(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerCaptain model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4.游戏状态（已修改）
     * 4. Gaming (modified)
     * mg_common_player_playing
     */
    default void onPlayerMGCommonPlayerPlaying(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerPlaying model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5.玩家在线状态
     * 5. Changing the game seat
     * mg_common_player_online
     */
    default void onPlayerMGCommonPlayerOnline(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerOnline model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6.玩家换游戏位状态
     * 6. Notifying the app of tapping a player's avatar (Added on February 9, 2022. This state applies only to player avatars in game scenes.)
     * mg_common_player_change_seat
     */
    default void onPlayerMGCommonPlayerChangeSeat(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonPlayerChangeSeat model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 游戏通知app点击玩家头像
     * 7. Click the player's Avatar on the game notification app
     * mg_common_self_click_game_player_icon
     */
    default void onPlayerMGCommonSelfClickGamePlayerIcon(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonSelfClickGamePlayerIcon model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 游戏通知app玩家死亡状态（2022-04-24新增）
     * 8. Game notification app player death status (added on April 24, 2022)
     * mg_common_self_die_status
     */
    default void onPlayerMGCommonSelfDieStatus(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonSelfDieStatus model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
     * 9. Game notification app player's turn (added on April 24, 2022)
     * mg_common_self_turn_status
     */
    default void onPlayerMGCommonSelfTurnStatus(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonSelfTurnStatus model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 游戏通知app玩家选择状态（2022-04-24新增）
     * 10. Game notification app player selection status (added on April 24, 2022)
     * mg_common_self_select_status
     */
    default void onPlayerMGCommonSelfSelectStatus(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonSelfSelectStatus model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
     * 11. The game notifies the app layer of the remaining time of the current game (added on May 23, 2022, and UMO takes effect at present)
     * mg_common_game_countdown_time
     */
    default void onPlayerMGCommonGameCountdownTime(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonGameCountdownTime model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
     * 12. The game notifies the app layer that the current player will become an OB perspective after death (added on August 23, 2022, and now the werewolf )
     * mg_common_self_ob_status
     */
    default void onPlayerMGCommonSelfObStatus(ISUDFSMStateHandle handle, String userId, GIMGState.MGCommonSelfObStatus model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    // endregion 游戏回调APP 玩家状态 English: Game callback to APP for player state.

    // region 游戏回调APP 玩家状态 你画我猜 English: Game callback to APP for player state in 'You Draw, I Guess'.
    // 参考文档：https://docs.sud.tech/zh-CN/app/Client/MGFSM/DrawGuess.html
    // Reference documentation：https://docs.sud.tech/en-US/app/Client/MGFSM/DrawGuess.html

    /**
     * 1. 选词中状态（已修改）
     * 1. Selecting words (modified)
     * mg_dg_selecting
     */
    default void onPlayerMGDGSelecting(ISUDFSMStateHandle handle, String userId, GIMGState.MGDGSelecting model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 作画中状态（已修改）
     * 2. Drawing (modified)
     * mg_dg_painting
     */
    default void onPlayerMGDGPainting(ISUDFSMStateHandle handle, String userId, GIMGState.MGDGPainting model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 显示错误答案状态（已修改）
     * 3. Displaying incorrect answers (modified)
     * mg_dg_erroranswer
     */
    default void onPlayerMGDGErroranswer(ISUDFSMStateHandle handle, String userId, GIMGState.MGDGErroranswer model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 显示总积分状态（已修改）
     * 4. Displaying the total points (modified)
     * mg_dg_totalscore
     */
    default void onPlayerMGDGTotalscore(ISUDFSMStateHandle handle, String userId, GIMGState.MGDGTotalscore model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 本次获得积分状态（已修改）
     * 5. Displaying the points obtained from this round (modified)
     * mg_dg_score
     */
    default void onPlayerMGDGScore(ISUDFSMStateHandle handle, String userId, GIMGState.MGDGScore model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    // endregion 游戏回调APP 玩家状态 你画我猜 English: Game callback to APP for player state in 'You Draw, I Guess'.

    // region 游戏回调APP 通用状态 元宇宙砂砂舞 English: Game callback to APP for general state in Metaverse Sand Dance.

    /**
     * 1. 元宇宙砂砂舞指令回调
     * 1. Callback for instructions in the Metaverse Sand Dance game.
     * mg_common_game_disco_action
     */
    default void onGameMGCommonGameDiscoAction(ISUDFSMStateHandle handle, GIMGState.MGCommonGameDiscoAction model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 元宇宙砂砂舞指令动作结束通知
     * 2. Notification for the completion of actions in the Metaverse Sand Dance game.
     * mg_common_game_disco_action_end
     */
    default void onGameMGCommonGameDiscoActionEnd(ISUDFSMStateHandle handle, GIMGState.MGCommonGameDiscoActionEnd model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态 元宇宙砂砂舞 English: Game callback to APP for general state in Metaverse Sand Dance.

    // region 游戏回调APP 通用状态 定制火箭 English: Game callback to APP for general state in Custom Rocket.

    /**
     * 1. 礼物配置文件(火箭)
     * 1. Gift Configuration File (Rocket)
     * mg_custom_rocket_config
     */
    default void onGameMGCustomRocketConfig(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketConfig model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 拥有模型列表(火箭)
     * 2. List of Owned Models (Rocket)
     * mg_custom_rocket_model_list
     */
    default void onGameMGCustomRocketModelList(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketModelList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 拥有组件列表(火箭)
     * 3. List of Owned Components (Rocket)
     * mg_custom_rocket_component_list
     */
    default void onGameMGCustomRocketComponentList(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketComponentList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 获取用户信息(火箭)
     * 4. Get User Information (Rocket)
     * mg_custom_rocket_user_info
     */
    default void onGameMGCustomRocketUserInfo(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketUserInfo model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 订单记录列表(火箭)
     * 5. Order History List (Rocket)
     * mg_custom_rocket_order_record_list
     */
    default void onGameMGCustomRocketOrderRecordList(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketOrderRecordList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 展馆内列表(火箭)
     * 6. Exhibition Hall List (Rocket)
     * mg_custom_rocket_room_record_list
     */
    default void onGameMGCustomRocketRoomRecordList(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketRoomRecordList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 展馆内玩家送出记录(火箭)
     * 7. Game client notifies the app to fetch the player gifting records in the exhibition hall.(Rocket)
     * mg_custom_rocket_user_record_list
     */
    default void onGameMGCustomRocketUserRecordList(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketUserRecordList model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 设置默认模型(火箭)
     * 8. Set default model (Rocket)
     * mg_custom_rocket_set_default_model
     */
    default void onGameMGCustomRocketSetDefaultModel(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketSetDefaultModel model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 动态计算一键发送价格(火箭)
     * 9. Dynamic calculation of one-click sending price(Rocket)
     * mg_custom_rocket_dynamic_fire_price
     */
    default void onGameMGCustomRocketDynamicFirePrice(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketDynamicFirePrice model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 一键发送(火箭)
     * 10. One-click sending(Rocket)
     * mg_custom_rocket_fire_model
     */
    default void onGameMGCustomRocketFireModel(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketFireModel model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 11. 新组装模型(火箭)
     * 11. Newly assembled model.(Rocket)
     * mg_custom_rocket_create_model
     */
    default void onGameMGCustomRocketCreateModel(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketCreateModel model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 12. 模型更换组件(火箭)
     * 12. Model Component Replacement(Rocket)
     * mg_custom_rocket_replace_component
     */
    default void onGameMGCustomRocketReplaceComponent(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketReplaceComponent model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 13. 购买组件(火箭)
     * 13. Buy component(Rocket)
     * mg_custom_rocket_buy_component
     */
    default void onGameMGCustomRocketBuyComponent(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketBuyComponent model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 14. 播放效果开始(火箭)
     * 14. Play effect start(Rocket)
     * mg_custom_rocket_play_effect_start
     */
    default void onGameMGCustomRocketPlayEffectStart(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketPlayEffectStart model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 15. 播放效果完成(火箭)
     * 15. Play effect finished(Rocket)
     * mg_custom_rocket_play_effect_finish
     */
    default void onGameMGCustomRocketPlayEffectFinish(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketPlayEffectFinish model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 16. 验证签名合规(火箭)
     * 16. To verify the compliance of a signature(Rocket)
     * mg_custom_rocket_verify_sign
     */
    default void onGameMGCustomRocketVerifySign(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketVerifySign model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 17. 上传icon(火箭)
     * 17. upload icon(Rocket)
     * mg_custom_rocket_upload_model_icon
     */
    default void onGameMGCustomRocketUploadModelIcon(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketUploadModelIcon model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 18. 前期准备完成(火箭)
     * 18. Preparation is complete.(Rocket)
     * mg_custom_rocket_prepare_finish
     */
    default void onGameMGCustomRocketPrepareFinish(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketPrepareFinish model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 19. 火箭主界面已显示(火箭)
     * 19. Rocket main interface has been displayed(Rocket)
     * mg_custom_rocket_show_game_scene
     */
    default void onGameMGCustomRocketShowGameScene(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketShowGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 20. 火箭主界面已隐藏(火箭)
     * 20. The rocket main interface has been hidden(Rocket)
     * mg_custom_rocket_hide_game_scene
     */
    default void onGameMGCustomRocketHideGameScene(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketHideGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 21. 点击锁住组件(火箭)
     * 21. Click to lock the component(Rocket)
     * mg_custom_rocket_click_lock_component
     */
    default void onGameMGCustomRocketClickLockComponent(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketClickLockComponent model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 22. 火箭效果飞行点击(火箭)
     * 22. Rocket effect fly click(Rocket)
     * mg_custom_rocket_fly_click
     */
    default void onGameMGCustomRocketFlyClick(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketFlyClick model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 23. 火箭效果飞行结束(火箭)
     * 23. Rocket effect fly finished(Rocket)
     * mg_custom_rocket_fly_end
     */
    default void onGameMGCustomRocketFlyEnd(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketFlyEnd model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 24. 设置点击区域(火箭)
     * 24. Set clickable area provided by the app for the game(Rocket)
     * mg_custom_rocket_set_click_rect
     */
    default void onGameMGCustomRocketSetClickRect(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketSetClickRect model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名
     * 25. Save rocket signature or color.(Rocket)
     * mg_custom_rocket_save_sign_color
     */
    default void onGameMGCustomRocketSaveSignColor(ISUDFSMStateHandle handle, GIMGState.MGCustomRocketSaveSignColor model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态 定制火箭 English: Game callback to APP for general state in Custom Rocket.

    // region 游戏回调APP 通用状态 棒球 English: Game callback to APP for general state in Baseball.

    /**
     * 1. 设置界面默认状态(棒球)
     * 1. Set Default State of Interface (Baseball)
     * mg_baseball_defualt_state
     */
    default void onGameMGBaseballDefaultState(ISUDFSMStateHandle handle, GIMGState.MGBaseballDefaultState model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 前期准备完成(棒球)
     * 2. Preparation is complete (Baseball)
     * mg_baseball_prepare_finish
     */
    default void onGameMGBaseballPrepareFinish(ISUDFSMStateHandle handle, GIMGState.MGBaseballPrepareFinish model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 3. 主界面已显示(棒球)
     * 3. The main interface is now displayed (Baseball)
     * mg_baseball_show_game_scene
     */
    default void onGameMGBaseballShowGameScene(ISUDFSMStateHandle handle, GIMGState.MGBaseballShowGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 4. 主界面已隐藏(棒球)
     * 4. The game client has notified the app that the main interface is now hidden (Baseball)
     * mg_baseball_hide_game_scene
     */
    default void onGameMGBaseballHideGameScene(ISUDFSMStateHandle handle, GIMGState.MGBaseballHideGameScene model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 5. 查询排行榜数据(棒球)
     * 5. To query the ranks data (Baseball)
     * mg_baseball_ranking
     */
    default void onGameMGBaseballRanking(ISUDFSMStateHandle handle, GIMGState.MGBaseballRanking model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 6. 查询我的排名(棒球)
     * 6. Querying my ranking (baseball)
     * mg_baseball_my_ranking
     */
    default void onGameMGBaseballMyRanking(ISUDFSMStateHandle handle, GIMGState.MGBaseballMyRanking model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 7. 查询当前距离我的前后玩家数据(棒球)
     * 7. The game client notifies the app to query the data of players before and after me in the current distance (Baseball)
     * mg_baseball_range_info
     */
    default void onGameMGBaseballRangeInfo(ISUDFSMStateHandle handle, GIMGState.MGBaseballRangeInfo model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 8. 设置app提供给游戏可点击区域(棒球)
     * 8. Set the clickable area provided by the app for the game (baseball)
     * mg_baseball_set_click_rect
     */
    default void onGameMGBaseballSetClickRect(ISUDFSMStateHandle handle, GIMGState.MGBaseballSetClickRect model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 9. 获取文本配置数据(棒球)
     * 9. Retrieve Text Configuration Data (Baseball)
     * mg_baseball_text_config
     */
    default void onGameMGBaseballTextConfig(ISUDFSMStateHandle handle, GIMGState.MGBaseballTextConfig model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 10. 球落地, 通知距离(棒球)
     * 10. Ball landed, notify distance (Baseball)
     * mg_baseball_send_distance
     */
    default void onGameMGBaseballSendDistance(ISUDFSMStateHandle handle, GIMGState.MGBaseballSendDistance model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 通用状态 棒球 English: Game callback to APP for general state in Baseball.

    // region 游戏回调APP 3D语聊房

    /**
     * 1. 请求房间数据
     * mg_custom_cr_room_init_data
     */
    default void onGameMGCustomCrRoomInitData(ISUDFSMStateHandle handle, GIMGState.MGCustomCrRoomInitData model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }

    /**
     * 2. 点击主播位或老板位通知
     * mg_custom_cr_click_seat
     */
    default void onGameMGCustomCrClickSeat(ISUDFSMStateHandle handle, GIMGState.MGCustomCrClickSeat model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 3D语聊房

    // region 游戏回调APP 喜羊羊

    /**
     * 1. 文本/语音聊天结果
     * mg_happy_goat_chat
     */
    default void onGameMGHappyGoatChat(ISUDFSMStateHandle handle, GIMGState.MGHappyGoatChat model) {
        GIFSMStateHandleUtils.handleSuccess(handle);
    }
    // endregion 游戏回调APP 喜羊羊

    /**
     * 游戏状态变化
     * 透传游戏向App发送的游戏通用状态消息
     * **********使用此方法可先看下此方法的使用逻辑*************
     *
     * @param handle   回调操作
     * @param state    状态命令
     * @param dataJson 状态值
     * @return 返回true，表示由此方法接管该状态处理，此时需注意调用：GIFSMStateHandleUtils.handleSuccess(handle);
     */
    default boolean onGameStateChange(ISUDFSMStateHandle handle, String state, String dataJson) {
        return false;
    }

    /**
     * 游戏玩家状态变化
     * 透传游戏向App发送的玩家状态变化
     * **********使用此方法可先看下此方法的使用逻辑*************
     *
     * @param handle   回调操作
     * @param userId   用户Id
     * @param state    状态命令
     * @param dataJson 状态值
     * @return 返回true，表示由此方法接管该状态处理，此时需注意调用：GIFSMStateHandleUtils.handleSuccess(handle);
     */
    default boolean onPlayerStateChange(ISUDFSMStateHandle handle, String userId, String state, String dataJson) {
        return false;
    }

}
