package global.sud.op.hello.common.base;

import android.os.Bundle;
import android.content.pm.ActivityInfo;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import global.sud.op.hello.R;
import global.sud.op.hello.common.utils.SystemUtils;
import me.jessyan.autosize.internal.CustomAdapt;

public abstract class BaseActivity extends RxAppCompatActivity implements CustomAdapt {

    private static final float DEFAULT_DESIGN_WIDTH_DP = 375f;
    private static final float TV_FORM_DESIGN_HEIGHT_DP = 540f;

    protected BaseActivity context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyOrientationPolicy();
        if (beforeSetContentView()) {
            finish();
            return;
        }
        int layoutId = getLayoutId();
        if (layoutId > 0) {
            setContentView(layoutId);
        }
        setStatusBar();
        initWidget();
        initData();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyOrientationPolicy();
    }

    /**
     * 在SetContentView之前会调用
     *
     * @return 返回true则直接销毁页面，不进行后续初始化
     */
    protected boolean beforeSetContentView() {
        return false;
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(getStatusBarColorResId()).statusBarDarkFont(true)
                .fitsSystemWindows(true).init();
    }

    /**
     * 获取状态栏的颜色
     *
     * @return 返回资源id
     */
    protected int getStatusBarColorResId() {
        return R.color.white;
    }

    protected abstract int getLayoutId();

    protected void initWidget() {
    }

    protected void initData() {
    }

    protected void setListeners() {
    }

    protected boolean useTvFormHeightAdapt() {
        return true;
    }

    protected float getTvFormDesignHeightDp() {
        return TV_FORM_DESIGN_HEIGHT_DP;
    }

    protected float getDefaultDesignWidthDp() {
        return DEFAULT_DESIGN_WIDTH_DP;
    }

    @Override
    public boolean isBaseOnWidth() {
        return !useTvFormHeightAdapt();
    }

    @Override
    public float getSizeInDp() {
        if (useTvFormHeightAdapt()) {
            return getTvFormDesignHeightDp();
        }
        return getDefaultDesignWidthDp();
    }

    protected int getPreferredOrientation() {
        return SystemUtils.isTV(this) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    protected void applyOrientationPolicy() {
        int orientation = getPreferredOrientation();
        if (getRequestedOrientation() != orientation) {
            setRequestedOrientation(orientation);
        }
    }

}
