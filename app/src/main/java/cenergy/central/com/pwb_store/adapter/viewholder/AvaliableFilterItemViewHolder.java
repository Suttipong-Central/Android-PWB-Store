package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.StoreFilterItemBus;
import cenergy.central.com.pwb_store.model.StoreFilterItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

public class AvaliableFilterItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private PowerBuyTextView mTxtItem;

    public AvaliableFilterItemViewHolder(View itemView) {
        super(itemView);
        mTxtItem = itemView.findViewById(R.id.txt_item_filter);
    }

    public void setViewHolder(StoreFilterItem storeFilterItem) {
        itemView.setTag(storeFilterItem);

        mTxtItem.setText(storeFilterItem.getFilterName());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),storeFilterItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        StoreFilterItem storeFilterItem = (StoreFilterItem) itemView.getTag();
        storeFilterItem.setSelected(!storeFilterItem.isSelected());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),storeFilterItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        EventBus.getDefault().post(new StoreFilterItemBus(storeFilterItem, getAdapterPosition()));
    }
}
