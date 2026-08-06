package global.sud.op.hello.ui.game.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseDialog;

public class ChooseImageDialog extends BaseDialog {

    private ChooseImageListener chooseImageListener;

    public ChooseImageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_choose_image;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_camera).setOnClickListener(v -> {
            if (chooseImageListener != null) {
                chooseImageListener.onClickCamera();
            }
            dismiss();
        });
        findViewById(R.id.tv_album).setOnClickListener(v -> {
            if (chooseImageListener != null) {
                chooseImageListener.onClickAlbum();
            }
            dismiss();
        });
        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            if (chooseImageListener != null) {
                chooseImageListener.onClickCancel();
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

    public void setChooseImageListener(ChooseImageListener chooseImageListener) {
        this.chooseImageListener = chooseImageListener;
    }

    public interface ChooseImageListener {
        void onClickCamera();

        void onClickAlbum();

        void onClickCancel();
    }

}
