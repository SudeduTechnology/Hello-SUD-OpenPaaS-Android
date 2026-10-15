package global.sud.gi.SUDGIWrapper.custom.ui;

import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONObject;

import global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog.ActionSheetDialog;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog.ModalDialog;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.GameToastView;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.LoadingHintView;
import global.sud.gi.SudGIPWrapper.R;
import global.sud.op.runtime.client.SUDOPCommonStateHandle;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.model.SUDOPSetKeepScreenOnParams;
import global.sud.op.runtime.core.model.SUDOPSetScreenBrightnessParams;
import global.sud.op.runtime.core.model.SUDOPShowActionSheetParams;
import global.sud.op.runtime.core.model.SUDOPShowLoadingParams;
import global.sud.op.runtime.core.model.SUDOPShowModalParams;
import global.sud.op.runtime.core.model.SUDOPShowToastParams;

public class GameUiManager {

    private FragmentActivity activity;
    private ConstraintLayout container;
    private LoadingHintView loadingHintView;
    private GameToastView gameToastView;

    public GameUiManager(FragmentActivity activity, ConstraintLayout container) {
        this.activity = activity;
        this.container = container;
    }

    public void showLoading(SUDOPStateHandle handle, SUDOPShowLoadingParams params) {
        String title = resolveLoadingTitle(params.title, activity.getString(R.string.sud_op_wck_loading_default));
        if (loadingHintView == null) {
            loadingHintView = new LoadingHintView(activity);
            loadingHintView.title = title;
            loadingHintView.mask = params.mask;
            container.addView(loadingHintView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        } else {
            loadingHintView.title = title;
            loadingHintView.mask = params.mask;
            loadingHintView.setVisibility(LoadingHintView.VISIBLE);
            loadingHintView.invalidate();
        }
        handle.success(null);
    }

    public void hideLoading(SUDOPStateHandle handle) {
        if (loadingHintView != null) {
            container.removeView(loadingHintView);
            loadingHintView = null;
        }
        handle.success(null);
    }

    public void showToast(SUDOPCommonStateHandle handle, SUDOPShowToastParams params) {
        if (gameToastView == null) {
            gameToastView = new GameToastView(activity);
        }
        String title = resolveToastTitle(params.title, params.icon, activity.getString(R.string.sud_op_wck_toast_success_default));
        gameToastView.setToast(title, params.icon, params.image, params.duration, params.mask);
        if (gameToastView.getParent() == null) {
            container.addView(gameToastView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        }
        gameToastView.show();
        handle.success(null);
    }

    public void hideToast(SUDOPCommonStateHandle handle) {
        if (gameToastView != null) {
            container.removeView(gameToastView);
            gameToastView = null;
        }
        handle.success(null);
    }

    public void showActionSheet(SUDOPCommonStateHandle handle, SUDOPShowActionSheetParams params) {
        ActionSheetDialog dialog = new ActionSheetDialog(activity, params);
        dialog.setOnActionSheetClickListener(new ActionSheetDialog.OnActionSheetClickListener() {
            @Override
            public void onClickItem(int index) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("tapIndex", index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handle.success(jsonObject.toString());
            }

            @Override
            public void cancel() {
                handle.failure(-1, activity.getString(R.string.sud_op_wck_error_user_cancelled));
            }
        });
        dialog.show();
    }

    public void showModal(SUDOPCommonStateHandle handle, SUDOPShowModalParams params) {
        ModalDialog dialog = new ModalDialog(activity, params);
        dialog.setOnClickListener(new ModalDialog.OnClickListener() {
            @Override
            public void onClick(String content, boolean confirm, boolean cancel) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", content);
                    jsonObject.put("confirm", confirm);
                    jsonObject.put("cancel", cancel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handle.success(jsonObject.toString());
            }
        });
        dialog.show();
    }

    public void setScreenBrightness(SUDOPCommonStateHandle handle, SUDOPSetScreenBrightnessParams params) {
        if (params.value == -1 || (params.value >= 0 && params.value <= 1)) { // 正常的取值范围
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = params.value;
            window.setAttributes(layoutParams);
            handle.success(null);
        } else {
            handle.failure(-1, activity.getString(R.string.sud_op_wck_error_brightness_out_of_range));
        }
    }

    static String resolveLoadingTitle(String title, String defaultTitle) {
        return isEmpty(title) ? defaultTitle : title;
    }

    static String resolveToastTitle(String title, String icon, String successDefaultTitle) {
        if (isEmpty(title) && GameToastView.ICON_SUCCESS.equals(icon)) {
            return successDefaultTitle;
        }
        return title;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public void getScreenBrightness(SUDOPCommonStateHandle handle) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        double value = Math.round(params.screenBrightness * 100.0) / 100.0;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("value", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handle.success(jsonObject.toString());
    }

    public void setKeepScreenOn(SUDOPCommonStateHandle handle, SUDOPSetKeepScreenOnParams params) {
        if (params.keepScreenOn) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        handle.success(null);
    }

}
