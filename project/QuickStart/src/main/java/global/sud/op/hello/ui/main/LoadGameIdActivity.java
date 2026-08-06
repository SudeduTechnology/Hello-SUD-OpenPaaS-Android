package global.sud.op.hello.ui.main;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;

import global.sud.op.hello.R;
import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.QuickStartGameActivity;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class LoadGameIdActivity extends BaseActivity {

    private EditText etGameId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_game_id;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        etGameId = findViewById(R.id.et_gameid);
        etGameId.setText(GlobalSP.getSP().getString(GlobalSP.KEY_GAME_ID, AppConfig.TEST_GAME_ID));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_start).setOnClickListener(v -> startGame());
    }

    private void startGame() {
        String gameId = getInputContent();
        if (TextUtils.isEmpty(gameId)) {
            ToastUtils.showShort("请输入gameId");
            return;
        }
        GameModel gameModel = new GameModel();
        gameModel.gameId = gameId;
        gameModel.pathType = SUDOPGamePathType.GAME_ID;
        // 测试代码，固定横屏
        gameModel.orientationMode = GameModel.ORIENTATION_LANDSCAPE;
        QuickStartGameActivity.start(this, gameModel);
        GlobalSP.getSP().put(GlobalSP.KEY_GAME_ID, gameId);
    }

    private String getInputContent() {
        Editable text = etGameId.getText();
        if (text == null) {
            return null;
        }
        return text.toString();
    }

}
