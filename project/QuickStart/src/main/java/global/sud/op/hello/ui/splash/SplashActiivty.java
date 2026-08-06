package global.sud.op.hello.ui.splash;

import android.content.Intent;

import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.ui.main.MainActivity;
import me.jessyan.autosize.internal.CancelAdapt;

public class SplashActiivty extends BaseActivity implements CancelAdapt {

    @Override
    protected boolean beforeSetContentView() {
        if (!this.isTaskRoot()) {
            //此操作是为了防止重复启动，主要是发生在应用安装成功后打开APP，然后回到launcher程序再打开时造成的问题
            if (getIntent() != null) {
                if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(getIntent().getAction())) {
                    return true;
                }
            }
        }
        return super.beforeSetContentView();
    }

    @Override
    protected void setStatusBar() {
    }

    @Override
    protected boolean useTvFormHeightAdapt() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
