package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.PromotionItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/2/2017 AD.
 */

public class PromotionItemProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.layout_promotion_product)
    LinearLayout mLinearLayout;

    @BindView(R.id.txt_header)
    PowerBuyTextView header;

    @BindView(R.id.txt_detail)
    PowerBuyTextView detail;

    public PromotionItemProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(PromotionItem promotionItem){
        header.setText(promotionItem.getHeader());
        detail.setText(promotionItem.getDetail());
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
