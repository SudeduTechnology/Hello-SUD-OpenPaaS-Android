package global.sud.gi.SUDGIWrapper.custom.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import java.util.Locale;

public class VideoGestureView extends View {
    public boolean enableProgressGesture = true; // 是否启用手势控制播放进度
    public boolean enablePlayGesture = true;     // 是否开启双击播放的手势

    public static abstract class Callback {

        /**
         * 视频总时长，单位 ms
         */
        public long getDuration() {
            return 0L;
        }

        /**
         * 当前播放进度，单位 ms
         */
        public long getCurrentPosition() {
            return 0L;
        }

        /**
         * 开始滑动进度
         */
        public void onSeekStart(long startPositionMs, long durationMs) {

        }

        /**
         * 滑动过程中
         */
        public void onSeekChange(long targetPositionMs, long durationMs, long offsetMs) {

        }

        /**
         * 手指松开，真正 seek
         */
        public void onSeekEnd(long targetPositionMs, long durationMs) {

        }

        /**
         * seek 被取消
         */
        public void onSeekCancel() {

        }

        /**
         * 双击事件，一般用来播放 / 暂停
         */
        public void onDoubleTap(float x, float y) {

        }

        /**
         * 单击事件，一般用来显示 / 隐藏控制栏
         */
        public void onSingleTap(float x, float y) {

        }
    }

    private static final int MODE_NONE = 0;
    private static final int MODE_SEEK = 1;
    private static final int MODE_IGNORE = 2;

    private GestureDetector gestureDetector;
    private Callback callback;

    private int touchSlop;
    private int gestureMode = MODE_NONE;

    private long seekStartPositionMs;
    private long seekTargetPositionMs;

    /**
     * 快进快退灵敏度。
     * <p>
     * 1.0f 表示手指横向滑动整个 View 宽度，对应整个视频时长。
     * 0.5f 表示手指横向滑动整个 View 宽度，对应视频时长的一半。
     */
    private float seekSensitivity = 0.7f;

    public VideoGestureView(Context context) {
        this(context, null);
    }

    public VideoGestureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoGestureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setFocusable(true);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (callback != null) {
                    callback.onSingleTap(e.getX(), e.getY());
                }

                performClick();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!enablePlayGesture) {
                    return false;
                }
                if (callback != null) {
                    callback.onDoubleTap(e.getX(), e.getY());
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!enableProgressGesture) {
                    return false;
                }
                if (e1 == null || e2 == null) {
                    return false;
                }

                if (callback == null) {
                    return false;
                }

                long durationMs = callback.getDuration();
                if (durationMs <= 0) {
                    return false;
                }

                float dx = e2.getX() - e1.getX();
                float dy = e2.getY() - e1.getY();

                if (gestureMode == MODE_IGNORE) {
                    return false;
                }

                if (gestureMode == MODE_NONE) {
                    boolean moveEnough = Math.abs(dx) > touchSlop || Math.abs(dy) > touchSlop;
                    if (!moveEnough) {
                        return false;
                    }

                    boolean horizontal = Math.abs(dx) >= Math.abs(dy);
                    if (!horizontal) {
                        gestureMode = MODE_IGNORE;
                        return false;
                    }

                    gestureMode = MODE_SEEK;

                    seekStartPositionMs = callback.getCurrentPosition();
                    seekTargetPositionMs = seekStartPositionMs;

                    callback.onSeekStart(seekStartPositionMs, durationMs);
                }

                if (gestureMode == MODE_SEEK) {
                    handleSeekGesture(dx, durationMs);
                    return true;
                }

                return false;
            }
        });
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    public void setSeekSensitivity(float seekSensitivity) {
        this.seekSensitivity = seekSensitivity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                gestureMode = MODE_NONE;

                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                gestureDetector.onTouchEvent(event);
                return true;

            case MotionEvent.ACTION_MOVE:
                gestureDetector.onTouchEvent(event);
                return true;

            case MotionEvent.ACTION_UP:
                gestureDetector.onTouchEvent(event);

                if (enableProgressGesture && gestureMode == MODE_SEEK) {
                    long durationMs = callback != null ? callback.getDuration() : 0L;
                    if (callback != null && durationMs > 0) {
                        callback.onSeekEnd(seekTargetPositionMs, durationMs);
                    }
                }

                resetState();

                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                return true;

            case MotionEvent.ACTION_CANCEL:
                if (enableProgressGesture && gestureMode == MODE_SEEK && callback != null) {
                    callback.onSeekCancel();
                }

                resetState();

                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                return true;
        }

        return gestureDetector.onTouchEvent(event);
    }

    private void handleSeekGesture(float dx, long durationMs) {
        if (getWidth() <= 0) {
            return;
        }

        float percent = dx / getWidth();

        long offsetMs = (long) (durationMs * percent * seekSensitivity);

        seekTargetPositionMs = seekStartPositionMs + offsetMs;

        if (seekTargetPositionMs < 0) {
            seekTargetPositionMs = 0;
        }

        if (seekTargetPositionMs > durationMs) {
            seekTargetPositionMs = durationMs;
        }

        if (callback != null) {
            callback.onSeekChange(seekTargetPositionMs, durationMs, offsetMs);
        }
    }

    private void resetState() {
        gestureMode = MODE_NONE;
        seekStartPositionMs = 0L;
        seekTargetPositionMs = 0L;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public static String formatTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60 % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }
}
