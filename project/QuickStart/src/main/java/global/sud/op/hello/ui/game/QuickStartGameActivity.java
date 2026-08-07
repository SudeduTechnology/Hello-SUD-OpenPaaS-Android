package global.sud.op.hello.ui.game;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.GsonUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.http.param.BaseResponse;
import global.sud.op.hello.common.http.param.RetCode;
import global.sud.op.hello.common.http.rx.RxCallback;
import global.sud.op.hello.common.utils.DensityUtils;
import global.sud.op.hello.common.utils.SystemUtils;
import global.sud.op.hello.common.utils.ViewUtils;
import global.sud.op.hello.service.MainRepository;
import global.sud.op.hello.service.resp.GetUserProfileResp;
import global.sud.op.hello.service.resp.MockPaymentResp;
import global.sud.op.hello.service.resp.ValidatePaymentResp;
import global.sud.op.hello.ui.game.model.ChooseImageResult;
import global.sud.op.hello.ui.game.utils.ImageCompressUtil;
import global.sud.op.hello.ui.game.utils.QgClientUtils;
import global.sud.op.hello.ui.game.utils.UriCopyUtil;
import global.sud.op.hello.ui.game.widget.ChooseImageDialog;
import global.sud.op.hello.ui.game.widget.GameRoomMoreDialog;
import global.sud.op.hello.ui.game.widget.GameRoomTopView;
import global.sud.op.hello.ui.game.widget.PaymentDialog;
import global.sud.op.hello.ui.main.GameModel;
import global.sud.op.runtime.client.SUDOPCommonStateHandle;
import global.sud.op.runtime.client.model.MenuButtonBoundingClientRect;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.ad.SUDOPBannerAd;
import global.sud.op.runtime.core.ad.SUDOPCustomAd;
import global.sud.op.runtime.core.ad.SUDOPGameBannerAd;
import global.sud.op.runtime.core.ad.SUDOPGameDrawerAd;
import global.sud.op.runtime.core.ad.SUDOPGamePortalAd;
import global.sud.op.runtime.core.ad.SUDOPInterstitialAd;
import global.sud.op.runtime.core.ad.SUDOPRewardedAd;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPGamePathType;
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

/**
 * 游戏页面
 * Game page
 */
public class QuickStartGameActivity extends BaseActivity {
    private static final String TAG = "QuickStartGameActivity";
    private String gameId;
    private String gameUrl;
    private String gamePkgVersion;
    private SUDOPGamePathType pathType;
    private String manifestJson;
    private GameRoomTopView topView;
    private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel();
    private TextView tvProgress;
    private LifecycleOwner lifecycleOwner = this;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean chooseImageIsOriginal;
    private SUDOPStateHandle chooseImageStateHandle;

