package global.sud.gi.hello.app.common.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.MainThread;

import java.lang.ref.WeakReference;

/**
 * 冒烟测试使用的可复用应用内 Toast，保留居中提示且不申请悬浮窗权限。
 * 避免 Android 11 每次构造系统 Toast 都注册新的无障碍客户端；所有状态在主线程维护。
 */
public final class SUDGIToast implements Application.ActivityLifecycleCallbacks {

    private static final long LONG_DURATION_MILLIS = 3500;

    private final Application application;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final PopupWindow popup;
    private final TextView messageView;
    private final Runnable showRunnable = this::showInResumedActivity;
    private final Runnable dismissRunnable = this::cancel;
    private WeakReference<Activity> resumedActivity = new WeakReference<>(null);
    private long visibleUntil;
    private boolean closed;

    @MainThread
    public SUDGIToast(Application application) {
        this.application = application;
        // 与原 ToastUtils 默认提示使用相同布局；View 仅持有 Application，不缓存 Activity。
        View content = LayoutInflater.from(application).inflate(
                com.blankj.utilcode.R.layout.utils_toast_view, null, false);
        messageView = content.findViewById(android.R.id.message);
        popup = new PopupWindow(content, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popup.setTouchable(false);
        popup.setOutsideTouchable(false);
        popup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        application.registerActivityLifecycleCallbacks(this);
    }

    public void showLong(CharSequence message) {
        // 不把调用方的 Spannable / Activity 留在排队任务中。
        String text = message == null ? "" : message.toString();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showOnMainThread(text);
        } else {
            handler.post(() -> showOnMainThread(text));
        }
    }

    private void showOnMainThread(String message) {
        if (closed) {
            return;
        }
        messageView.setText(message);
        visibleUntil = SystemClock.uptimeMillis() + LONG_DURATION_MILLIS;
        handler.removeCallbacks(dismissRunnable);
        handler.postDelayed(dismissRunnable, LONG_DURATION_MILLIS);
        scheduleShow();
    }

    private void scheduleShow() {
        handler.removeCallbacks(showRunnable);
        // onActivityResumed 时窗口可能尚未添加，等当前生命周期分发结束后再显示。
        handler.post(showRunnable);
    }

    private void showInResumedActivity() {
        Activity activity = resumedActivity.get();
        if (closed || visibleUntil <= SystemClock.uptimeMillis() || activity == null
                || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        View decor = activity.getWindow().getDecorView();
        if (decor.getWindowToken() == null) {
            return;
        }
        if (popup.isShowing()) {
            popup.update();
            return;
        }
        try {
            popup.showAtLocation(decor, Gravity.CENTER, 0, 0);
        } catch (WindowManager.BadTokenException ignored) {
            // 页面已退出时丢弃本次显示，不持有失效窗口，也不影响游戏加载。
            popup.dismiss();
        }
    }

    @MainThread
    public void cancel() {
        visibleUntil = 0;
        handler.removeCallbacksAndMessages(null);
        popup.dismiss();
        messageView.setText(null);
    }

    @MainThread
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        cancel();
        resumedActivity.clear();
        application.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (closed) {
            return;
        }
        popup.dismiss();
        resumedActivity = new WeakReference<>(activity);
        if (visibleUntil > SystemClock.uptimeMillis()) {
            scheduleShow();
        }
    }

    private void detachFrom(Activity activity) {
        if (resumedActivity.get() == activity) {
            handler.removeCallbacks(showRunnable);
            popup.dismiss();
            resumedActivity.clear();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        detachFrom(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        detachFrom(activity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }

    @Override
    public void onActivityStarted(Activity activity) { }

    @Override
    public void onActivityStopped(Activity activity) { }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
}
