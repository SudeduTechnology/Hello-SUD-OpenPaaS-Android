package global.sud.op.hello.ui.main;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONObject;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.QuickStartGameActivity;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class LoadGameSignatureActivity extends BaseActivity {

    private EditText etContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_game_signature;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        etContent = findViewById(R.id.et_content);
        etContent.setText(GlobalSP.getSP().getString(GlobalSP.KEY_GAME_SIGNATURE, ""));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_start).setOnClickListener(v -> startGame());
    }

    private void startGame() {
        String signature = getInputContent();
        if (TextUtils.isEmpty(signature)) {
            ToastUtils.showShort("请输入gameSignature");
            return;
        }
        String gameId = getGameId(signature);
        if (TextUtils.isEmpty(gameId)) {
            ToastUtils.showShort("signature格式不对");
            return;
        }
        GameModel gameModel = new GameModel();
        gameModel.gameId = gameId;
        gameModel.gameUrl = signature;
        gameModel.pathType = SUDOPGamePathType.SIGNATURE;
        QuickStartGameActivity.start(this, gameModel);
        GlobalSP.getSP().put(GlobalSP.KEY_GAME_SIGNATURE, signature);
    }

    private String getGameId(String signature) {
        try {
            JSONObject jsonObject = new JSONObject(signature);
            String game_meta_str = jsonObject.getString("game_meta_str");
            JSONObject metaStrJsonObj = new JSONObject(game_meta_str);
            return metaStrJsonObj.getString("game_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getInputContent() {
        Editable text = etContent.getText();
        if (text == null) {
            return null;
        }
        return text.toString();
    }

}
