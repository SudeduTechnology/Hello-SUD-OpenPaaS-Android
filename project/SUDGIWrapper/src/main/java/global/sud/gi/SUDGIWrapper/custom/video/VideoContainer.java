package global.sud.gi.SUDGIWrapper.custom.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;

import java.util.Locale;

import global.sud.gi.SudGIPWrapper.R;
import global.sud.op.runtime.core.video.SUDOPVideo;

public class VideoContainer extends ConstraintLayout {

    private ImageView ivPoster;
    private ImageView ivFullScreen;
    private ImageView ivControl;
    private SeekBar seekBar;
    private TextView tvProgress;
    private TextView tvTotal;
    private ImageView ivVideoDestroy;
    private VideoGestureView videoGestureView;
    private View containerBottomControl;
    private View ivBigPlay;

    private FrameLayout gameContainer;

    private SUDOPVideo video;

    private boolean isTrackingTouch;
    private boolean isGesture;
    private boolean isFullScreen;
    private boolean showCenterPlayBtn;
    private Activity activity;
    private int currentOrientation;
    private int currentTime;

    public VideoContainer(@NonNull Activity context, FrameLayout gameContainer) {
        super(context);
        this.activity = context;
        this.gameContainer = gameContainer;
        initView(context);
        setListeners();
    }

    private void initView(Context context) {
        inflate(context, R.layout.sud_op_wck_view_video_container, this);
        ivPoster = findViewById(R.id.iv_poster);
        ivFullScreen = findViewById(R.id.iv_video_fullscreen);
        ivControl = findViewById(R.id.iv_video_control);
        seekBar = findViewById(R.id.seek_bar);
        tvProgress = findViewById(R.id.tv_progress);
        tvTotal = findViewById(R.id.tv_total);
        ivVideoDestroy = findViewById(R.id.iv_video_destroy);
        videoGestureView = findViewById(R.id.video_gesture_view);
        containerBottomControl = findViewById(R.id.container_bottom_control);
        ivBigPlay = findViewById(R.id.iv_big_play);
    }

    private void setListeners() {
        ivControl.setOnClickListener(v -> {
            onClickControl();
        });
        ivVideoDestroy.setOnClickListener(v -> {
            onClickDestroy();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    processStartTrackingTouch();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                changeProgress(progress);
                isTrackingTouch = false;
            }
        });

