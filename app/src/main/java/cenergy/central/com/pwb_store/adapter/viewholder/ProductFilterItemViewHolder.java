package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ProductFilterItemViewHolder";


    private PowerBuyTextView mTxtItem;

    public ProductFilterItemViewHolder(View itemView) {
        super(itemView);
        mTxtItem = itemView.findViewById(R.id.txt_item_filter);
    }

    public void setViewHolder(ProductFilterItem productFilterItem) {
        itemView.setTag(productFilterItem);

        mTxtItem.setText(productFilterItem.getFilterName());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),productFilterItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ProductFilterItem productFilterItem = (ProductFilterItem) itemView.getTag();
        productFilterItem.setSelected(!productFilterItem.isSelected());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),productFilterItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
//        EventBus.getDefault().post(new ProductFilterItemBus(productFilterItem, getAdapterPosition()));
    }
}
