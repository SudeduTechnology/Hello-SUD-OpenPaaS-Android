package global.sud.op.hello.ui.game.widget;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseDialogFragment;
import global.sud.op.hello.common.utils.DensityUtils;
import global.sud.op.hello.service.MainRepository;
import global.sud.op.hello.ui.main.GameModel;

/**
 * 选择游戏模式的弹窗
 */
public class GameModeDialog extends BaseDialogFragment {

    private RecyclerView recyclerView;
    private final int spanCount = 4;
    private final GameModeAdapter gameModeAdapter = new GameModeAdapter();
    private String playingGameId; // 当前正在玩的游戏id
    private SelectGameListener selectGameListener;
    private int runtime;

    /**
     * 设置当前正在玩的游戏id
     *
     * @param playingGameId
     */
    public void setPlayingGameId(String playingGameId) {
        this.playingGameId = playingGameId;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * 设置选中游戏模式的监听
     */
    public void setSelectGameListener(SelectGameListener listener) {
        this.selectGameListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_game_mode;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return (int) (DensityUtils.getAppScreenHeight() * 0.7);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(gameModeAdapter);

        View headView = View.inflate(getContext(), R.layout.view_game_mode_head, null);
        gameModeAdapter.addHeaderView(headView);

        gameModeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                GameModel gameModel = gameModeAdapter.getItem(position);
                selectGameMode(gameModel);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        List<GameModel> runtimeGameList = MainRepository.getRuntimeGameList();
        gameModeAdapter.setList(runtimeGameList);
        gameModeAdapter.addData(0, new GameModel()); // 添加一个关闭游戏选项
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    private void selectGameMode(GameModel model) {
        dismiss();
        if (selectGameListener != null) {
            selectGameListener.onSelectGame(model);
        }
    }

    private class GameModeAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public GameModeAdapter() {
            super(R.layout.item_game_mode);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, GameModel gameModel) {
            ImageView ivIcon = holder.getView(R.id.item_game_mode_iv_icon);

            String gameId = gameModel.gameId;
            if (TextUtils.isEmpty(gameId)) {// 设置为关闭游戏的选项
                holder.setVisible(R.id.item_game_mode_tv_gameing, false);
                ivIcon.setImageResource(R.drawable.ic_close_game);
                holder.setText(R.id.item_game_mode_tv_name, R.string.close_game);
                return;
            }

            ivIcon.setImageResource(gameModel.gamePic);

            if (gameId.equals(playingGameId)) {
                holder.setVisible(R.id.item_game_mode_tv_gameing, true);
            } else {
                holder.setVisible(R.id.item_game_mode_tv_gameing, false);
            }

            holder.setText(R.id.item_game_mode_tv_name, gameModel.gameName);
        }
    }

    public interface SelectGameListener {
        /**
         * 选择了游戏模式
         */
        void onSelectGame(GameModel model);
    }

}
