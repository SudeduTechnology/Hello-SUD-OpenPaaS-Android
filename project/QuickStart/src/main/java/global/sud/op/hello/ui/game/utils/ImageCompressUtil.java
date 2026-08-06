package global.sud.op.hello.ui.game.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressUtil {

    /**
     * 将 Uri 图片压缩后保存到 outDir，返回新文件
     *
     * @param context Context
     * @param uri     content:// 或 file://
     * @param outDir  输出目录（不存在会自动创建）
     * @param maxSize 目标最长边（如 1080）
     * @param quality 质量（0~100）
     */
    public static File compressUriToFile(
            @NonNull Context context,
            @NonNull Uri uri,
            @NonNull File outDir,
            int maxSize,
            int quality
    ) throws IOException {

        if (!outDir.exists()) outDir.mkdirs();

        ContentResolver resolver = context.getContentResolver();

        // 1. 先读尺寸（不加载像素）
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is1 = resolver.openInputStream(uri);
        BitmapFactory.decodeStream(is1, null, options);
        closeQuietly(is1);

        int srcW = options.outWidth;
        int srcH = options.outHeight;

        // 2. 计算采样率（避免OOM）
        options.inSampleSize = calculateInSampleSize(srcW, srcH, maxSize, maxSize);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // 3. 真正解码
        InputStream is2 = resolver.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(is2, null, options);
        closeQuietly(is2);

        if (bitmap == null) {
            throw new IOException("decode bitmap failed");
        }

        // 4. 再按最长边缩放（更精确）
        Bitmap scaled = scaleBitmap(bitmap, maxSize);
        if (scaled != bitmap) {
            bitmap.recycle();
        }

        // 5. 处理旋转（Exif）
        int degree = getExifRotation(context, uri);
        Bitmap rotated = rotateIfNeeded(scaled, degree);
        if (rotated != scaled) {
            scaled.recycle();
        }

        // 6. 生成输出文件
        String name = buildFileName(context, uri);
        File outFile = new File(outDir, name);

        FileOutputStream fos = new FileOutputStream(outFile);
        Bitmap.CompressFormat format = chooseFormat(context, uri);
        int q = (format == Bitmap.CompressFormat.PNG) ? 100 : quality;
        rotated.compress(format, q, fos);

        fos.flush();
        fos.close();

        rotated.recycle();

        return outFile;
    }

    // 计算 inSampleSize
    private static int calculateInSampleSize(int srcW, int srcH, int reqW, int reqH) {
        int inSampleSize = 1;
        if (srcH > reqH || srcW > reqW) {
            final int halfH = srcH / 2;
            final int halfW = srcW / 2;
            while ((halfH / inSampleSize) >= reqH
                    && (halfW / inSampleSize) >= reqW) {
                inSampleSize *= 2;
            }
        }
        return Math.max(1, inSampleSize);
    }

    // 按最长边缩放
    private static Bitmap scaleBitmap(Bitmap src, int maxSize) {
        int w = src.getWidth();
        int h = src.getHeight();
        int max = Math.max(w, h);
        if (max <= maxSize) return src;

        float ratio = maxSize * 1f / max;
        int newW = Math.round(w * ratio);
        int newH = Math.round(h * ratio);

        return Bitmap.createScaledBitmap(src, newW, newH, true);
    }

    // 读取Exif旋转角度
    private static int getExifRotation(Context context, Uri uri) {
        try (InputStream is = context.getContentResolver().openInputStream(uri)) {
            if (is == null) return 0;
            ExifInterface exif = new ExifInterface(is);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception ignore) {
            return 0;
        }
    }

    // 旋转
    private static Bitmap rotateIfNeeded(Bitmap src, int degree) {
        if (degree == 0) return src;
        android.graphics.Matrix m = new android.graphics.Matrix();
        m.postRotate(degree);
        Bitmap rotated = Bitmap.createBitmap(src, 0, 0,
                src.getWidth(), src.getHeight(), m, true);
        return rotated;
    }

    // 生成文件名（优先原名）
    private static String buildFileName(Context context, Uri uri) {
        String name = queryDisplayName(context, uri);
        if (name == null || name.trim().isEmpty()) {
            name = System.currentTimeMillis() + ".jpg";
        } else {
            // 强制jpg后缀
            int dot = name.lastIndexOf('.');
            if (dot > 0) name = name.substring(0, dot);
            name = name + "_" + System.currentTimeMillis() + ".jpg";
        }
        return name;
    }

    private static String queryDisplayName(Context context, Uri uri) {
        if (!"content".equals(uri.getScheme())) return null;
        try (android.database.Cursor c =
                     context.getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return c.getString(idx);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ignore) {
            }
        }
    }

    private static Bitmap.CompressFormat chooseFormat(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        if (type != null) {
            if (type.contains("png")) {
                return Bitmap.CompressFormat.PNG;
            } else if (type.contains("webp")) {
                return Bitmap.CompressFormat.WEBP;
            }
        }
        return Bitmap.CompressFormat.JPEG;
    }

    public static String getSurffix(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        if (type != null) {
            if (type.contains("png")) {
                return "png";
            } else if (type.contains("webp")) {
                return "webp";
            }
        }
        return "jpg";
    }
}
