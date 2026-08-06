package global.sud.op.hello.ui.game.widget;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import global.sud.op.hello.R;
import global.sud.op.hello.common.base.BaseDialogFragment;
import global.sud.op.hello.common.utils.DensityUtils;
import global.sud.op.hello.ui.game.model.PaymentSignData;
import global.sud.op.runtime.core.wrapped.SUDOPRequestPaymentParams;


public class PaymentDialog extends BaseDialogFragment {

    private MyAdapter adapter = new MyAdapter();
    private PaymentListener paymentListener;
    private SUDOPRequestPaymentParams paymentParams;

    public PaymentDialog(SUDOPRequestPaymentParams params) {
        this.paymentParams = params;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_payment;
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
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        initPaymentInfoDynamic();
//        initPaymentInfo();
    }

    // 字段没对齐时，直接解析json，不用反射
    private void initPaymentInfoDynamic() {
        try {
            ArrayList<String> list = new ArrayList<>();
            JSONObject obj = new JSONObject(paymentParams.signData);
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                list.add(next + ":" + obj.getString(next));
            }
            adapter.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPaymentInfo() {
        PaymentSignData paymentSignData = null;
        try {
            paymentSignData = GsonUtils.fromJson(paymentParams.signData, PaymentSignData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paymentSignData == null) {
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("gameId:" + paymentSignData.gameId);
        list.add("userId:" + paymentSignData.userId);
        list.add("productId:" + paymentSignData.productId);
        list.add("productName:" + paymentSignData.productName);
        list.add("quantity:" + paymentSignData.quantity);
        list.add("unitPrice:" + paymentSignData.unitPrice);
        list.add("currency:" + paymentSignData.currency);
        list.add("sudTradeNo:" + paymentSignData.sudTradeNo);
        list.add("notifyUrl:" + paymentSignData.notifyUrl);
        adapter.setList(list);
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_success).setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.paymentCompleted(true);
            }
            dismiss();
        });
        findViewById(R.id.btn_fail).setOnClickListener(v -> {
            if (paymentListener != null) {
                paymentListener.paymentCompleted(false);
            }
            dismiss();
        });
    }

    private class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_info, null);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String item) {
            holder.setText(R.id.tv_info, item);
        }
    }

    @Override
    protected boolean cancelable() {
        return false;
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return false;
    }

    public void setPaymentListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    public interface PaymentListener {
        void paymentCompleted(boolean isSuccess);
    }

}
