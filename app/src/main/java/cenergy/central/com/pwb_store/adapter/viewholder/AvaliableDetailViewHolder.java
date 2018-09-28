package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.AvaliableStoreItem;
import cenergy.central.com.pwb_store.model.StoreDao;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableDetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_store)
    PowerBuyTextView nameStore;
//
//    @BindView(R.id.text_address)
//    PowerBuyTextView storeAddress;

    @BindView(R.id.text_stock)
    PowerBuyTextView stock;

    private PowerBuyTextView tel = itemView.findViewById(R.id.text_telephone);
    private PowerBuyTextView name = itemView.findViewById(R.id.text_name);


    //Data Member
    private StoreDao mStoreDao;


    public AvaliableDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(AvaliableStoreItem avaliableStoreItem, StoreDao storeDao) {
        this.mStoreDao = storeDao;

        nameStore.setText(avaliableStoreItem.getStoreName());
//        storeAddress.setText(avaliableStoreItem.getAddress());
        stock.setText(String.valueOf(avaliableStoreItem.getStock()));
        tel.setText(avaliableStoreItem.getTelephone());
        name.setText(avaliableStoreItem.getName());
    }
}
