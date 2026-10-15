package global.sud.gi.hello.app.game.gi;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.Utils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import global.sud.gi.SUDGIWrapper.gi.model.GIGameConfigModel;
import global.sud.gi.SUDGIWrapper.gi.model.GIGameViewInfoModel;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.SUDGIWrapper.custom.base.utils.SystemUtils;
import global.sud.gi.hello.app.common.utils.ViewUtils;
import global.sud.gi.hello.app.game.gi.widget.GIGameModeDialog;
import global.sud.gi.hello.app.game.gi.widget.GIGameRoomMoreDialog;
import global.sud.gi.hello.app.game.gi.widget.GIGameRoomTopView;

/**
 * 游戏页面
 * Game page
 */
public class GIGameActivity extends BaseActivity {

    private String roomId;
    private long gameId;

    private GIGameRoomTopView topView;

    private final GIGameViewModel gameViewModel = new GIGameViewModel();

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, String roomId, long gameId) {
        Intent intent = new Intent(context, GIGameActivity.class);
        intent.putExtra("roomId", roomId);
        intent.putExtra("gameId", gameId);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        roomId = getIntent().getStringExtra("roomId");
        gameId = getIntent().getLongExtra("gameId", 0);
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gi_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = findViewById(R.id.room_top_view);

        ViewUtils.addMarginTop(topView, ImmersionBar.getStatusBarHeight(this));

        topView.setName(getString(R.string.room_name));
        topView.setId(getString(R.string.room_number) + " " + roomId);

        // 设置游戏安全操作区域
        // Set the secure operation area for the game.
        GIGameViewInfoModel.GameViewRectModel gameViewRectModel = new GIGameViewInfoModel.GameViewRectModel();
        gameViewRectModel.left = 0;
        gameViewRectModel.top = DensityUtils.dp2px(Utils.getApp(), 44) + BarUtils.getStatusBarHeight();
        gameViewRectModel.right = 0;
        gameViewRectModel.bottom = DensityUtils.dp2px(Utils.getApp(), 54);
        gameViewModel.gameViewRectModel = gameViewRectModel;

        // 游戏配置
        // Game configuration
        GIGameConfigModel gameConfigModel = gameViewModel.getGIGameConfigModel();
        gameConfigModel.ui.ping.hide = false; // 配置不隐藏ping值 English: Configuration to not hide ping value

        // 语言代码
        // Language code
        gameViewModel.languageCode = SystemUtils.getLanguageCode(this);
    }

    @Override
    protected void initData() {
        super.initData();
        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        // Call this method to load the corresponding game. Developers can decide when to load the game based on their business logic.
        gameViewModel.switchGame(this, roomId, gameId);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        FrameLayout gameContainer = findViewById(R.id.game_container); // 获取游戏View容器 English: Retrieve the game view container.
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除 English: When closing the game, remove the game view.
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内 English: Add the game view to the container.
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        // 选择游戏的点击监听
        // Click listener for selecting the game.
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGIGameModeDialog();
            }
        });

        // 更多按钮的点击监听
        // Click listener for the 'More' button.
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        });
    }

    private void showMoreDialog() {
        GIGameRoomMoreDialog dialog = new GIGameRoomMoreDialog();
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                gameViewModel.destroyMG();
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showGIGameModeDialog() {
        GIGameModeDialog dialog = new GIGameModeDialog();
        dialog.setPlayingGameId(gameId);
        dialog.setSelectGameListener(new GIGameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(long gameId) {
                GIGameActivity.this.gameId = gameId;
                gameViewModel.switchGame(context, roomId, gameId);
                updateStatusBar();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void updateStatusBar() {
        // 这个沉浸式状态栏的使用是APP的业务，对于游戏而言不是必须的
        // The use of the immersive status bar is part of the app's functionality and is not essential for games.
        if (gameId > 0) { // 玩着游戏 English: Playing the game.
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
        // 注意：要在此处调用onResume()方法
        // Note: Call the onResume() method here.
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注意：要在此处调用onPause()方法
        // Note: Call the onPause() method here.
        gameViewModel.onPause();
    }

    @Override
    public void onBackPressed() {
        // 注意：需要保证页面销毁之前，先调用游戏的销毁方法
        // 如果有其他地方调用finish()，那么也要在finish()之前，先调用游戏的销毁方法

        // Note: Ensure that the game's destruction method is called before the page is destroyed.
        // If finish() is called elsewhere, make sure to call the game's destruction method before finish().

        gameViewModel.destroyMG();

        super.onBackPressed();
    }

    @Override
    public void finish() {
        gameViewModel.destroyMG();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.destroyMG();
    }

}
