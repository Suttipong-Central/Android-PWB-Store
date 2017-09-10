package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.Contextor;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "SortingItemViewHolder";


    @BindView(R.id.txt_item_filter)
    PowerBuyTextView mTxtItem;

    public SortingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(SortingItem sortingItem) {
        itemView.setTag(sortingItem);

        mTxtItem.setText(sortingItem.getFilterName());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),sortingItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SortingItem sortingItem = (SortingItem) itemView.getTag();
        sortingItem.setSelected(!sortingItem.isSelected());
        mTxtItem.setTextAppearance(Contextor.getInstance().getContext(),sortingItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        EventBus.getDefault().post(new SortingItemBus(sortingItem, getAdapterPosition()));
    }
}
