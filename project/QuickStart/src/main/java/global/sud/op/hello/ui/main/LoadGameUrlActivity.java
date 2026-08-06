package global.sud.op.hello.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import global.sud.op.hello.R;
import global.sud.op.hello.app.AppConfig;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.QuickStartGameActivity;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class LoadGameUrlActivity extends BaseActivity {
    private File sudRootDir;
    private EditText etGameUrl;
    private View btnStart;
    private View btnClearCache;
    private boolean isWorking = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_game_url;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        sudRootDir = new File(getFilesDir(), "sud");
        ensureSudRootDir();
        etGameUrl = findViewById(R.id.et_gameurl);
        btnStart = findViewById(R.id.btn_start);
        btnClearCache = findViewById(R.id.btn_clear_cache);
        etGameUrl.setText(GlobalSP.getSP().getString(GlobalSP.KEY_GAME_URL, AppConfig.TEST_GAME_URL));
        btnStart.requestFocus();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        btnStart.setOnClickListener(v -> startDownloadGame());
        btnClearCache.setOnClickListener(v -> clearCacheDir());
    }

    private void startDownloadGame() {
        if (!checkStartWork()) {
            return;
        }

        String gameUrl = getInputContent();
        if (TextUtils.isEmpty(gameUrl)) {
            ToastUtils.showShort("请输入gameUrl");
            finishWork();
            return;
        }
        GlobalSP.getSP().put(GlobalSP.KEY_GAME_URL, gameUrl);
        String zipFileName = parseZipFileName(gameUrl);
        if (TextUtils.isEmpty(zipFileName)) {
            ToastUtils.showShort("gameUrl格式不正确");
            finishWork();
            return;
        }
        String bagId = parseBagId(zipFileName);
        if (TextUtils.isEmpty(bagId)) {
            ToastUtils.showShort("gameUrl格式不正确");
            finishWork();
            return;
        }

        if (!ensureSudRootDir()) {
            ToastUtils.showShort("缓存目录初始化失败");
            finishWork();
            return;
        }

        File bagDirFile = new File(sudRootDir, bagId);
        if (isValidGameDir(bagDirFile)) {
            startGame(bagDirFile);
            finishWork();
            return;
        }
        deleteRecursively(bagDirFile);

        if (!bagDirFile.exists() && !bagDirFile.mkdirs()) {
            ToastUtils.showShort("创建缓存目录失败");
            finishWork();
            return;
        }

        new Thread(() -> {
            File zipFile = new File(sudRootDir, zipFileName);
            try {
                if (zipFile.exists() && !zipFile.delete()) {
                    throw new IOException("删除旧缓存失败");
                }
                downloadFile(gameUrl, zipFile);
                unzip(zipFile, bagDirFile);
                runOnUiThread(() -> startGame(bagDirFile));
            } catch (Exception e) {
                e.printStackTrace();
                deleteRecursively(zipFile);
                deleteRecursively(bagDirFile);
                runOnUiThread(() -> ToastUtils.showShort("下载失败：" + buildReadableError(e)));
            } finally {
                runOnUiThread(this::finishWork);
            }
        }).start();
    }

    private boolean ensureSudRootDir() {
        return sudRootDir.exists() || sudRootDir.mkdirs();
    }

    private void updateActionButtons() {
        btnStart.setEnabled(!isWorking);
        btnStart.setAlpha(isWorking ? 0.3f : 1.0f);
        btnClearCache.setEnabled(!isWorking);
        btnClearCache.setAlpha(isWorking ? 0.3f : 1.0f);
    }

    private boolean checkStartWork() {
        if (isWorking) return false;
        isWorking = true;
        updateActionButtons();
        return true;
    }

    private void finishWork() {
        isWorking = false;
        updateActionButtons();
    }

    private String parseZipFileName(String gameUrl) {
        int lastSlashIndex = gameUrl.lastIndexOf("/");
        if (lastSlashIndex < 0 || lastSlashIndex >= gameUrl.length() - 1) {
            return null;
        }
        return gameUrl.substring(lastSlashIndex + 1);
    }

    private String parseBagId(String zipFileName) {
        int dotIndex = zipFileName.indexOf(".");
        if (dotIndex <= 0) {
            return null;
        }
        return zipFileName.substring(0, dotIndex);
    }

    private String buildReadableError(Exception e) {
        String msg = e.getMessage();
        if (!TextUtils.isEmpty(msg)) {
            return msg;
        }
        return e.getClass().getSimpleName();
    }

    private boolean isValidGameDir(File gameDir) {
        if (gameDir == null || !gameDir.exists() || !gameDir.isDirectory()) {
            return false;
        }
        File[] children = gameDir.listFiles();
        return children != null && children.length > 0;
    }

    private void clearCacheDir() {
        if (!checkStartWork()) {
            return;
        }
        new Thread(() -> {
            boolean success;
            String error = null;
            try {
                success = deleteRecursively(sudRootDir);
                if (success) {
                    success = ensureSudRootDir();
                }
            } catch (Exception e) {
                success = false;
                error = buildReadableError(e);
            }
            boolean finalSuccess = success;
            String finalError = error;
            runOnUiThread(() -> {
                if (finalSuccess) {
                    ToastUtils.showShort("缓存已清空");
                } else {
                    ToastUtils.showShort("清空缓存失败" + (TextUtils.isEmpty(finalError) ? "" : "：" + finalError));
                }
                finishWork();
            });
        }).start();
    }

    private boolean deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    private void downloadFile(String urlStr, File output) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            conn.disconnect();
            throw new IOException("HTTP " + responseCode);
        }

        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(output)) {

            byte[] buffer = new byte[2048];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } finally {
            conn.disconnect();
        }
    }

    private void unzip(File zipFile, File targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(targetDir, entry.getName());

                if (entry.isDirectory()) {
                    if (!newFile.exists() && !newFile.mkdirs()) {
                        throw new IOException("创建目录失败：" + newFile.getAbsolutePath());
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (parent != null && !parent.exists() && !parent.mkdirs()) {
                        throw new IOException("创建目录失败：" + parent.getAbsolutePath());
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[2048];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    private void startGame(File gameDirFile) {
        GameModel gameModel = new GameModel();
        gameModel.gameName = gameDirFile.getName();
//        gameModel.gameId = gameModel.gameName;
//        gameModel.gameId = "1468180338417074177";
        gameModel.gameId = "0";
        gameModel.gamePkgVersion = "1.0.0";
        gameModel.gameUrl = gameDirFile.getAbsolutePath();
        gameModel.pathType = SUDOPGamePathType.DIR;
        // 测试代码，固定横屏
        gameModel.orientationMode = GameModel.ORIENTATION_LANDSCAPE;
        QuickStartGameActivity.start(this, gameModel);
    }

    private String getInputContent() {
        Editable text = etGameUrl.getText();
        if (text == null) {
            return null;
        }
        return text.toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
