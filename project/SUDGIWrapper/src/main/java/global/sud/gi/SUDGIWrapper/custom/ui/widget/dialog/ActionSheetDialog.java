package global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import global.sud.gi.SUDGIWrapper.custom.base.BaseDialog;
import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.SudGIPWrapper.R;
import global.sud.op.runtime.core.model.SUDOPShowActionSheetParams;

public class ActionSheetDialog extends BaseDialog {

    private SUDOPShowActionSheetParams params;
    private OnActionSheetClickListener onActionSheetClickListener;

    public ActionSheetDialog(@NonNull Context context, SUDOPShowActionSheetParams params) {
        super(context);
        this.params = params;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sud_op_wck_dialog_action_sheet;
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
        initItemList();
    }

    private void initItemList() {
        List<String> itemList = params.itemList;
        if (itemList == null || itemList.size() == 0) {
            return;
        }
        LinearLayout container = findViewById(R.id.container_item);
        int height = DensityUtils.dp2px(getContext(), 52);
        int lineHeight = DensityUtils.dp2px(getContext(), 0.5f);
        int textColor;
        try {
            textColor = Color.parseColor(params.itemColor);
        } catch (Exception e) {
            textColor = Color.parseColor("#303133");
        }
        for (int i = 0; i < itemList.size(); i++) {
            String item = itemList.get(i);
            if (i > 0) {
                View viewLine = new View(getContext());
                viewLine.setBackgroundColor(Color.parseColor("#e6e7ef"));
                container.addView(viewLine, LinearLayout.LayoutParams.MATCH_PARENT, lineHeight);
            }
            TextView tv = new TextView(getContext());
            tv.setTextSize(14);
            tv.setTextColor(textColor);
            tv.setGravity(Gravity.CENTER);
            tv.setText(item);
            container.addView(tv, LinearLayout.LayoutParams.MATCH_PARENT, height);
            int finalI = i;
            tv.setOnClickListener(v -> {
                if (onActionSheetClickListener != null) {
                    onActionSheetClickListener.onClickItem(finalI);
                }
                dismiss();
            });
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            if (onActionSheetClickListener != null) {
                onActionSheetClickListener.cancel();
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

    public void setOnActionSheetClickListener(OnActionSheetClickListener onActionSheetClickListener) {
        this.onActionSheetClickListener = onActionSheetClickListener;
    }

    public interface OnActionSheetClickListener {
        void onClickItem(int index);

        void cancel();
    }

}
