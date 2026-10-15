package global.sud.gi.SUDGIWrapper.custom.image;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PermissionUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import global.sud.gi.SUDGIWrapper.custom.base.utils.ImageCompressUtils;
import global.sud.gi.SUDGIWrapper.custom.base.utils.ImageUtils;
import global.sud.gi.SUDGIWrapper.custom.base.utils.UriCopyUtils;
import global.sud.gi.SUDGIWrapper.custom.ui.model.ChooseImageResult;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog.ChooseImageDialog;
import global.sud.gi.SudGIPWrapper.R;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.wrapped.SUDOPChooseImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageTempParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageToPhotosAlbumParams;

public class ImageManager {

    private static final String TAG = "ImageManager";

    private FragmentActivity activity;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<String> pickImageLauncher;
    private boolean chooseImageIsOriginal;
    private SUDOPStateHandle chooseImageStateHandle;
    // 拍照
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri imageUri;

    public ImageManager(FragmentActivity activity) {
        this.activity = activity;

        pickImageLauncher = activity.registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            processChooseImageUri(uri);
        });

        takePictureLauncher = activity.registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (chooseImageStateHandle == null) {
                return;
            }
            if (success && imageUri != null) {
                processChooseImageUri(imageUri);
                imageUri = null;
            } else {
                chooseImageStateHandle.failure(-1, activity.getString(R.string.sud_op_wck_error_user_cancelled));
                chooseImageStateHandle = null;
            }
        });
    }

    public void saveImageTemp(SUDOPStateHandle handle, SUDOPSaveImageTempParams params) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String filePath = getTempFile(params.fileType);
                try {
                    if ("jpg".equals(params.fileType)) {
                        ImageUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
                    } else if ("png".equals(params.fileType)) {
                        ImageUtils.rgbaToPng(params.data, params.width, params.height, filePath);
                    } else {
                        handle.failure(-1, activity.getString(R.string.sud_op_wck_error_unsupported_file_type, params.fileType));
                        return;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "save_image_temp_generation_failure", e);
                    handle.failure(-1, activity.getString(R.string.sud_op_wck_error_generate_image_data_failed));
                    return;
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempFilePath", filePath);
                } catch (Exception e) {
                    Log.e(TAG, "save_image_temp_result_serialization_failure", e);
                }
                String result = obj.toString();
                handle.success(result);
            }
        }.start();
    }

    public String saveImageTempSync(SUDOPSaveImageTempParams params) {
        String filePath = getTempFile(params.fileType);
        try {
            if ("jpg".equals(params.fileType)) {
                ImageUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
            } else if ("png".equals(params.fileType)) {
                ImageUtils.rgbaToPng(params.data, params.width, params.height, filePath);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return filePath;
    }

    public void saveImageToPhotosAlbum(SUDOPStateHandle handle, SUDOPSaveImageToPhotosAlbumParams params) {
        File file;
        if (params.filePath == null || !(file = new File(params.filePath)).exists()) {
            handle.failure(-1, activity.getString(R.string.sud_op_wck_error_file_path_not_found));
            return;
        }
        boolean result = false;
        try {
            result = saveImageToGallery(activity, file);
        } catch (Exception e) {
            Log.e(TAG, "save_image_to_photos_failure", e);
            handle.failure(-1, activity.getString(R.string.sud_op_wck_error_save_photo_failed));
            return;
        }
        if (result) {
            handle.success(null);
        } else {
            handle.failure(-1, activity.getString(R.string.sud_op_wck_error_save_photo_failed));
        }
    }

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
            handle.failure(-1, activity.getString(R.string.sud_op_wck_error_invalid_source_types));
        }
    }

    @NonNull
    private String getTempFile(String suffixName) {
        return new File(getTempDir(), UUID.randomUUID() + "." + suffixName).getAbsolutePath();
    }

    @NonNull
    private File getTempDir() {
        File dir = activity.getExternalCacheDir();
        if (dir == null) {
            dir = activity.getCacheDir();
        }
        File hellosud = new File(dir, "hellosud");
        if (!hellosud.exists()) {
            hellosud.mkdir();
        }
        return hellosud;
    }

    public boolean saveImageToGallery(Context context, File file) throws Exception {
        // Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES);
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
                return false;
            }
            try (OutputStream out = resolver.openOutputStream(uri);
                 FileInputStream in = new FileInputStream(file)) {
                if (out == null) {
                    return false;
                }
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                return true;
            }
        }

        // Android 9及以下
        File picturesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );
        if (!picturesDir.exists()) {
            picturesDir.mkdirs();
        }
        File destFile = new File(picturesDir, file.getName());
        try (FileInputStream in = new FileInputStream(file);
             FileOutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
        // 通知图库刷新
        MediaScannerConnection.scanFile(context, new String[]{destFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
        return true;
    }

    private void showChooseDialog(int count, boolean isOriginal, SUDOPStateHandle handle) {
        ChooseImageDialog dialog = new ChooseImageDialog(activity);
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
                handle.failure(-1, activity.getString(R.string.sud_op_wck_error_user_cancelled));
            }
        });
        dialog.show();
    }

    private void selectCamera(int count, boolean isOriginal, SUDOPStateHandle handle) {
        boolean granted = PermissionUtils.isGranted(Manifest.permission.CAMERA);
        if (!granted) {
            PermissionUtils permissionUtils = PermissionUtils.permission(Manifest.permission.CAMERA);
            permissionUtils.callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    activity.runOnUiThread(() -> {
                        selectCamera(count, isOriginal, handle);
                    });
                }

                @Override
                public void onDenied() {
                    handle.failure(-1, activity.getString(R.string.sud_op_wck_error_camera_permission_denied));
                }
            });
            permissionUtils.request();
            return;
        }
        String tempFilePath = getTempFile("jpg");
        File imageFile = new File(tempFilePath);
        imageUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileprovider",
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

    private void processChooseImageUri(Uri uri) {
        if (chooseImageStateHandle == null) {
            return;
        }
        if (uri == null) {
            chooseImageStateHandle.failure(-1, activity.getString(R.string.sud_op_wck_error_user_cancelled));
            chooseImageStateHandle = null;
            return;
        }
        // 这里拿到图片 URI
        executorService.execute(() -> {
            try {
                File outDir = getTempDir();
                File result;
                if (chooseImageIsOriginal) {
                    result = UriCopyUtils.copyUriToFile(activity, uri, outDir);
                } else { // 压缩
                    result = ImageCompressUtils.compressUriToFile(activity, uri, outDir,
                            1080,   // 目标最长边
                            80      // JPEG质量(0~100)
                    );
                }
                ChooseImageResult chooseImageResult = new ChooseImageResult();
                chooseImageResult.addFile(result);
                chooseImageStateHandle.success(GsonUtils.toJson(chooseImageResult));
            } catch (Exception e) {
                Log.e(TAG, "choose_image_processing_failure", e);
                chooseImageStateHandle.failure(-1, activity.getString(R.string.sud_op_wck_error_compress_image_failed));
            }
            chooseImageStateHandle = null;
        });
    }

}
