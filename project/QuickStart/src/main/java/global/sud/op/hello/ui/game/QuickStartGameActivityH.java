package global.sud.op.hello.ui.game;

import android.content.pm.ActivityInfo;

/**
 * 横屏游戏页面
 * Game page
 */
public class QuickStartGameActivityH extends QuickStartGameActivity {

    @Override
    protected int getPreferredOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

}
