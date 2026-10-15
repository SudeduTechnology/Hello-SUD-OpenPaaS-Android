package global.sud.gi.hello.app.game.gi.widget;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.blankj.utilcode.util.BarUtils;
import com.gyf.immersionbar.ImmersionBar;

import global.sud.gi.hello.R;
import global.sud.gi.SUDGIWrapper.custom.base.BaseDialogFragment;
import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.hello.app.common.utils.ViewUtils;

/**
 * 房间内，更多-弹窗
 */
public class GIGameRoomMoreDialog extends BaseDialogFragment {

    private View containerExit;
    private View.OnClickListener exitOnClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_gi_room_more;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return DensityUtils.dp2px(146) + BarUtils.getStatusBarHeight();
    }

    @Override
    protected int getGravity() {
        return Gravity.TOP;
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setDimAmount(0);
        window.setWindowAnimations(R.style.TopToBottomAnim);
        ImmersionBar.with(this).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        containerExit = findViewById(R.id.container_exit);
        ViewUtils.addMarginTop(containerExit, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        containerExit.setOnClickListener(exitOnClickListener);
    }

    /** 设置退出按钮点击事件 */
    public void setExitOnClickListener(View.OnClickListener listener) {
        exitOnClickListener = listener;
    }

}
