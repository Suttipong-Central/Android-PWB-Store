package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.StoreFilterHeaderBus;
import cenergy.central.com.pwb_store.model.StoreFilterHeader;
import cenergy.central.com.pwb_store.model.StoreFilterItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableFilterHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "AvaliableFilterHeaderViewHolder";

    @BindView(R.id.txt_header_filter)
    PowerBuyTextView mTxtHeader;

    @BindView(R.id.img_filter_header)
    ImageView mImgFilterHeader;

    @BindView(R.id.img_fill_back)
    ImageView mImgBack;


    public AvaliableFilterHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
    }

    public void setViewHolder(StoreFilterHeader storeFilterHeader) {
        String filter = countFilter(storeFilterHeader);
        mTxtHeader.setText(filter);
        itemView.setTag(storeFilterHeader);
        itemView.setOnClickListener(this);
        syncIndicator(storeFilterHeader);
    }

    private void syncIndicator(StoreFilterHeader storeFilterHeader) {
        if (!storeFilterHeader.isExpanded()) {
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
        } else {
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
        }
    }

    @Override
    public void onClick(View view) {
        StoreFilterHeader storeFilterHeader = (StoreFilterHeader) itemView.getTag();

        if (!storeFilterHeader.isExpanded()) {
            storeFilterHeader.setExpanded(true);
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
            EventBus.getDefault().post(new StoreFilterHeaderBus(storeFilterHeader, getAdapterPosition()));
        } else {
            storeFilterHeader.setExpanded(false);
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
            EventBus.getDefault().post(new StoreFilterHeaderBus(storeFilterHeader, getAdapterPosition()));
        }
    }

    private String countFilter(StoreFilterHeader storeFilterHeader) {
        String filter;
        int checkCount = 0;

        for (StoreFilterItem storeFilterItem :
                storeFilterHeader.getStoreFilterItemList()) {
            if (storeFilterItem.isSelected()) {
                checkCount++;
            }
        }


        if (checkCount <= 0) {
            filter = storeFilterHeader.getName();
        } else {
            filter = storeFilterHeader.getName() + " (" + checkCount + ")";
        }

        return filter;
    }
}