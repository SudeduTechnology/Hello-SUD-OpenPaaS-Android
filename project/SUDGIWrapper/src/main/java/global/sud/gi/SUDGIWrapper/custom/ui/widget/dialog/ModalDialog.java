package global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import global.sud.gi.SUDGIWrapper.custom.base.BaseDialog;
import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.SUDGIWrapper.custom.base.utils.HSTextUtils;
import global.sud.gi.SudGIPWrapper.R;
import global.sud.op.runtime.core.model.SUDOPShowModalParams;

public class ModalDialog extends BaseDialog {

    private SUDOPShowModalParams params;
    private TextView tvTitle;
    private TextView tvContent;
    private EditText etInput;
    private View viewLineVertical;
    private TextView tvCancel;
    private TextView tvConfirm;
    private OnClickListener onClickListener;

    public ModalDialog(@NonNull Context context, SUDOPShowModalParams params) {
        super(context);
        this.params = params;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sud_op_wck_dialog_modal;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(getContext(), 275);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        etInput = findViewById(R.id.et_input);
        viewLineVertical = findViewById(R.id.view_line_vertical);
        tvCancel = findViewById(R.id.tv_cancel);
        tvConfirm = findViewById(R.id.tv_confirm);
    }

    @Override
    protected void initData() {
        super.initData();
        tvTitle.setText(params.title);
        tvContent.setText(params.content);
        if (params.editable) {
            etInput.setHint(params.placeholderText);
            etInput.setVisibility(View.VISIBLE);
        } else {
            etInput.setVisibility(View.GONE);
        }

        if (params.showCancel) {
            viewLineVertical.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(params.cancelText)) {
                tvCancel.setText(R.string.sud_op_wck_common_cancel);
            } else {
                tvCancel.setText(params.cancelText);
            }
            int cancelColor;
            try {
                cancelColor = Color.parseColor(params.cancelColor);
            } catch (Exception e) {
                cancelColor = Color.parseColor("#303133");
            }
            tvCancel.setTextColor(cancelColor);
        } else {
            viewLineVertical.setVisibility(View.GONE);
            tvCancel.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(params.confirmText)) {
            tvConfirm.setText(R.string.sud_op_wck_common_confirm);
        } else {
            tvConfirm.setText(params.confirmText);
        }
        int confirmColor;
        try {
            confirmColor = Color.parseColor(params.confirmColor);
        } catch (Exception e) {
            confirmColor = Color.parseColor("#007aff");
        }
        tvConfirm.setTextColor(confirmColor);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        tvCancel.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(HSTextUtils.getText(etInput), false, true);
            }
            dismiss();
        });
        tvConfirm.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(HSTextUtils.getText(etInput), true, false);
            }
            dismiss();
        });
    }

    @Override
    protected boolean cancelable() {
        return false;
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String content, boolean confirm, boolean cancel);
    }

}
