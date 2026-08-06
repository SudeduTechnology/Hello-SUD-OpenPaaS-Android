package global.sud.op.hello.ui.game.utils;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;

public class QgClientUtils {
    public static Bitmap rgbaToBitmap(byte[] rgba, int width, int height) {
        int[] pixels = new int[width * height];

        for (int i = 0, j = 0; i < pixels.length; i++, j += 4) {
            int r = rgba[j] & 0xFF;
            int g = rgba[j + 1] & 0xFF;
            int b = rgba[j + 2] & 0xFF;
            int a = rgba[j + 3] & 0xFF;

            // ARGB_8888
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static void saveAsJpeg(Bitmap bitmap, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } finally {
            fos.close();
        }
    }

    public static void saveAsPng(Bitmap bitmap, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } finally {
            fos.close();
        }
    }

    public static void rgbaToJpeg(byte[] rgba, int width, int height, String path) throws IOException {
        Bitmap bitmap = rgbaToBitmap(rgba, width, height);
        saveAsJpeg(bitmap, path);
    }

    public static void rgbaToPng(byte[] rgba, int width, int height, String path) throws IOException {
        Bitmap bitmap = rgbaToBitmap(rgba, width, height);
        saveAsPng(bitmap, path);
    }

}
