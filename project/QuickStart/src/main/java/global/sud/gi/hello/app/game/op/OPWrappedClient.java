package global.sud.gi.hello.app.game.op;

import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.AppUtils;

import org.json.JSONObject;

import global.sud.gi.SUDGIWrapper.custom.image.ImageManager;
import global.sud.gi.SUDGIWrapper.custom.ui.GameUiManager;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.PreviewImageView;
import global.sud.gi.SUDGIWrapper.custom.video.VideoContainer;
import global.sud.gi.hello.app.game.op.data.OPSession;
import global.sud.gi.hello.app.game.op.data.OPSessionStore;
import global.sud.op.runtime.client.SUDOPCommonStateHandle;
import global.sud.op.runtime.client.model.MenuButtonBoundingClientRect;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPSetKeepScreenOnParams;
import global.sud.op.runtime.core.model.SUDOPSetScreenBrightnessParams;
import global.sud.op.runtime.core.model.SUDOPShowActionSheetParams;
import global.sud.op.runtime.core.model.SUDOPShowLoadingParams;
import global.sud.op.runtime.core.model.SUDOPShowModalParams;
import global.sud.op.runtime.core.model.SUDOPShowToastParams;
import global.sud.op.runtime.core.video.SUDOPVideo;
import global.sud.op.runtime.core.wrapped.SUDOPChooseImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPOnGetUserProfileParams;
import global.sud.op.runtime.core.wrapped.SUDOPPreviewImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPRequestPaymentParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageTempParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageToPhotosAlbumParams;

public class OPWrappedClient implements SUDOPWrappedClient {

    private final FragmentActivity activity;
    private final FrameLayout gameContainer;
    private final ConstraintLayout widgetContainer;
    private final View menuButton;
    private final OPSessionStore sessionStore;
    private final GameUiManager gameUiManager;
    private final ImageManager imageManager;

    public OPWrappedClient(FragmentActivity activity,
                                 FrameLayout gameContainer,
                                 ConstraintLayout widgetContainer,
                                 View menuButton,
                                 OPSessionStore sessionStore) {
        this.activity = activity;
        this.gameContainer = gameContainer;
        this.widgetContainer = widgetContainer;
        this.menuButton = menuButton;
        this.sessionStore = sessionStore;
        gameUiManager = new GameUiManager(activity, widgetContainer);
        imageManager = new ImageManager(activity);
    }

    @Override
    public void onGetLegacyUserIdentity(SUDOPStateHandle handle) {
        OPSession session = sessionStore.readSession();
        JSONObject data = new JSONObject();
        try {
            data.put("legacy_user_identity", session == null ? "" : String.valueOf(session.uid));
            handle.success(data.toString());
        } catch (Exception e) {
            handle.failure(-1, "Unable to build user identity");
        }
    }

    @Override
    public void onGetUserInfo(SUDOPStateHandle handle) {
        OPSession session = sessionStore.readSession();
        if (session == null) {
            handle.failure(-1, "User session is unavailable");
            return;
        }
        JSONObject data = new JSONObject();
        try {
            data.put("nickname", session.nickname);
            data.put("avatar", session.avatarUrl);
            handle.success(data.toString());
        } catch (Exception e) {
            handle.failure(-1, "Unable to build user information");
        }
    }

    @Override
    public void onGetUserProfile(SUDOPStateHandle handle, SUDOPOnGetUserProfileParams params) {
        handle.failure(-1, "User profile decryption is not supported");
    }

    @Override
    public void requestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        handle.failure(-1, "Payment is not supported");
    }

    @Override
    public void saveImageTemp(SUDOPStateHandle handle, SUDOPSaveImageTempParams params) {
        imageManager.saveImageTemp(handle, params);
    }

    @Override
    public String saveImageTempSync(SUDOPSaveImageTempParams params) {
        return imageManager.saveImageTempSync(params);
    }

    @Override
    public void saveImageToPhotosAlbum(SUDOPStateHandle handle,
                                       SUDOPSaveImageToPhotosAlbumParams params) {
        imageManager.saveImageToPhotosAlbum(handle, params);
    }

    @Override
    public void chooseImage(SUDOPStateHandle handle, SUDOPChooseImageParams params) {
        imageManager.chooseImage(handle, params);
    }

    @Override
    public void previewImage(SUDOPStateHandle handle, SUDOPPreviewImageParams params) {
        handle.success(null);
        if (params.urls == null || params.urls.length == 0) {
            return;
        }
        PreviewImageView preview = new PreviewImageView(activity);
        widgetContainer.addView(preview,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        preview.setImages(params.urls, params.current);
        preview.setOnImageClickListener((position, url) -> widgetContainer.removeView(preview));
    }

    @Override
    public void createVideo(SUDOPVideo params) {
        new VideoContainer(activity, gameContainer).initVideo(params);
    }

    @Override
    public void showLoading(SUDOPStateHandle handle, SUDOPShowLoadingParams params) {
        gameUiManager.showLoading(handle, params);
    }

    @Override
    public void hideLoading(SUDOPStateHandle handle) {
        gameUiManager.hideLoading(handle);
    }

    @Override
    public void showToast(SUDOPCommonStateHandle handle, SUDOPShowToastParams params) {
        gameUiManager.showToast(handle, params);
    }

    @Override
    public void hideToast(SUDOPCommonStateHandle handle) {
        gameUiManager.hideToast(handle);
    }

    @Override
    public void showActionSheet(SUDOPCommonStateHandle handle, SUDOPShowActionSheetParams params) {
        gameUiManager.showActionSheet(handle, params);
    }

    @Override
    public void showModal(SUDOPCommonStateHandle handle, SUDOPShowModalParams params) {
        gameUiManager.showModal(handle, params);
    }

    @Override
    public void setScreenBrightness(SUDOPCommonStateHandle handle,
                                    SUDOPSetScreenBrightnessParams params) {
        gameUiManager.setScreenBrightness(handle, params);
    }

    @Override
    public void getScreenBrightness(SUDOPCommonStateHandle handle) {
        gameUiManager.getScreenBrightness(handle);
    }

    @Override
    public void setKeepScreenOn(SUDOPCommonStateHandle handle,
                                SUDOPSetKeepScreenOnParams params) {
        gameUiManager.setKeepScreenOn(handle, params);
    }

    @Override
    public MenuButtonBoundingClientRect getMenuButtonBoundingClientRect() {
        MenuButtonBoundingClientRect rect = new MenuButtonBoundingClientRect();
        rect.width = menuButton.getMeasuredWidth();
        rect.height = menuButton.getMeasuredHeight();
        rect.top = menuButton.getTop();
        rect.right = menuButton.getRight();
        rect.bottom = menuButton.getBottom();
        rect.left = menuButton.getLeft();
        return rect;
    }

    @Override
    public void getAppBaseInfo(JSONObject jsonObject) {
        try {
            jsonObject.put("version", AppUtils.getAppVersionName());
        } catch (Exception ignored) {
        }
    }
}
