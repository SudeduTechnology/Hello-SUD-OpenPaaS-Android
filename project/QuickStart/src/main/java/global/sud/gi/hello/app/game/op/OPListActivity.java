package global.sud.gi.hello.app.game.op;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.List;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.LanguageUtils;
import global.sud.gi.hello.app.game.op.data.OPSession;
import global.sud.gi.hello.app.game.op.net.OPApiClient;
import global.sud.gi.hello.app.game.op.repository.OPRepository;
import global.sud.gi.hello.app.game.op.ui.OPAdapter;
import global.sud.gi.hello.app.game.op.ui.OPPresentation;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.listener.SUDOPInitSDKListener;
import global.sud.op.runtime.core.model.SUDOPGameListItem;

public class OPListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private View stateContainer;
    private ProgressBar progressBar;
    private TextView statusView;
    private Button retryButton;
    private OPAdapter adapter;
    private OPApiClient apiClient;
    private OPRepository gameRepository;
    private int loadGeneration;
    private boolean requestInFlight;

    public static void start(Context context) {
        context.startActivity(new Intent(context, OPListActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_op_list;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = findViewById(R.id.op_game_list);
        stateContainer = findViewById(R.id.op_state_container);
        progressBar = findViewById(R.id.op_progress);
        statusView = findViewById(R.id.op_status);
        retryButton = findViewById(R.id.op_retry);

        adapter = new OPAdapter(LanguageUtils.getLanguageTag(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        apiClient = new OPApiClient(getApplicationContext());
        gameRepository = new OPRepository();
    }

    @Override
    protected void initData() {
        super.initData();
        startLoad();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.op_back).setOnClickListener(v -> finish());
        retryButton.setOnClickListener(v -> startLoad());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> source,
                                    @NonNull View view,
                                    int position) {
                if (requestInFlight) {
                    return;
                }
                SUDOPGameListItem item = adapter.getItem(position);
                String language = LanguageUtils.getLanguageTag(OPListActivity.this);
                OPActivity.start(
                        OPListActivity.this,
                        item.gameId,
                        OPPresentation.resolveName(item, language)
                );
            }
        });
    }

    private void startLoad() {
        if (requestInFlight) {
            return;
        }
        int generation = ++loadGeneration;
        requestInFlight = true;
        showLoading(R.string.op_initializing_sdk);
        SUDOP.initSDK(
                getApplicationContext(),
                AppConfig.OP_APP_ID,
                AppConfig.OP_APP_KEY,
                new SUDOPInitSDKListener() {
                    @Override
                    public void onSuccess() {
                        runIfCurrent(generation, () -> ensureSession(generation));
                    }

                    @Override
                    public void onFailure(int retCode, String retMsg) {
                        runIfCurrent(generation, () ->
                                showError(getString(R.string.op_sdk_init_failed, retCode)));
                    }
                }
        );
    }

    private void ensureSession(int generation) {
        showLoading(R.string.op_preparing_account);
        apiClient.ensureSession(new OPApiClient.SessionCallback() {
            @Override
            public void onSuccess(OPSession session) {
                if (!canHandle(generation)) {
                    return;
                }
                loadGames(generation);
            }

            @Override
            public void onFailure(int code, String message) {
                if (canHandle(generation)) {
                    showError(getString(R.string.op_login_failed, code));
                }
            }
        });
    }

    private void loadGames(int generation) {
        showLoading(R.string.op_loading_games);
        gameRepository.loadAll(
                AppConfig.OP_SELECTION_ID,
                LanguageUtils.getLanguageTag(this),
                new OPRepository.Callback() {
                    @Override
                    public void onSuccess(List<SUDOPGameListItem> games) {
                        runIfCurrent(generation, () -> {
                            requestInFlight = false;
                            adapter.setList(games);
                            if (games.isEmpty()) {
                                showEmpty();
                            } else {
                                showContent();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int retCode, String retMsg) {
                        runIfCurrent(generation, () ->
                                showError(getString(R.string.op_game_list_failed, retCode)));
                    }
                }
        );
    }

    private boolean canHandle(int generation) {
        return generation == loadGeneration && !isFinishing() && !isDestroyed();
    }

    private void runIfCurrent(int generation, Runnable action) {
        runOnUiThread(() -> {
            if (canHandle(generation)) {
                action.run();
            }
        });
    }

    private void showLoading(int messageResId) {
        recyclerView.setVisibility(View.GONE);
        stateContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        statusView.setText(messageResId);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        stateContainer.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        stateContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
        statusView.setText(R.string.op_empty_games);
    }

    private void showError(String message) {
        requestInFlight = false;
        recyclerView.setVisibility(View.GONE);
        stateContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
        statusView.setText(message);
    }

    @Override
    protected void onDestroy() {
        loadGeneration++;
        requestInFlight = false;
        if (apiClient != null) {
            apiClient.cancelAll();
        }
        if (gameRepository != null) {
            gameRepository.cancel();
        }
        super.onDestroy();
    }
}
