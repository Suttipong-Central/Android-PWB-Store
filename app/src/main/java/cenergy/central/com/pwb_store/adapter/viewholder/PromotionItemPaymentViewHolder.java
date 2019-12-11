package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.PromotionPaymentItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionItemPaymentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.layout_promotion_payment)
    LinearLayout mLinearLayout;

    @BindView(R.id.txt_header)
    PowerBuyTextView header;

    @BindView(R.id.txt_detail)
    PowerBuyTextView detail;

    public PromotionItemPaymentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(PromotionPaymentItem promotionPaymentItem){
        header.setText(promotionPaymentItem.getDetail());
        if (promotionPaymentItem.getStartDate().length() > 0){
            detail.setText("ระยะเวลาโปรโมชั่น "+ promotionPaymentItem.getStartDate() + " ถึง " + promotionPaymentItem.getEndDate());
        }else {
            detail.setText("");
        }
        mLinearLayout.setTag(promotionPaymentItem);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PromotionPaymentItem promotionPaymentItem = (PromotionPaymentItem) mLinearLayout.getTag();
        //EventBus.getDefault().post(new PromotionItemBus(promotionItem, v));
        Log.d("Click :", "true");
    }
}
