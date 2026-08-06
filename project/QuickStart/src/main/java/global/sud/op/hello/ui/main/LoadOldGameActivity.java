package global.sud.op.hello.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.codekidlabs.storagechooser.StorageChooser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseActivity;
import global.sud.op.hello.common.utils.GlobalSP;
import global.sud.op.hello.ui.game.QuickStartGameActivity;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class LoadOldGameActivity extends BaseActivity {

    private TextView tvInfo;
    private EditText etPath;
    private static final int _REQUEST_CODE_PICK_JSON = 20001;
    private String manifestJson;
    private TextView tvManifestInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_load_old_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvInfo = findViewById(R.id.tv_info);
        etPath = findViewById(R.id.et_path);
        tvManifestInfo = findViewById(R.id.tv_manifest_info);
        etPath.setText(GlobalSP.getSP().getString(GlobalSP.KEY_GAME_DIR_PATH));
        initPermission();
//        SUDGI.getCfg().getAdvancedConfigMap().put(ISUDCfg.OP_CHANNEL_OPPO, "true");
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                requestPermissions(permissions, 0);
            }

            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_select).setOnClickListener(v -> {
            onClickSelect();
        });
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            onClickStart();
        });
        findViewById(R.id.btn_select_manifestjson).setOnClickListener(v -> {
            selectManifestJson();
        });
    }

    private void onClickSelect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                ToastUtils.showShort("没有拿到权限");
                return;
            }
        }

        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(getFragmentManager())
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.setOnSelectListener(path -> {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            etPath.setText(path);
        });
        chooser.show();
    }

    private void onClickStart() {
        String path = getInputPath();
        File gameDir;
        if (TextUtils.isEmpty(path) || !(gameDir = new File(path)).exists()) {
            ToastUtils.showShort("游戏目录不存在");
            return;
        }

        GameModel gameModel = new GameModel();
        gameModel.gameName = gameDir.getName();
        gameModel.gameId = gameModel.gameName;
        gameModel.gameId = "1468180338417074177";
        gameModel.gamePkgVersion = "1.0.0";
        gameModel.gameUrl = gameDir.getAbsolutePath();
        gameModel.pathType = SUDOPGamePathType.DIR;
        gameModel.manifestJson = manifestJson;
        QuickStartGameActivity.start(this, gameModel);
        GlobalSP.getSP().put(GlobalSP.KEY_GAME_DIR_PATH, gameModel.gameUrl);
    }

    private String getInputPath() {
        Editable text = etPath.getText();
        if (text == null) {
            return null;
        }
        return text.toString();
    }

    private void selectManifestJson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                ToastUtils.showShort("没有拿到权限");
                initPermission();
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, _REQUEST_CODE_PICK_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != _REQUEST_CODE_PICK_JSON || resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri manifestJsonUid = data.getData();
        if (manifestJsonUid == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String json = readTextFromUri(manifestJsonUid);
                    manifestJson = parseManifestJson(json);
                    ThreadUtils.runOnUiThread(() -> {
                        tvManifestInfo.setText(manifestJson);
                    });
                    ToastUtils.showShort("读取manifest.json完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showShort("异常：" + e);
                }
            }
        }.start();
    }

    private String parseManifestJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray subpackagesArr = obj.optJSONArray("subpackages");
            if (subpackagesArr != null && subpackagesArr.length() > 0) {
                for (int i = 0; i < subpackagesArr.length(); i++) {
                    JSONObject subpackageObj = subpackagesArr.getJSONObject(i);
                    String name = subpackageObj.getString("name");
                    subpackageObj.put("root", name + "/");
                }
            }
            JSONObject pluginsObj = obj.optJSONObject("plugins");
            if (pluginsObj != null) {
                obj.put("resolvedPlugins", pluginsObj); // 这是因为runtime3，只解析这个字段了
            }
            return obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        inputStream.close();
        return builder.toString();
    }

}
