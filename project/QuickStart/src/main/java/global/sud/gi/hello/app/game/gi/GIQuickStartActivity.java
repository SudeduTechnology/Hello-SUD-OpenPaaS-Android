package global.sud.gi.hello.app.game.gi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.hello.app.common.utils.ViewUtils;
import global.sud.gi.hello.app.common.widget.view.SimpleTextWatcher;
import global.sud.gi.hello.app.common.widget.view.round.RoundedImageView;
import global.sud.gi.hello.app.game.gi.data.GIGameRepository;
import global.sud.gi.hello.app.game.gi.data.GIGameModel;

/**
 * 主页
 */
public class GIQuickStartActivity extends BaseActivity {

    private final MyAdapter adapter = new MyAdapter();
    private final String defaultRoomId = AppConfig.GI_DEFAULT_ROOM_ID; // 默认使用的房间Id

    private EditText editText;
    private TextView tvEnter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, GIQuickStartActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gi_quick_start;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        editText = findViewById(R.id.et_room_id);
        tvEnter = findViewById(R.id.tv_enter);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        editText.setHint(defaultRoomId);

        adapter.setHeaderView(getHeaderView());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private View getHeaderView() {
        LinearLayout container = new LinearLayout(this);

        RoundedImageView iv = new RoundedImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setImageResource(R.drawable.ic_quick_start);
        int radius = DensityUtils.dp2px(this, 8);
        iv.setCornerRadius(radius, radius, 0, 0);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.topMargin = DensityUtils.dp2px(this, 8);
        int marginHorizontal = DensityUtils.dp2px(this, 5);
        params.setMarginStart(marginHorizontal);
        params.setMarginEnd(marginHorizontal);
        container.addView(iv, params);

        return container;
    }

    @Override
    protected void initData() {
        super.initData();
        List<GIGameModel> gameList = GIGameRepository.getGameList();
        adapter.setList(gameList);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                onClickItemGame(position);
            }
        });
        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputRoomId = getInputRoomId();
                if (TextUtils.isEmpty(inputRoomId)) {
                    tvEnter.setVisibility(View.GONE);
                } else {
                    tvEnter.setVisibility(View.VISIBLE);
                }
            }
        });
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                return onClickEnterRoom();
            }
            return false;
        });
        tvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEnterRoom();
            }
        });
    }

    /** 输入自定义房间号 打开游戏页面 */
    private boolean onClickEnterRoom() {
        String inputRoomId = getInputRoomId();
        if (TextUtils.isEmpty(inputRoomId)) {
            return false;
        }
        startGameActivity(inputRoomId, 0);
        return true;
    }

    /** 获取输入的房间号 */
    private String getInputRoomId() {
        return ViewUtils.getEditTextText(editText);
    }

    /** 点击了游戏列表中的游戏 */
    private void onClickItemGame(int position) {
        GIGameModel model = adapter.getItem(position);
        startGameActivity(defaultRoomId, model.gameId);
    }

    /** 打开游戏页面 */
    private void startGameActivity(String roomId, long gameId) {
        GIGameActivity.start(this, roomId, gameId);
    }

    // 游戏列表适配器
    private static class MyAdapter extends BaseQuickAdapter<GIGameModel, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_gi_home_game);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GIGameModel gameModel) {
            holder.setImageResource(R.id.iv_icon, gameModel.homeGamePic);
            holder.setText(R.id.tv_name, gameModel.gameName);
        }
    }

    // region 点击屏幕空白区域隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this);
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view.
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (!KeyboardUtils.isSoftInputVisible(this)) return false;
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            int left = l[0];
            int top = l[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            return !(rawX > left && rawX < right && rawY > top && rawY < bottom);
        }
        return false;
    }
    // endregion 点击屏幕空白区域隐藏软键盘

}
