package global.sud.op.hello.ui.game.sudedu.video;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

public class VideoPlayer {

    private static final String TAG = "VideoPlayer";

    private final TextureView textureView;
    private MediaPlayer mediaPlayer;
    private VideoPlayerListener listener;
    private boolean prepared = false;
    private int[] cropRect;
    private Surface surface;

    public VideoPlayer(Context context) {
        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                if (mediaPlayer != null && surface == null) {
                    surface = new Surface(surfaceTexture);
                    mediaPlayer.setSurface(surface);
                    applyCrop();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                applyCrop();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (surface != null) {
                    surface.release();
                    surface = null;
                }
                if (mediaPlayer != null) {
                    mediaPlayer.setSurface(null);
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
    }

    public TextureView getView() {
        return textureView;
    }

    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    public void setCropRect(int left, int top, int right, int bottom) {
        this.cropRect = new int[]{left, top, right, bottom};
        applyCrop();
    }

    public void play(String url) {
        internalPlay(url);
    }

    public void pause() {
        if (mediaPlayer != null && prepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null && prepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            try {
                if (prepared) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {
            }
            mediaPlayer.release();
            mediaPlayer = null;
            prepared = false;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null && prepared) {
            mediaPlayer.seekTo(msec);
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null && prepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null && prepared) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getVideoWidth() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoWidth();
        }
        return 0;
    }

    public int getVideoHeight() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoHeight();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && prepared && mediaPlayer.isPlaying();
    }

    public void release() {
        stop();
    }

    private void internalPlay(String url) {
        stop();
        prepared = false;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(false);

            if (textureView.isAvailable()) {
                surface = new Surface(textureView.getSurfaceTexture());
                mediaPlayer.setSurface(surface);
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                prepared = true;
                applyCrop();
                if (listener != null) {
                    listener.onPrepared();
                }
                mp.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (listener != null) {
                    listener.onCompletion();
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (listener != null) {
                    listener.onError("播放错误 what=" + what + " extra=" + extra);
                }
                return true;
            });
            mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                if (listener != null) {
                    listener.onBuffering(percent);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            if (listener != null) {
                listener.onError("播放失败: " + e.getMessage());
            }
        }
    }

    private void applyCrop() {
        if (cropRect == null) return;

        int vw = mediaPlayer != null ? mediaPlayer.getVideoWidth() : 0;
        int vh = mediaPlayer != null ? mediaPlayer.getVideoHeight() : 0;
        if (vw == 0 || vh == 0) return;

        float viewWidth = textureView.getWidth();
        float viewHeight = textureView.getHeight();
        if (viewWidth <= 0 || viewHeight <= 0) return;

        int left = cropRect[0];
        int top = cropRect[1];
//        int right = cropRect[2];
//        int bottom = cropRect[3];
//        int cropWidth = right - left;
//        int cropHeight = bottom - top;

        // 算法不完善，仅用于裁剪视频中间部分
        Matrix matrix = new Matrix();
        float scaleX = vw/viewWidth;
        float scaleY = vh/viewHeight;
        float scaleXX = 1 / scaleX;
        matrix.postScale(scaleX, scaleY);
        matrix.postTranslate(-left, -top);
        matrix.postScale(scaleXX, scaleXX);

        textureView.setTransform(matrix);
    }
}
