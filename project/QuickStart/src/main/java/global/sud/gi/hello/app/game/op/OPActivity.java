package global.sud.gi.hello.app.game.op;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.ViewUtils;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.internal.CustomAdapt;

public class OPActivity extends BaseActivity implements CustomAdapt {

    private static final String EXTRA_GAME_ID = "gameId";
    private static final String EXTRA_GAME_NAME = "gameName";

    private String gameId;
    private String gameName;
    private OPViewModel gameViewModel;
    private FrameLayout gameContainer;
    private ConstraintLayout widgetContainer;
    private View loadingContainer;
    private TextView loadingText;
    private View menuButton;

    public static void start(Context context,
                             String gameId,
                             String gameName) {
        Intent intent = new Intent(context, OPActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameId);
        intent.putExtra(EXTRA_GAME_NAME, gameName);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        gameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        gameName = getIntent().getStringExtra(EXTRA_GAME_NAME);
        return TextUtils.isEmpty(gameId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_op;
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameContainer = findViewById(R.id.op_game_container);
        widgetContainer = findViewById(R.id.op_widget_container);
        loadingContainer = findViewById(R.id.op_game_loading);
        loadingText = findViewById(R.id.op_game_loading_text);
        menuButton = findViewById(R.id.op_game_menu);
        TextView title = findViewById(R.id.op_game_title);
        title.setText(TextUtils.isEmpty(gameName) ? getString(R.string.op_game_title) : gameName);
        ViewUtils.addMarginTop(menuButton, ImmersionBar.getStatusBarHeight(this));
        gameViewModel = new OPViewModel(this);
    }

    @Override
    protected void initData() {
        super.initData();
        gameViewModel.setSUDOPWrappedClient(new OPWrappedClient(
                this,
                gameContainer,
                widgetContainer,
                menuButton,
                gameViewModel.getApiClient().getSessionStore()
        ));
        gameViewModel.switchGame(this, gameId);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.op_game_close).setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        gameViewModel.gameViewLiveData.observe(this, view -> {
            gameContainer.removeAllViews();
            if (view != null) {
                gameContainer.addView(view,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
            }
        });
        gameViewModel.progressLiveData.observe(this, this::updateProgress);
        gameViewModel.queryExitLiveData.observe(this, ignored -> finish());
    }

    private void updateProgress(BaseGameViewModel.ProgressModel progress) {
        if (progress == null) {
            return;
        }
        if (progress.stage == 3 && progress.retCode == 0) {
            loadingContainer.setVisibility(View.GONE);
            return;
        }
        loadingContainer.setVisibility(View.VISIBLE);
        if (progress.retCode != 0) {
            loadingText.setText(getString(R.string.op_game_start_failed, progress.retCode));
        } else if (progress.progress > 0) {
            loadingText.setText(getString(R.string.op_game_loading_progress, progress.progress));
        } else {
            loadingText.setText(R.string.op_game_loading);
        }
    }

    private void updateStatusBar() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ImmersionBar.with(this)
                    .statusBarColor(R.color.transparent)
                    .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
                    .init();
        } else {
            ImmersionBar.with(this).fullScreen(true).hideBar(BarHide.FLAG_HIDE_BAR).init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gameViewModel != null) {
            gameViewModel.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
        if (gameViewModel != null) {
            gameViewModel.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (gameViewModel != null) {
            gameViewModel.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (gameViewModel != null) {
            gameViewModel.onStop();
        }
        super.onStop();
    }

    @Override
    public void finish() {
        if (gameViewModel != null) {
            gameViewModel.destroy();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (gameViewModel != null) {
            gameViewModel.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean isBaseOnWidth() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public float getSizeInDp() {
        return 375;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AutoSize.autoConvertDensityOfCustomAdapt(this, this);
        updateStatusBar();
    }
}