    // 拍照
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri imageUri;

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model) {
        Log.d(TAG, "start");
        Intent intent = new Intent(context, QuickStartGameActivity.class);
        intent.putExtra("GameModel", model);
        context.startActivity(intent);
    }

    @Override
    protected int getPreferredOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    protected boolean useTvFormHeightAdapt() {
        return false;
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        GameModel model = (GameModel) getIntent().getSerializableExtra("GameModel");
        if (model == null) {
            return true;
        }
        gameId = model.gameId;
        gameUrl = model.gameUrl;
        gamePkgVersion = model.gamePkgVersion;
        pathType = model.pathType;
        manifestJson = model.manifestJson;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget");
        super.initWidget();
        topView = findViewById(R.id.room_top_view);
        tvProgress = findViewById(R.id.tv_progress);

        ViewUtils.addMarginTop(topView, ImmersionBar.getStatusBarHeight(this));

        // 隐藏无关UI
//        topView.setName(getString(R.string.room_name));
        topView.setSelectGameVisible(false);
        View roomBottomView = findViewById(R.id.room_bottom_view);
        roomBottomView.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        gameViewModel.sudOPWrappedClient = sudOPWrappedClient;
        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        // Call this method to load the corresponding game. Developers can decide when to load the game based on their business logic.
        gameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion, pathType, manifestJson);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        FrameLayout gameContainer = findViewById(R.id.game_container); // 获取游戏View容器 English: Retrieve the game view container.
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除 English: When closing the game, remove the game view.
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内 English: Add the game view to the container.
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        gameViewModel.progressLiveData.observe(this, new Observer<BaseGameViewModel.ProgressModel>() {
            @Override
            public void onChanged(BaseGameViewModel.ProgressModel model) {
                updateProgress(model);
            }
        });

        // 更多按钮的点击监听
        // Click listener for the 'More' button.
        QuickStartGameActivity activity = this;
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SystemUtils.isTV(activity)){
                    showMoreDialog();
                }
            }
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            processChooseImageUri(uri);
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (chooseImageStateHandle == null) {
                return;
            }
            if (success && imageUri != null) {
                processChooseImageUri(imageUri);
                imageUri = null;
            } else {
                chooseImageStateHandle.failure(RetCode.Fail, "user cancel");
                chooseImageStateHandle = null;
            }
        });
    }

    private void processChooseImageUri(Uri uri) {
        if (chooseImageStateHandle == null) {
            return;
        }
        if (uri == null) {
            chooseImageStateHandle.failure(RetCode.Fail, "user cancel");
            chooseImageStateHandle = null;
            return;
        }
        // 这里拿到图片 URI
        executorService.execute(() -> {
            try {
                File outDir = getTempDir();
                File result;
                if (chooseImageIsOriginal) {
                    result = UriCopyUtil.copyUriToFile(this, uri, outDir);
                } else { // 压缩
                    result = ImageCompressUtil.compressUriToFile(this, uri, outDir,
                            1080,   // 目标最长边
                            80      // JPEG质量(0~100)
                    );
                }
                ChooseImageResult chooseImageResult = new ChooseImageResult();
                chooseImageResult.addFile(result);
                chooseImageStateHandle.success(GsonUtils.toJson(chooseImageResult));
            } catch (Exception e) {
                e.printStackTrace();
                chooseImageStateHandle.failure(RetCode.Fail, "compress fail:" + e);
            }
            chooseImageStateHandle = null;
        });
    }

    private void updateProgress(BaseGameViewModel.ProgressModel model) {
        if (model == null) {
            return;
        }
        if (model.stage == 3) {
            tvProgress.setVisibility(View.GONE);
        } else {
            tvProgress.setVisibility(View.VISIBLE);
            if (model.retCode == RetCode.SUCCESS) {
                tvProgress.setText(String.format(Locale.US, "加载百分比为:%d%%", model.progress));
            } else {
                tvProgress.setText(String.format(Locale.US, "加载失败，code：%d", model.retCode));
            }
        }
    }

    private void showMoreDialog() {
        GameRoomMoreDialog dialog = new GameRoomMoreDialog();
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                gameViewModel.destroyGame();
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void updateStatusBar() {
        // 这个沉浸式状态栏的使用是APP的业务，对于游戏而言不是必须的
        // The use of the immersive status bar is part of the app's functionality and is not essential for games.
        if (!TextUtils.isEmpty(gameId)) { // 玩着游戏 English: Playing the game.
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
        // 注意：要在此处调用onResume()方法
        // Note: Call the onResume() method here.
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注意：要在此处调用onPause()方法
        // Note: Call the onPause() method here.
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    @Override
    public void onBackPressed() {
        // 注意：需要保证页面销毁之前，先调用游戏的销毁方法
        // 如果有其他地方调用finish()，那么也要在finish()之前，先调用游戏的销毁方法

        // Note: Ensure that the game's destruction method is called before the page is destroyed.
        // If finish() is called elsewhere, make sure to call the game's destruction method before finish().

        gameViewModel.destroyGame();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        gameViewModel.destroyGame();
    }

    private SUDOPWrappedClient sudOPWrappedClient = new SUDOPWrappedClient() {
        @Override
        public void onGetLegacyUserIdentity(SUDOPStateHandle handle) {
            String userId = gameViewModel.getUserId();
            JSONObject obj = new JSONObject();
            try {
                obj.put("legacy_user_identity", userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handle.success(obj.toString());
        }

        @Override
        public void onGetUserInfo(SUDOPStateHandle handle) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("nickname", gameViewModel.getNickName());
                obj.put("avatar", gameViewModel.getAvatar());
            } catch (Exception e) {
                e.printStackTrace();
            }
            handle.success(obj.toString());
        }

        @Override
        public void onGetUserProfile(SUDOPStateHandle handle, SUDOPOnGetUserProfileParams params) {
            String encrypted_data = params.encryptedData;
            MainRepository.getUserProfile(lifecycleOwner, gameViewModel.getAppId(), gameViewModel.getUserId(), encrypted_data, new RxCallback<GetUserProfileResp>() {
                @Override
                public void onNext(BaseResponse<GetUserProfileResp> resp) {
                    super.onNext(resp);
                    if (resp.getRet_code() == 0) {
                        GetUserProfileResp data = resp.getData();
                        if (data == null || TextUtils.isEmpty(data.user_profile_data)) {
                            handle.failure(-1, "The server returned an empty user profile.");
                        } else {
                            handle.success(data.user_profile_data);
                        }
                    } else {
                        handle.failure(resp.getRet_code(), resp.getRet_msg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    handle.failure(-1, "error:" + e);
                }
            });
        }

        @Override
        public void requestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
            onRequestPayment(handle, params);
        }

        @Override
        public void saveImageTemp(SUDOPStateHandle handle, SUDOPSaveImageTempParams params) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String filePath = getTempFile(params.fileType);
                    try {
                        if ("jpg".equals(params.fileType)) {
                            QgClientUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
                        } else if ("png".equals(params.fileType)) {
                            QgClientUtils.rgbaToPng(params.data, params.width, params.height, filePath);
                        } else {
                            handle.failure(-1, "This file type is not supported：" + params.fileType);
                            return;
                        }
                    } catch (IOException e) {
                        handle.failure(-1, "error:" + e);
                        return;
                    }
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("tempFilePath", filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String result = obj.toString();
                    handle.success(result);
                }
            }.start();
        }

        @Override
        public String saveImageTempSync(SUDOPSaveImageTempParams params) {
            String filePath = getTempFile(params.fileType);
            try {
                if ("jpg".equals(params.fileType)) {
                    QgClientUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
                } else if ("png".equals(params.fileType)) {
                    QgClientUtils.rgbaToPng(params.data, params.width, params.height, filePath);
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
            return filePath;
        }

        @Override
        public void saveImageToPhotosAlbum(SUDOPStateHandle handle, SUDOPSaveImageToPhotosAlbumParams params) {
            File file;
            if (params.filePath == null || !(file = new File(params.filePath)).exists()) {
                handle.failure(-1, "The filePath does not exist.");
                return;
            }
            boolean result = false;
            try {
                result = saveImageToGallery(context, file);
            } catch (IOException e) {
                handle.failure(-1, "saveImageToPhotsAlbum error:" + e);
                return;
            }
            if (result) {
                handle.success(null);
            } else {
                handle.failure(-1, "saveImageToPhotsAlbum fail");
            }
        }

        @Override
        public void chooseImage(SUDOPStateHandle handle, SUDOPChooseImageParams params) {
            int count = params.count;
            String[] sizeType = params.sizeType;
            String[] sourceType = params.sourceType;
            boolean isOriginal = true;
            if (sizeType != null) {
                for (String type : sizeType) {
                    if ("compressed".equals(type)) {
                        isOriginal = false;
                    }
                }
            }
            boolean isAlbum = false;
            boolean isCamera = false;
            if (sourceType != null) {
                for (String type : sourceType) {
                    if ("album".equals(type)) {
                        isAlbum = true;
                    } else if ("camera".equals(type)) {
                        isCamera = true;
                    }
                }
            }
            if (isAlbum && isCamera) {
                showChooseDialog(count, isOriginal, handle);
            } else if (isCamera) {
                selectCamera(count, isOriginal, handle);
            } else if (isAlbum) {
                selectAlbum(count, isOriginal, handle);
            } else {
                handle.failure(-1, "sourceType fail to identify");
            }
        }

        @Override
        public void previewImage(SUDOPStateHandle var1, SUDOPPreviewImageParams var2){

        }

        @Override
        public void createVideo(SUDOPVideo var1){}

        @Override
        public void createBannerAd(SUDOPBannerAd var1){}

        @Override
        public void createCustomAd(SUDOPCustomAd var1){}

        @Override
        public void createInterstitialAd(SUDOPInterstitialAd var1){}

        @Override
        public void createGameBannerAd(SUDOPGameBannerAd var1){}

        @Override
        public void createGamePortalAd(SUDOPGamePortalAd var1){}

        @Override
        public void createGameDrawerAd(SUDOPGameDrawerAd var1){}

        @Override
        public void createRewardedVideoAd(SUDOPRewardedAd var1){}

        @Override
        public void showLoading(SUDOPStateHandle var1, SUDOPShowLoadingParams var2){}

        @Override
        public void hideLoading(SUDOPStateHandle var1){}

        @Override
        public void showToast(SUDOPCommonStateHandle var1, SUDOPShowToastParams var2){}

        @Override
        public void hideToast(SUDOPCommonStateHandle var1){}

        @Override
        public void showActionSheet(SUDOPCommonStateHandle var1, SUDOPShowActionSheetParams var2){}

        @Override
        public void showModal(SUDOPCommonStateHandle var1, SUDOPShowModalParams var2){}

        @Override
        public MenuButtonBoundingClientRect getMenuButtonBoundingClientRect(){
            return null;
        }

        @Override
        public void setScreenBrightness(SUDOPCommonStateHandle var1, SUDOPSetScreenBrightnessParams var2){}

        @Override
        public void getScreenBrightness(SUDOPCommonStateHandle var1){}

        @Override
        public void setKeepScreenOn(SUDOPCommonStateHandle var1, SUDOPSetKeepScreenOnParams var2){}

        @Override
        public void getAppBaseInfo(JSONObject var1){}


    };

    @NonNull
    private String getTempFile(String suffixName) {
        return new File(getTempDir(), UUID.randomUUID() + "." + suffixName).getAbsolutePath();
    }

    @NonNull
    private File getTempDir() {
        File dir = getExternalCacheDir();
        if (dir == null) {
            dir = getCacheDir();
        }
        File hellosud = new File(dir, "hellosud");
        if (!hellosud.exists()) {
            hellosud.mkdir();
        }
        return hellosud;
    }

    public static boolean saveImageToGallery(Context context, File file) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri == null) return false;

        try (OutputStream out = resolver.openOutputStream(uri);
             FileInputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void onRequestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        MainRepository.validatePayment(lifecycleOwner, gameViewModel.getAppId(), gameViewModel.getUserId(), params.signData, params.signature, new RxCallback<ValidatePaymentResp>() {
            @Override
            public void onNext(BaseResponse<ValidatePaymentResp> t) {
                super.onNext(t);
                if (t.getRet_code() == 0) {
                    if (t.getData() != null && t.getData().is_valid) {
                        showPaymentDialog(handle, params);
                    } else {
                        handle.failure(-1, "validate payment fail");
                    }
                } else {
                    handle.failure(t.getRet_code(), t.getRet_msg());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                handle.failure(-1, "validate payment net fail:" + e);
            }
        });

    }

    private void showPaymentDialog(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        PaymentDialog dialog = new PaymentDialog(params);
        dialog.setPaymentListener(new PaymentDialog.PaymentListener() {
            @Override
            public void paymentCompleted(boolean isSuccess) {
                String action = isSuccess ? "SUCCESS" : "FAILED";
                MainRepository.mockPayment(lifecycleOwner, gameViewModel.getAppId(), gameViewModel.getUserId(), params.sudTradeNo, action, new RxCallback<MockPaymentResp>() {
                    @Override
                    public void onNext(BaseResponse<MockPaymentResp> t) {
                        super.onNext(t);
                        if (t.getRet_code() == 0) {
                            if (t.getData() != null && t.getData().status) { // 这个状态只是模拟支付本身是否成功
                                if (isSuccess) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("sudTradeNo", params.sudTradeNo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    handle.success(jsonObject.toString());
                                } else {
                                    handle.failure(-1, "payment fail");
                                }
                            } else {
                                handle.failure(-1, "mock payment fail");
                            }
                        } else {
                            handle.failure(t.getRet_code(), t.getRet_msg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        handle.failure(-1, "mock payment net fail:" + e);
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showChooseDialog(int count, boolean isOriginal, SUDOPStateHandle handle) {
        ChooseImageDialog dialog = new ChooseImageDialog(this);
        dialog.setChooseImageListener(new ChooseImageDialog.ChooseImageListener() {
            @Override
            public void onClickCamera() {
                selectCamera(count, isOriginal, handle);
            }

            @Override
            public void onClickAlbum() {
                selectAlbum(count, isOriginal, handle);
            }

            @Override
            public void onClickCancel() {
                handle.failure(RetCode.Fail, "user cancel chooseImage");
            }
        });
        dialog.show();
    }

    private void selectCamera(int count, boolean isOriginal, SUDOPStateHandle handle) {
        String tempFilePath = getTempFile("jpg");
        File imageFile = new File(tempFilePath);
        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                imageFile
        );
        this.chooseImageIsOriginal = isOriginal;
        this.chooseImageStateHandle = handle;
        takePictureLauncher.launch(imageUri);
    }

    private void selectAlbum(int count, boolean isOriginal, SUDOPStateHandle handle) {
        this.chooseImageIsOriginal = isOriginal;
        this.chooseImageStateHandle = handle;
        pickImageLauncher.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gameViewModel.onRequestRecordPermissionsResult(requestCode, permissions, grantResults);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            gameViewModel.onKeyDown(keyCode);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (SystemUtils.isTV(this)
            && event.getAction() == KeyEvent.ACTION_DOWN
            ) {
            int keyCode = event.getKeyCode();
            gameViewModel.onKeyDown(keyCode);
            if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || keyCode == KeyEvent.KEYCODE_ENTER
                    || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER){
                View focused = getCurrentFocus();
                if (focused != null && focused.getId() == R.id.top_iv_more) {
                    // 焦点在“更多”按钮时，吞掉确定键，不往下分发
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
