package global.sud.gi.SUDGIWrapper.custom.ui.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LoadingHintView extends View {

    /**
     * 提示的内容
     */
    public String title = "";

    /**
     * 是否显示透明蒙层，防止触摸穿透
     */
    public boolean mask = true;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private float density;
    private boolean attached;

    private final int spinnerCount = 12;

    public LoadingHintView(Context context) {
        super(context);
        init(context);
    }

    public LoadingHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp(15));
        textPaint.setTextAlign(Paint.Align.CENTER);

        setWillNotDraw(false);
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void setMask(boolean mask) {
        this.mask = mask;
        invalidate();
    }

    public void setLoading(String title, boolean mask) {
        this.title = title;
        this.mask = mask;
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attached = true;
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        attached = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        // 透明蒙层
        if (mask) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0x66000000);
            canvas.drawRect(0, 0, width, height, paint);
        }

        drawLoadingBox(canvas, width, height);

        if (attached) {
            postInvalidateOnAnimation();
        }
    }

    private void drawLoadingBox(Canvas canvas, int width, int height) {
        float boxWidth = dp(120);
        float boxHeight = dp(112);

        float centerX = width / 2f;
        float centerY = height / 2f;

        float left = centerX - boxWidth / 2f;
        float top = centerY - boxHeight / 2f;
        float right = centerX + boxWidth / 2f;
        float bottom = centerY + boxHeight / 2f;

        rectF.set(left, top, right, bottom);

        // 深色圆角背景
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xDD333333);
        canvas.drawRoundRect(rectF, dp(8), dp(8), paint);

        // loading spinner
        float spinnerCenterX = centerX;
        float spinnerCenterY = centerY - dp(18);
        drawSpinner(canvas, spinnerCenterX, spinnerCenterY);

        // 文字
        drawTitle(canvas, centerX, centerY + dp(32), boxWidth);
    }

    private void drawSpinner(Canvas canvas, float cx, float cy) {
        long time = System.currentTimeMillis();
        int activeIndex = (int) ((time / 80) % spinnerCount);

        float innerRadius = dp(10);
        float outerRadius = dp(16);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(3));
        paint.setStrokeCap(Paint.Cap.ROUND);

        for (int i = 0; i < spinnerCount; i++) {
            int index = (i + activeIndex) % spinnerCount;

            int alpha = 255 - index * 15;
            if (alpha < 50) {
                alpha = 50;
            }

            paint.setColor(Color.argb(alpha, 255, 255, 255));

            double angle = Math.toRadians(i * 360f / spinnerCount);

            float startX = cx + (float) Math.cos(angle) * innerRadius;
            float startY = cy + (float) Math.sin(angle) * innerRadius;
            float stopX = cx + (float) Math.cos(angle) * outerRadius;
            float stopY = cy + (float) Math.sin(angle) * outerRadius;

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    private void drawTitle(Canvas canvas, float centerX, float baselineY, float boxWidth) {
        if (TextUtils.isEmpty(title)) {
            return;
        }

        float maxTextWidth = boxWidth - dp(20);

        CharSequence showText = TextUtils.ellipsize(
                title,
                textPaint,
                maxTextWidth,
                TextUtils.TruncateAt.END
        );

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float realBaseline = baselineY - (fontMetrics.ascent + fontMetrics.descent) / 2f;

        canvas.drawText(showText.toString(), centerX, realBaseline, textPaint);
    }

    /**
     * mask = true 时消费事件，防止触摸穿透。
     * mask = false 时不消费事件，允许点击下层 View。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mask;
    }

    private float dp(float value) {
        return value * density + 0.5f;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity + 0.5f;
    }
}
