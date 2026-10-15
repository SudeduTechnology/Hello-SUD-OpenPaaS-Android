package global.sud.gi.SUDGIWrapper.custom.ui.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreviewImageView extends FrameLayout {

    private ViewPager2 viewPager2;
    private TextView tvProgress;
    private ImageAdapter adapter;

    private final List<String> imageList = new ArrayList<>();

    private OnImageClickListener onImageClickListener;

    private final ViewPager2.OnPageChangeCallback pageChangeCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateProgress(position);
                }
            };

    public PreviewImageView(@NonNull Context context) {
        this(context, null);
    }

    public PreviewImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.BLACK);

        viewPager2 = new ViewPager2(context);
        viewPager2.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        adapter = new ImageAdapter();
        viewPager2.setAdapter(adapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.registerOnPageChangeCallback(pageChangeCallback);

        addView(viewPager2);

        tvProgress = new TextView(context);
        tvProgress.setTextColor(Color.WHITE);
        tvProgress.setTextSize(16);
        tvProgress.setGravity(Gravity.CENTER);

        LayoutParams progressLp = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        progressLp.topMargin = getStatusBarHeight(context) + dp(32);

        addView(tvProgress, progressLp);
    }

    /**
     * 设置图片数据
     *
     * @param urls    图片地址数组
     * @param current 当前需要展示的图片地址
     */
    public void setImages(@Nullable String[] urls, @Nullable String current) {
        imageList.clear();

        if (urls != null && urls.length > 0) {
            imageList.addAll(Arrays.asList(urls));
        }

        adapter.notifyDataSetChanged();

        int index = findCurrentIndex(current);

        if (!imageList.isEmpty()) {
            viewPager2.setCurrentItem(index, false);
            updateProgress(index);
        } else {
            updateProgress(0);
        }
    }

    /**
     * 设置图片数据
     */
    public void setImages(@Nullable List<String> urls, @Nullable String current) {
        imageList.clear();

        if (urls != null && !urls.isEmpty()) {
            imageList.addAll(urls);
        }

        adapter.notifyDataSetChanged();

        int index = findCurrentIndex(current);

        if (!imageList.isEmpty()) {
            viewPager2.setCurrentItem(index, false);
            updateProgress(index);
        } else {
            updateProgress(0);
        }
    }

    private int findCurrentIndex(@Nullable String current) {
        if (TextUtils.isEmpty(current) || imageList.isEmpty()) {
            return 0;
        }

        for (int i = 0; i < imageList.size(); i++) {
            if (current.equals(imageList.get(i))) {
                return i;
            }
        }

        return 0;
    }

    private void updateProgress(int index) {
        int total = imageList.size();

        if (total <= 0) {
            tvProgress.setText("0 / 0");
        } else {
            tvProgress.setText((index + 1) + " / " + total);
        }
    }

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    public TextView getProgressView() {
        return tvProgress;
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.onImageClickListener = listener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public interface OnImageClickListener {
        void onImageClick(int position, String url);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FrameLayout root = new FrameLayout(parent.getContext());
            root.setBackgroundColor(Color.BLACK);

            RecyclerView.LayoutParams rootLp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            root.setLayoutParams(rootLp);

            ImageView imageView = new ImageView(parent.getContext());
            imageView.setBackgroundColor(Color.BLACK);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LayoutParams imageLp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            root.addView(imageView, imageLp);

            return new ImageViewHolder(root, imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String url = imageList.get(position);

            Glide.with(holder.imageView)
                    .load(url)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClick(position, url);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        @Override
        public void onViewRecycled(@NonNull ImageViewHolder holder) {
            super.onViewRecycled(holder);
            Glide.with(holder.imageView).clear(holder.imageView);
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView, ImageView imageView) {
            super(itemView);
            this.imageView = imageView;
        }
    }
}
