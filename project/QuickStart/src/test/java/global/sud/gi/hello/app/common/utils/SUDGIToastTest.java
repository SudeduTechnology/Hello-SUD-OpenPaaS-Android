package global.sud.gi.hello.app.common.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;

import java.lang.ref.WeakReference;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28, 30, 34}, application = SUDGIToastTest.SUDGITestApplication.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class SUDGIToastTest {

    private SUDGITestApplication application;
    private SUDGIToast toast;
    private ActivityController<Activity> home;
    private ActivityController<Activity> game;

    @Before
    public void setUp() {
        application = (SUDGITestApplication) RuntimeEnvironment.getApplication();
        Utils.init(application);
        toast = new SUDGIToast(application);
        home = Robolectric.buildActivity(Activity.class).setup().visible();
        idle();
    }

    @After
    public void tearDown() {
        toast.close();
        if (game != null) {
            game.close();
        }
        home.close();
        idle();
    }

    @Test
    public void repeatedShowsReuseTheWindowAndApplicationContent() {
        PopupWindow popup = popup();
        View content = popup.getContentView();
        for (int index = 0; index < 1000; index++) {
            toast.showLong("Next game " + index);
            idle();
            assertSame(popup, popup());
            assertSame(content, popup.getContentView());
        }
        assertTrue(popup.isShowing());
        assertEquals("Next game 999", message().getText().toString());
        assertSame(application, content.getContext());
        assertEquals(1, application.toastRegistrations);
        assertFalse(popup.isFocusable());
        assertFalse(popup.isTouchable());
        assertEquals(Gravity.CENTER, (int) ReflectionHelpers.getField(popup, "mGravity"));
    }

    @Test
    public void newerMessageRestartsTheLongDuration() {
        toast.showLong("first");
        idle();
        advance(3000);
        toast.showLong("second");
        idle();
        advance(501);
        assertTrue(popup().isShowing());
        assertEquals("second", message().getText().toString());
        advance(2999);
        assertFalse(popup().isShowing());
    }

    @Test
    public void pendingMessageMovesToGameWithoutKeepingTheOldWindow() {
        toast.showLong("next toggle");
        idle();
        home.pause().stop();
        assertFalse(popup().isShowing());
        assertNull(owner().get());
        game = Robolectric.buildActivity(Activity.class).setup().visible();
        idle();
        assertTrue(popup().isShowing());
        assertSame(game.get(), owner().get());
        assertEquals("next toggle", message().getText().toString());
        advance(3500);
        assertFalse(popup().isShowing());
    }

    @Test
    public void expiredMessageDoesNotReappearAfterReturningFromBackground() {
        toast.showLong("expired");
        idle();
        home.pause().stop();
        advance(3500);
        home.restart().start().resume().visible();
        idle();
        assertFalse(popup().isShowing());
    }

    @Test
    public void cancellingBeforeQueuedShowPreventsTheWindowAppearing() {
        toast.showLong("cancelled");
        toast.cancel();
        idle();
        assertFalse(popup().isShowing());
        assertEquals("", message().getText().toString());
    }

    @Test
    public void cancellingAlsoDiscardsQueuedWorkerRequests() throws Exception {
        Thread worker = new Thread(() -> toast.showLong("cancelled worker"));
        worker.start();
        worker.join();
        toast.cancel();
        idle();
        assertFalse(popup().isShowing());
        assertEquals("", message().getText().toString());
    }

    @Test
    public void disabledNotificationsDoNotHideTheInAppToast() {
        android.app.NotificationManager manager = application.getSystemService(
                android.app.NotificationManager.class);
        shadowOf(manager).setNotificationsEnabled(false);
        toast.showLong("still visible");
        idle();
        assertTrue(popup().isShowing());
        assertEquals("still visible", message().getText().toString());
    }

    @Test
    public void closeCancelsPendingWorkAndUnregistersOnlyOnce() throws Exception {
        Thread worker = new Thread(() -> toast.showLong("queued"));
        worker.start();
        worker.join();
        toast.close();
        toast.close();
        toast.showLong("late callback");
        idle();
        advance(4000);
        assertFalse(popup().isShowing());
        assertNull(owner().get());
        assertEquals(0, application.toastRegistrations);
        assertEquals("", message().getText().toString());
    }

    @Test
    public void workerThreadCanRequestAVisibleToast() throws Exception {
        Thread worker = new Thread(() -> toast.showLong("worker"));
        worker.start();
        worker.join();
        assertFalse(popup().isShowing());
        idle();
        assertTrue(popup().isShowing());
        assertEquals("worker", message().getText().toString());
    }

    @Test
    public void finishingActivityDoesNotReceiveAQueuedWindow() {
        toast.showLong("finishing");
        home.get().finish();
        idle();
        assertFalse(popup().isShowing());
    }

    private PopupWindow popup() {
        return ReflectionHelpers.getField(toast, "popup");
    }

    private TextView message() {
        return popup().getContentView().findViewById(android.R.id.message);
    }

    private WeakReference<Activity> owner() {
        return ReflectionHelpers.getField(toast, "resumedActivity");
    }

    private void idle() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private void advance(long millis) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(millis));
    }

    public static class SUDGITestApplication extends Application {
        private int toastRegistrations;

        @Override
        public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            super.registerActivityLifecycleCallbacks(callback);
            if (callback instanceof SUDGIToast) {
                toastRegistrations++;
            }
        }

        @Override
        public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
            super.unregisterActivityLifecycleCallbacks(callback);
            if (callback instanceof SUDGIToast) {
                toastRegistrations--;
            }
        }
    }
}
