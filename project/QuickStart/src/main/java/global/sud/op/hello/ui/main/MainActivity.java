package global.sud.op.hello.ui.main;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.blankj.utilcode.util.ToastUtils;

import global.sud.gi.core.ISUDAPPD;
import global.sud.op.hello.R;
import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.http.param.BaseUrlManager;
import global.sud.op.hello.common.http.param.IBaseUrl;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.sudedu.repository.FeedRepository;
import global.sud.op.hello.ui.game.widget.ChangeAppIdDialog;
import global.sud.op.runtime.core.SUDOP;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
        AppConfig.SudGIP_APP_ID = GlobalSP.getSP().getString(GlobalSP.KEY_APP_ID, AppConfig.SudGIP_APP_ID);
        AppConfig.SudGIP_APP_KEY = GlobalSP.getSP().getString(GlobalSP.KEY_APP_KEY, AppConfig.SudGIP_APP_KEY);
        new Thread(() -> {
            try {
                FeedRepository.getInstance().fetchLinkedGames();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtils.showLong(e.getMessage()));
            }
        }).start();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_game_id).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameIdActivity.class));
        });
        findViewById(R.id.btn_change_app_id).setOnClickListener(v -> {
            onClickChangeAppId();
        });
        findViewById(R.id.btn_game_signature).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameSignatureActivity.class));
        });
        findViewById(R.id.btn_load_old_game).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadOldGameActivity.class));
        });
        findViewById(R.id.btn_load_game_url).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameUrlActivity.class));
        });
    }

    private void onClickChangeAppId() {
        new ChangeAppIdDialog().show(getSupportFragmentManager(), null);
    }

}
