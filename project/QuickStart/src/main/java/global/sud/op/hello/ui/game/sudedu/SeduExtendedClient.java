package global.sud.op.hello.ui.game.sudedu;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import com.blankj.utilcode.util.ToastUtils;

import org.json.JSONObject;

import java.util.Random;

import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.common.utils.SystemUtils;
import global.sud.op.hello.ui.game.QuickStartGameActivity;
import global.sud.op.hello.ui.game.sudedu.video.VideoPlayerListener;
import global.sud.op.hello.ui.game.sudedu.video.VideoPlayer;
import global.sud.op.runtime.api.SUDRTJSCallback;
import global.sud.op.runtime.client.options.CommonOptions;
import global.sud.runtime.annotation.SUDASync;
import global.sud.runtime.annotation.SUDSync;

public final class SeduExtendedClient {
    private static final String TAG = "SeduExtendedClient";
    public static final int REQUEST_CODE_RECORD_AUDIO = 20011;
    private Activity activity;
    private SUDRTJSCallback requestRecordPermissionCallback;
    private SUDRTJSCallback onKeyDownCallback;
    private VideoPlayer videoPlayer;
    private WebView webView;
    SUDRTJSCallback onWebviewCallback;


    public SeduExtendedClient(Activity activity){
        this.activity = activity;
    }

    @SUDSync
    public void log(String tag, String msg){
        Log.d(tag, msg);
    }