        videoGestureView.setCallback(new VideoGestureView.Callback() {
            @Override
            public long getDuration() {
                long duration = getVideoDuration() * 1000;
                LogUtils.d("video: duration:" + duration);
                return duration;
            }

            @Override
            public long getCurrentPosition() {
                long position = getVideoPosition() * 1000;
                LogUtils.d("video: position:" + position);
                return position;
            }

            @Override
            public void onSeekStart(long startPositionMs, long durationMs) {
                isGesture = true;
            }

            @Override
            public void onSeekChange(long targetPositionMs, long durationMs, long offsetMs) {
                if (durationMs == 0) {
                    return;
                }
                // 如果你有进度条，也可以临时更新
                int progress = (int) (targetPositionMs * 1.0d / durationMs * 100);
                seekBar.setProgress(progress);
                tvProgress.setText(formatTime((int) (targetPositionMs / 1000)));
            }

            @Override
            public void onSeekEnd(long targetPositionMs, long durationMs) {
                if (durationMs == 0) {
                    return;
                }
                seek((int) (targetPositionMs / 1000));
                isGesture = false;
            }

            @Override
            public void onSeekCancel() {
                isGesture = false;
            }

            @Override
            public void onDoubleTap(float x, float y) {
                onClickControl();
            }

            @Override
            public void onSingleTap(float x, float y) {
            }
        });
        ivFullScreen.setOnClickListener(v -> onClickFullScreen());
        ivBigPlay.setOnClickListener(v -> onClickControl());
    }

    private void onClickFullScreen() {
        if (isFullScreen) {
            exitFullScreen();
        } else {
            requestFullScreen(-90);
        }
    }

    private int getVideoDuration() {
        SUDOPVideo video = this.video;
        if (video != null) {
            return video.videoControl.getDuration();
        }
        return 0;
    }

    private int getVideoPosition() {
        SUDOPVideo video = this.video;
        if (video != null) {
            return video.videoControl.getPosition();
        }
        return 0;
    }

    private void processStartTrackingTouch() {
        SUDOPVideo video = this.video;
        if (video == null) {
            return;
        }
        int duration = video.videoControl.getDuration();
        int time = (int) (seekBar.getProgress() * 1.0f / 100 * duration);
        tvProgress.setText(formatTime(time));
    }

    private void changeProgress(int progress) {
        int duration = video.videoControl.getDuration();
        int time = (int) (progress * 1.0f / 100 * duration);
        seek(time);
    }

    private void seek(int time) {
        SUDOPVideo video = this.video;
        if (video == null) {
            return;
        }
        video.videoControl.seek(time);
    }

    private void onClickDestroy() {
        exitFullScreen();
        SUDOPVideo video = this.video;
        if (video == null) {
            return;
        }
        video.videoControl.destroy();
    }

    private void onClickControl() {
        SUDOPVideo video = this.video;
        if (video == null) {
            return;
        }
        boolean paused = video.videoControl.isPaused();
        if (paused) {
            video.videoControl.play();
        } else {
            video.videoControl.pause();
        }
    }

    public void initVideo(SUDOPVideo video) {
        currentOrientation = activity.getRequestedOrientation();
        this.video = video;
        TextureView videoView = video.videoView;
        LayoutParams videoViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(videoView, 0, videoViewLayoutParams);

        setVideoListeners(video);

        // 封面，需要有展示的时机，以及不展示的时机
        if (!TextUtils.isEmpty(video.poster)) {
            Glide.with(ivPoster).load(video.poster).into(ivPoster);
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(video.width, video.height);
        layoutParams.topMargin = video.y;
        layoutParams.setMarginStart(video.x);
        if (video.underGameView) {
            gameContainer.addView(this, 0, layoutParams);
        } else {
            gameContainer.addView(this, layoutParams);
        }
        if (!video.controls) {
            containerBottomControl.setVisibility(View.GONE);
            ivVideoDestroy.setVisibility(View.GONE);
        }
        if (!video.showProgress) {
            seekBar.setVisibility(View.GONE);
        }
        showCenterPlayBtn = video.showCenterPlayBtn;
        if (!showCenterPlayBtn) {
            ivBigPlay.setVisibility(View.GONE);
        }
        videoGestureView.enableProgressGesture = video.enableProgressGesture;
        videoGestureView.enablePlayGesture = video.enablePlayGesture;
        try {
            setBackgroundColor(Color.parseColor(video.backgroundColor));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVideoListeners(SUDOPVideo params) {
        params.videoControl.setOnEndedListener(() -> {
            ivControl.setImageResource(R.drawable.ic_video_play);
            tvProgress.setText(formatTime(0));
            ivBigPlay.setVisibility(View.VISIBLE);
        });
        params.videoControl.setOnDestroyListener(() -> {
            gameContainer.removeView(this);
        });
        params.videoControl.setOnPlayListener(() -> {
            onPlay();
        });
        params.videoControl.setOnPauseListener(() -> {
            onPause();
        });
        params.videoControl.setOnTimeUpdateListener((currentTime, duration) -> {
            this.currentTime = currentTime;
            onTimeUpdate(currentTime, duration);
        });
        params.videoControl.setRequestFullScreenListener(direction -> requestFullScreen(direction));
        params.videoControl.setExitFullScreenListener(() -> exitFullScreen());
        params.videoControl.setOnProgressListener((bufferedTime, duration) -> {
            onTimeUpdate(this.currentTime, duration);
        });
    }

    private void requestFullScreen(int direction) {
        if (isFullScreen) {
            return;
        }
        isFullScreen = true;
        switch (direction) {
            case 0: // 正常竖向
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 90: // 屏幕逆时针90度
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case -90: // 屏幕顺时针90度
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.topMargin = 0;
            layoutParams.setMarginStart(0);
            setLayoutParams(params);
        }
    }

    private void exitFullScreen() {
        if (!isFullScreen) {
            return;
        }
        isFullScreen = false;
        SUDOPVideo video = this.video;
        if (video == null) {
            return;
        }
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;
            layoutParams.width = video.width;
            layoutParams.height = video.height;
            layoutParams.topMargin = video.y;
            layoutParams.setMarginStart(video.x);
            setLayoutParams(layoutParams);
        }
        activity.setRequestedOrientation(currentOrientation);
    }

    private void onTimeUpdate(int currentTime, int duration) {
        if (isTrackingTouch || isGesture) {
            return;
        }
        tvProgress.setText(formatTime(currentTime));
        tvTotal.setText(formatTime(duration));
        int progress = (int) (currentTime * 1.0f / duration * 100);
        seekBar.setProgress(progress);
    }

    private void onPause() {
        ivControl.setImageResource(R.drawable.ic_video_play);
        if (showCenterPlayBtn) {
            ivBigPlay.setVisibility(View.VISIBLE);
        }
    }

    private void onPlay() {
        ivControl.setImageResource(R.drawable.ic_video_pause);
        ivPoster.setVisibility(View.GONE);
        ivBigPlay.setVisibility(View.GONE);
    }

    public String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

}
