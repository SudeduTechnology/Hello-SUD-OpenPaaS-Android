package global.sud.gi.hello.app.game.gi;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import global.sud.gi.SUDGIWrapper.gi.model.GIGameViewInfoModel;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class GIBaseGameViewModelTest {

    private ActivityController<Activity> controller;
    private Activity activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Activity.class).setup();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        controller.destroy();
    }

    @Test
    public void destroyCancelsRetryAndIsIdempotent() {
        TestViewModel viewModel = new TestViewModel();

        viewModel.switchGame(activity, "10000", 1461227817776713818L);
        assertEquals(1, viewModel.codeRequestCount);

        viewModel.destroyMG();
        viewModel.destroyMG();
        shadowOf(Looper.getMainLooper()).idleFor(6, TimeUnit.SECONDS);

        assertEquals(1, viewModel.codeRequestCount);
        assertEquals(3, viewModel.cancelCount);
        assertEquals(3, viewModel.removeViewCount);
        assertEquals(0, viewModel.getPlayingGameId());
    }

    @Test
    public void closeGameDoesNotRequestCode() {
        TestViewModel viewModel = new TestViewModel();

        viewModel.switchGame(activity, "10000", 0);

        assertEquals(0, viewModel.codeRequestCount);
        assertEquals(0, viewModel.getPlayingGameId());
        assertEquals(1, viewModel.removeViewCount);
    }

    private static final class TestViewModel extends GIBaseGameViewModel {
        int codeRequestCount;
        int cancelCount;
        int removeViewCount;

        @Override
        protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
            codeRequestCount++;
            listener.onFailed();
        }

        @Override
        protected String getUserId() {
            return "user";
        }

        @Override
        protected String getAppId() {
            return "app";
        }

        @Override
        protected String getAppKey() {
            return "key";
        }

        @Override
        protected String getLanguageCode() {
            return "zh-CN";
        }

        @Override
        protected void getGameRect(GIGameViewInfoModel gameViewInfoModel) {
        }

        @Override
        protected boolean isTestEnv() {
            return true;
        }

        @Override
        protected void onAddGameView(View gameView) {
        }

        @Override
        protected void onRemoveGameView() {
            removeViewCount++;
        }

        @Override
        protected void traceMsg(String msg) {
        }

        @Override
        protected void cancelPendingRequests() {
            cancelCount++;
        }
    }
}
