package global.sud.op.hello.ui.game.sudedu.video;

public interface VideoPlayerListener {

    void onPrepared();

    void onCompletion();

    void onError(String message);

    void onBuffering(int percent);
}
