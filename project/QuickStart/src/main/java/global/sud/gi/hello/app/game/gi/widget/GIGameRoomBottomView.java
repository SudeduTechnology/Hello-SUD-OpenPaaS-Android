package global.sud.gi.hello.app.game.gi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import global.sud.gi.hello.R;

public class GIGameRoomBottomView extends ConstraintLayout {

    private ImageView mIvMicState;
    private TextView mTvInput;
    private View mViewGift;

    private boolean micOpened = false;

    public GIGameRoomBottomView(@NonNull Context context) {
        this(context, null);
    }

    public GIGameRoomBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GIGameRoomBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_gi_game_room_bottom, this);
        mIvMicState = findViewById(R.id.iv_mic_state);
        mTvInput = findViewById(R.id.tv_input);
        mViewGift = findViewById(R.id.view_gift);
    }

    public void showMicState() {
        mIvMicState.setVisibility(View.VISIBLE);
    }

    public void hideMicState() {
        mIvMicState.setVisibility(View.GONE);
    }

    public boolean isMicOpened() {
        return micOpened;
    }

    public void setMicOpened(boolean micOpened) {
        this.micOpened = micOpened;
        if (micOpened) {
            mIvMicState.setImageResource(R.drawable.ic_mic_opened);
        } else {
            mIvMicState.setImageResource(R.drawable.ic_mic_closed);
        }
    }

    public void setInputClickListener(OnClickListener listener) {
        mTvInput.setOnClickListener(listener);
    }

    /**
     * 麦克风按钮点击监听
     */
    public void setMicStateClickListener(OnClickListener listener) {
        mIvMicState.setOnClickListener(listener);
    }

    public void setGiftClickListener(OnClickListener listener) {
        mViewGift.setOnClickListener(listener);
    }

}
