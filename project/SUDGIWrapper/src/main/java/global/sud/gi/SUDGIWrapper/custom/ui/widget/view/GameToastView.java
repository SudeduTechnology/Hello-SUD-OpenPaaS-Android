package global.sud.gi.SUDGIWrapper.custom.ui.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

public class GameToastView extends View {

    public static final String ICON_SUCCESS = "success";
    public static final String ICON_ERROR = "error";
    public static final String ICON_LOADING = "loading";
    public static final String ICON_NONE = "none";

    /**
     * 提示的内容
     */
    public String title = "";

    /**
     * success 显示成功图标
     * error 显示失败图标
     * loading 显示加载图标
     * none 不显示图标
     */
    public String icon = ICON_NONE;

    /**
     * 自定义图标的本地路径，image 的优先级高于 icon
     */
    public String image;

    /**
     * 提示的延迟时间，单位毫秒
     */
    public int duration = 1500;

    /**
     * 是否显示透明蒙层，防止触摸穿透
     */
    public boolean mask = false;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private float density;
    private float scaledDensity;

    private boolean attached = false;

    private Bitmap customBitmap;
    private String lastImagePath;

    private final Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    public GameToastView(Context context) {
        super(context);
        init(context);
    }

    public GameToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameToastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

        setWillNotDraw(false);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp(16));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setToast(String title, String icon, String image, int duration, boolean mask) {
        this.title = title;
        this.icon = icon;
        this.image = image;
        this.duration = duration;
        this.mask = mask;

        reloadCustomBitmapIfNeed();
        invalidate();

        startAutoDismiss();
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void setIcon(String icon) {
        this.icon = icon;
        invalidate();
    }

    public void setImage(String image) {
        this.image = image;
        reloadCustomBitmapIfNeed();
        invalidate();
    }

    public void setDuration(int duration) {
        this.duration = duration;
        startAutoDismiss();
    }

    public void setMask(boolean mask) {
        this.mask = mask;
        invalidate();
    }

    /**
     * 显示 ToastView。
     */
    public void show() {
        setVisibility(VISIBLE);
        startAutoDismiss();
    }

    /**
     * 隐藏并从父布局移除。
     */
    public void dismiss() {
        removeCallbacks(dismissRunnable);

        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        } else {
            setVisibility(GONE);
        }
    }

    private void startAutoDismiss() {
        removeCallbacks(dismissRunnable);

        if (duration > 0) {
            postDelayed(dismissRunnable, duration);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;
        reloadCustomBitmapIfNeed();
        startAutoDismiss();
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        attached = false;
        removeCallbacks(dismissRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        if (viewWidth <= 0 || viewHeight <= 0) {
            return;
        }

        if (mask) {
            drawMask(canvas, viewWidth, viewHeight);
        }

        boolean hasCustomImage = hasCustomImage();
        boolean hasIcon = hasCustomImage || !ICON_NONE.equals(icon);

        if (hasIcon) {
            drawIconToast(canvas, viewWidth, viewHeight, hasCustomImage);
        } else {
            drawTextOnlyToast(canvas, viewWidth, viewHeight);
        }

        if (attached && ICON_LOADING.equals(icon) && !hasCustomImage()) {
            postInvalidateOnAnimation();
        }
    }

    private void drawMask(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x66000000);
        canvas.drawRect(0, 0, width, height, paint);
    }

    /**
     * 带图标的 Toast，类似截图中的成功、失败、提示样式。
     */
    private void drawIconToast(Canvas canvas, int viewWidth, int viewHeight, boolean hasCustomImage) {
        IconToastLayout toastLayout = calculateIconToastLayout(title, viewWidth);
        float boxWidth = toastLayout.boxWidth;
        float boxHeight = toastLayout.boxHeight;
        StaticLayout titleLayout = toastLayout.titleLayout;

        float centerX = viewWidth / 2f;
        float centerY = viewHeight / 2f;

        float left = centerX - boxWidth / 2f;
        float top = centerY - boxHeight / 2f;
        float right = centerX + boxWidth / 2f;
        float bottom = centerY + boxHeight / 2f;

        rectF.set(left, top, right, bottom);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xDD333333);
        canvas.drawRoundRect(rectF, dp(8), dp(8), paint);

        float iconCenterX = centerX;
        float iconCenterY = top + dp(43);

        if (hasCustomImage) {
            drawCustomImage(canvas, iconCenterX, iconCenterY);
        } else {
            drawSystemIcon(canvas, iconCenterX, iconCenterY);
        }

        if (titleLayout != null) {
            float textLeft = centerX - titleLayout.getWidth() / 2f;
            float textTop = top + dp(21) + dp(44) + dp(12);
            canvas.save();
            canvas.translate(textLeft, textTop);
            titleLayout.draw(canvas);
            canvas.restore();
        }
    }

    IconToastLayout calculateIconToastLayout(String title, int viewWidth) {
        String text = title == null ? "" : title;
        float horizontalPadding = dp(18);
        float maxBoxWidth = Math.max(dp(1), Math.min(dp(320), viewWidth - dp(40)));
        float desiredBoxWidth = TextUtils.isEmpty(text) ? dp(130) : Layout.getDesiredWidth(text, textPaint) + horizontalPadding * 2;
        float boxWidth = Math.min(maxBoxWidth, Math.max(dp(130), desiredBoxWidth));
        int textWidth = Math.max(1, Math.round(boxWidth - horizontalPadding * 2));
        StaticLayout titleLayout = TextUtils.isEmpty(text) ? null : buildIconModeTitleLayout(text, textWidth);
        float titleHeight = titleLayout == null ? 0 : titleLayout.getHeight();
        float boxHeight = Math.max(dp(122), dp(21) + dp(44) + dp(12) + titleHeight + dp(18));
        return new IconToastLayout(boxWidth, boxHeight, titleLayout);
    }

    /**
     * 无图标 Toast。
     */
    private void drawTextOnlyToast(Canvas canvas, int viewWidth, int viewHeight) {
        String text = title == null ? "" : title;

        float maxTextWidth = Math.min(viewWidth - dp(80), dp(260));
        CharSequence showText = TextUtils.ellipsize(
                text,
                textPaint,
                maxTextWidth,
                TextUtils.TruncateAt.END
        );

        float textWidth = textPaint.measureText(showText.toString());
        float horizontalPadding = dp(22);
        float boxWidth = Math.max(dp(96), textWidth + horizontalPadding * 2);
        float boxHeight = dp(48);

        float centerX = viewWidth / 2f;
        float centerY = viewHeight / 2f;

        rectF.set(
                centerX - boxWidth / 2f,
                centerY - boxHeight / 2f,
                centerX + boxWidth / 2f,
                centerY + boxHeight / 2f
        );

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xDD333333);
        canvas.drawRoundRect(rectF, dp(8), dp(8), paint);

        drawSingleLineText(canvas, showText.toString(), centerX, centerY, boxWidth - horizontalPadding * 2);
    }

    private void drawSystemIcon(Canvas canvas, float cx, float cy) {
        if (ICON_SUCCESS.equals(icon)) {
            drawSuccessIcon(canvas, cx, cy);
        } else if (ICON_ERROR.equals(icon)) {
            drawErrorIcon(canvas, cx, cy);
        } else if (ICON_LOADING.equals(icon)) {
            drawLoadingIcon(canvas, cx, cy);
        } else {
            drawInfoIcon(canvas, cx, cy);
        }
    }

    /**
     * 成功图标：白色圆 + 深色对勾。
     */
    private void drawSuccessIcon(Canvas canvas, float cx, float cy) {
        float radius = dp(22);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(0xFF333333);

        Path path = new Path();
        path.moveTo(cx - dp(10), cy);
        path.lineTo(cx - dp(2), cy + dp(8));
        path.lineTo(cx + dp(12), cy - dp(9));
        canvas.drawPath(path, paint);
    }

    /**
     * 失败图标：白色圆 + 深色叉号。
     */
    private void drawErrorIcon(Canvas canvas, float cx, float cy) {
        float radius = dp(22);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(0xFF333333);

        canvas.drawLine(cx - dp(8), cy - dp(8), cx + dp(8), cy + dp(8), paint);
        canvas.drawLine(cx + dp(8), cy - dp(8), cx - dp(8), cy + dp(8), paint);
    }

    /**
     * 提示图标：白色圆 + 感叹号。
     * 当 icon 不是 success/error/loading/none 时，默认画这个。
     */
    private void drawInfoIcon(Canvas canvas, float cx, float cy) {
        float radius = dp(22);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(4));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(0xFF333333);

        canvas.drawLine(cx, cy - dp(10), cx, cy + dp(4), paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy + dp(12), dp(2.4f), paint);
    }

    /**
     * loading 菊花图标。
     */
    private void drawLoadingIcon(Canvas canvas, float cx, float cy) {
        int count = 12;
        long time = System.currentTimeMillis();
        int activeIndex = (int) ((time / 80) % count);

        float innerRadius = dp(10);
        float outerRadius = dp(18);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < count; i++) {
            int index = (i + activeIndex) % count;
            int alpha = 255 - index * 16;
            if (alpha < 45) {
                alpha = 45;
            }

            paint.setColor(Color.argb(alpha, 255, 255, 255));

            double angle = Math.toRadians(i * 360f / count);

            float startX = cx + (float) Math.cos(angle) * innerRadius;
            float startY = cy + (float) Math.sin(angle) * innerRadius;
            float stopX = cx + (float) Math.cos(angle) * outerRadius;
            float stopY = cy + (float) Math.sin(angle) * outerRadius;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    private void drawCustomImage(Canvas canvas, float cx, float cy) {
        if (customBitmap == null || customBitmap.isRecycled()) {
            drawInfoIcon(canvas, cx, cy);
            return;
        }

        float size = dp(46);
        RectF dst = new RectF(
                cx - size / 2f,
                cy - size / 2f,
                cx + size / 2f,
                cy + size / 2f
        );

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
        canvas.drawBitmap(customBitmap, null, dst, paint);
    }

    private void drawSingleLineText(Canvas canvas, String text, float centerX, float centerY, float maxWidth) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        CharSequence showText = TextUtils.ellipsize(
                text,
                textPaint,
                maxWidth,
                TextUtils.TruncateAt.END
        );

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float baseline = centerY - (fm.ascent + fm.descent) / 2f;

        canvas.drawText(showText.toString(), centerX, baseline, textPaint);
    }

    @SuppressWarnings("deprecation")
    StaticLayout buildIconModeTitleLayout(String text, int width) {
        TextPaint layoutPaint = new TextPaint(textPaint);
        layoutPaint.setTextAlign(Paint.Align.LEFT);
        return new StaticLayout(
                text == null ? "" : text,
                layoutPaint,
                Math.max(1, width),
                Layout.Alignment.ALIGN_CENTER,
                1f,
                0f,
                false
        );
    }

    static final class IconToastLayout {
        final float boxWidth;
        final float boxHeight;
        final StaticLayout titleLayout;

        IconToastLayout(float boxWidth, float boxHeight, StaticLayout titleLayout) {
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.titleLayout = titleLayout;
        }
    }

    private boolean hasCustomImage() {
        return !TextUtils.isEmpty(image) && customBitmap != null && !customBitmap.isRecycled();
    }

    private void reloadCustomBitmapIfNeed() {
        if (TextUtils.isEmpty(image)) {
            customBitmap = null;
            lastImagePath = null;
            return;
        }

        if (image.equals(lastImagePath) && customBitmap != null && !customBitmap.isRecycled()) {
            return;
        }

        lastImagePath = image;

        File file = new File(image);
        if (!file.exists()) {
            customBitmap = null;
            return;
        }

        customBitmap = BitmapFactory.decodeFile(image);
    }

    /**
     * mask = true 时消费事件，防止触摸穿透。
     * mask = false 时不消费事件，允许事件继续往下层 View 传。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mask;
    }

    private float dp(float value) {
        return value * density + 0.5f;
    }

    private float sp(float value) {
        return value * scaledDensity + 0.5f;
    }
}
