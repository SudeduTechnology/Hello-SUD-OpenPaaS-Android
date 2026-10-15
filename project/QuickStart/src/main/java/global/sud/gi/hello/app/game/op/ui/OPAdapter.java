package global.sud.gi.hello.app.game.op.ui;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.utils.ImageLoader;
import global.sud.op.runtime.core.model.SUDOPGameListItem;

public class OPAdapter extends BaseQuickAdapter<SUDOPGameListItem, BaseViewHolder> {

    private final String languageTag;

    public OPAdapter(String languageTag) {
        super(R.layout.item_op);
        this.languageTag = languageTag;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SUDOPGameListItem item) {
        holder.setText(R.id.op_game_name,
                OPPresentation.resolveName(item, languageTag));
        ImageLoader.loadGameCover(
                holder.getView(R.id.op_game_icon),
                OPPresentation.resolveIcon(item, languageTag)
        );
    }
}