    @SUDASync
    public void requestRecordPermission(SUDRTJSCallback requestRecordPermissionCallback) {
        if(SystemUtils.isTV(activity))
        {
            invokeRequestRecordPermissionCallback(false);
            return;
        }
        this.requestRecordPermissionCallback = requestRecordPermissionCallback;
        boolean hasPermisson = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermisson = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        }
        if(hasPermisson){
            GlobalSP.getSP().put(GlobalSP.KEY_RECORD_PERMISSION, 1);
            invokeRequestRecordPermissionCallback(true);
        }else{
            // 0未设置，1仅该app允许或仅本次启动app允许，2拒绝
            int state = GlobalSP.getSP().getInt(GlobalSP.KEY_RECORD_PERMISSION, 0);
            if(state == 0 || state == 1){
                // 出现3个选项 1.仅该app允许 2.仅本次启动app允许 3.拒绝
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_RECORD_AUDIO);
            }else{
                invokeRequestRecordPermissionCallback(false);
            }
        }
    }

    public void onRequestRecordPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == SeduExtendedClient.REQUEST_CODE_RECORD_AUDIO) {
            boolean granted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            ToastUtils.showShort(granted ? "麦克风已开启" : "未获得录音权限");
            if(granted){
                GlobalSP.getSP().put(GlobalSP.KEY_RECORD_PERMISSION, 1);
                invokeRequestRecordPermissionCallback(true);
            }else{
                GlobalSP.getSP().put(GlobalSP.KEY_RECORD_PERMISSION, 2);
                invokeRequestRecordPermissionCallback(false);
            }
        }
    }

    public void invokeRequestRecordPermissionCallback(boolean ret){
        if(requestRecordPermissionCallback!= null){
            requestRecordPermissionCallback.invoke(ret);
            releaseRequestRecordPermissionCallback();
        }
    }

    private void releaseRequestRecordPermissionCallback(){
        if(requestRecordPermissionCallback!= null){
            requestRecordPermissionCallback.release();
            requestRecordPermissionCallback = null;
        }
    }

    @SUDSync
    public void startRecording(){
        SimpleRecorder.startRecording(activity.getApplicationContext());
    }

    @SUDSync
    public String stopRecording(){
        String recordFilePath = SimpleRecorder.stopRecording();
        releaseRequestRecordPermissionCallback();
        return recordFilePath;
    }

    @SUDSync
    public boolean isTV(){
        return SystemUtils.isTV(activity);
    }

    @SUDSync
    public void setKeyDownFun(SUDRTJSCallback onKeyDownCallback){
        this.onKeyDownCallback = onKeyDownCallback;
    }

    public void invokeKeyDownCallback(int keyCode){
        if(this.onKeyDownCallback!= null){
            int creatorKeyCode = androidKeyCodeToCreator(keyCode);
            this.onKeyDownCallback.invoke(creatorKeyCode);
        }
    }

    private int androidKeyCodeToCreator(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP: return 38;
            case KeyEvent.KEYCODE_DPAD_DOWN: return 40;
            case KeyEvent.KEYCODE_DPAD_LEFT: return 37;
            case KeyEvent.KEYCODE_DPAD_RIGHT: return 39;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: return 13;
            default: return 0;
        }
    }

    public void destroy(){
        stopRecording();
        stopVideo();
        if(this.onKeyDownCallback!= null){
            this.onKeyDownCallback.release();
            this.onKeyDownCallback = null;
        }
        activity = null;
    }

    @SUDSync
    public void createVideoPlayer() {
        if (videoPlayer == null) {
            videoPlayer = new VideoPlayer(activity);
        }
    }

    @SUDSync
    public void playVideo(String url, SUDRTJSCallback onVideoPlayCallback) {
        if (videoPlayer != null) {
            videoPlayer.setListener(new VideoPlayerListener() {
                @Override
                public void onPrepared() {
                    int videoWidth = videoPlayer.getVideoWidth();
                    int videoHeight = videoPlayer.getVideoHeight();
                    onVideoPlayCallback.invoke(1, videoWidth, videoHeight);
                }

                @Override
                public void onCompletion() {
                    onVideoPlayCallback.invoke(2);
                    onVideoPlayCallback.release();
                }

                @Override
                public void onError(String message) {
                    onVideoPlayCallback.invoke(-1);
                    onVideoPlayCallback.release();
                }

                @Override
                public void onBuffering(int percent) {

                }
            });

            videoPlayer.play(url);
        }
    }

    @SUDSync
    public void setVideoCropRect(int left, int top, int right, int bottom) {
        if (videoPlayer != null) {
            videoPlayer.setCropRect(left, top, right, bottom);
        }
    }

    @SUDSync
        public void setVideoLayout(int x, int y, int width, int height) {
        if (videoPlayer == null) return;
        TextureView view = videoPlayer.getView();
        ViewGroup rootView = activity.findViewById(android.R.id.content);
        if (view.getParent() == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.leftMargin = x;
            params.topMargin = y;
            rootView.addView(view, params);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.width = width;
            params.height = height;
            params.leftMargin = x;
            params.topMargin = y;
            view.setLayoutParams(params);
        }
    }

    @SUDSync
    public void stopVideo() {
        if (videoPlayer != null) {
            TextureView view = videoPlayer.getView();
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    @SUDSync
    public void showWebView(String url, int x, int y, int width, int height, SUDRTJSCallback onWebviewCallback) {
        if (webView == null) {
            this.onWebviewCallback = onWebviewCallback;
            WebView webView = new WebView(activity);
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.leftMargin = x;
            params.topMargin = y;
            rootView.addView(webView, params);

            // ===== WebView 设置 =====
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);

            // 允许webview播放声音
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.setMediaPlaybackRequiresUserGesture(false);
            }

            // 处理页面派发的消息window.parent.postMessage({ type: 'ANIMATION_FINISHED' }, '*');
            webView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void postMessage(String json) {
                    Log.d("QuickStartGameActivity", "postMessage json="+json);
                    onWebviewCallback.invoke(json);
                    // 处理来自JS的消息
//                    try {
//                        JSONObject data = new JSONObject(json);
//                        String type = data.getString("type");
//                        // 处理不同类型...
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }, "SeduAndroidBridge");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    injectPostMessageInterceptor(view);
                }
            });

            // ===== 加载 URL =====
            webView.loadUrl(url);
            this.webView = webView;
        }
    }

    private void injectPostMessageInterceptor(WebView webView) {
        // 在每个 iframe 里注入你的拦截脚本，这样Android层才能接收到
        String js =
                "(function() {" +
                        "  function interceptPostMessage(win) {" +
                        "    if (!win || win._postMessagePatched) return;" +
                        "    win._postMessagePatched = true;" +
                        "    var original = win.postMessage;" +
                        "    win.postMessage = function(data, origin) {" +
                        "      if (window.SeduAndroidBridge) {" +
                        "        window.SeduAndroidBridge.postMessage(JSON.stringify(data));" +
                        "      }" +
                        "      original.apply(win, arguments);" +
                        "    };" +
                        "  }" +
                        "  interceptPostMessage(window);" +
                        "  Array.from(document.getElementsByTagName('iframe')).forEach(function(iframe) {" +
                        "    try {" +
                        "      interceptPostMessage(iframe.contentWindow);" +
                        "    } catch(e) {}" +
                        "  });" +
                        "})();";

        webView.evaluateJavascript(js, null);
    }

    @SUDSync
    public void destroyWebView() {
//        if (Looper.myLooper() != Looper.getMainLooper()) {
//            activity.runOnUiThread(this::destroyWebView);
//            return;
//        }
        if (webView != null) {
            // 1. 立刻停止加载 & JS 执行
            webView.stopLoading();
            webView.loadUrl("about:blank");

            // 2. 暂停（减少功耗）
            webView.onPause();

            // 3. 移除 JS 接口（防止回调 Activity）
            webView.removeJavascriptInterface("SeduAndroidBridge");

            // 4. 清除视图
            webView.removeAllViews();

            // 5. 从父布局移除
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }

            // 6. 真正销毁
            webView.destroy();
            webView = null;

            onWebviewCallback.release();
            onWebviewCallback = null;
            webView = null;
        }
    }
}
