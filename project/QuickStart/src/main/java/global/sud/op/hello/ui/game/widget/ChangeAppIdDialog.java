package global.sud.op.hello.ui.game.widget;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;

import global.sud.op.hello.R;
import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.base.BaseDialogFragment;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.common.utils.HSTextUtils;
import global.sud.op.runtime.core.SUDOP;


public class ChangeAppIdDialog extends BaseDialogFragment {

    private EditText etAppId;
    private EditText etAppKey;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_change_app_id;
    }

    @Override
    protected int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        etAppId = findViewById(R.id.et_app_id);
        etAppKey = findViewById(R.id.et_app_key);
    }

    @Override
    protected void initData() {
        super.initData();
        etAppId.setText(GlobalSP.getSP().getString(GlobalSP.KEY_APP_ID, AppConfig.SudGIP_APP_ID));
        etAppKey.setText(GlobalSP.getSP().getString(GlobalSP.KEY_APP_KEY, AppConfig.SudGIP_APP_KEY));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_success).setOnClickListener(v -> {
            String appId = HSTextUtils.getText(etAppId);
            String appKey = HSTextUtils.getText(etAppKey);
            if (TextUtils.isEmpty(appId)) {
                ToastUtils.showShort("请输入appId");
                return;
            }
            if (TextUtils.isEmpty(appKey)) {
                ToastUtils.showShort("请输入appKey");
                return;
            }
            AppConfig.SudGIP_APP_ID = appId;
            AppConfig.SudGIP_APP_KEY = appKey;
            GlobalSP.getSP().put(GlobalSP.KEY_APP_ID, appId);
            GlobalSP.getSP().put(GlobalSP.KEY_APP_KEY, appKey);
            ToastUtils.showShort("保存成功");
            dismiss();
            SUDOP.uninitSDK(null);
        });
    }

}
