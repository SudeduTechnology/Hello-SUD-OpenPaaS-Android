package global.sud.op.hello.ui.game.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UriCopyUtil {

    public static File copyUriToFile(Context context, Uri uri, File targetDir) throws Exception {
        if (context == null || uri == null) {
            throw new IllegalArgumentException("context or uri is null");
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // 1. 获取文件名
        String fileName = getFileName(context, uri);
        if (fileName == null) {
            fileName = System.currentTimeMillis() + ".jpg";
        } else {
            fileName = FileUtils.getFileNameNoExtension(fileName) + "_" + System.currentTimeMillis() + ".jpg";
        }

        File outFile = new File(targetDir, fileName);

        // 2. 打开输入流
        ContentResolver resolver = context.getContentResolver();
        InputStream is = resolver.openInputStream(uri);

        if (is == null) {
            throw new Exception("openInputStream failed");
        }

        // 3. 拷贝
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buffer = new byte[8 * 1024];
        int len;

        while ((len = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        fos.flush();
        fos.close();
        is.close();

        return outFile;
    }

    // 获取文件名
    private static String getFileName(Context context, Uri uri) {
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        return cursor.getString(index);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // fallback
        String path = uri.getPath();
        if (path != null) {
            int index = path.lastIndexOf('/');
            if (index != -1) {
                return path.substring(index + 1);
            }
        }

        return null;
    }
}
