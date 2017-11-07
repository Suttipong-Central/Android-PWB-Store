package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ProductFilterHeaderView";

    @BindView(R.id.txt_header_filter)
    PowerBuyTextView mTxtHeader;

    @BindView(R.id.img_filter_header)
    ImageView mImgFilterHeader;

    @BindView(R.id.img_fill_back)
    ImageView mImgBack;


    public ProductFilterHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
    }

    public void setViewHolder(ProductFilterHeader productFilterHeader) {
        String filter = countFilter(productFilterHeader);
        mTxtHeader.setText(filter);
        itemView.setTag(productFilterHeader);
        itemView.setOnClickListener(this);
        syncIndicator(productFilterHeader);
    }

    private void syncIndicator(ProductFilterHeader productFilterHeader) {
        if (!productFilterHeader.isExpanded()) {
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
        } else {
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
        }
    }

    @Override
    public void onClick(View view) {
        ProductFilterHeader productFilterHeader = (ProductFilterHeader) itemView.getTag();

        if (!productFilterHeader.isExpanded()) {
            productFilterHeader.setExpanded(true);
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
            EventBus.getDefault().post(new ProductFilterHeaderBus(productFilterHeader, getAdapterPosition()));
        } else {
            productFilterHeader.setExpanded(false);
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
            EventBus.getDefault().post(new ProductFilterHeaderBus(productFilterHeader, getAdapterPosition()));
        }
    }

    private String countFilter(ProductFilterHeader productFilterHeader) {
        String filter;
        int checkCount = 0;

        for (ProductFilterItem productFilterItem :
                productFilterHeader.getProductFilterItemList()) {
            if (productFilterItem.isSelected()) {
                checkCount++;
            }
        }


        if (checkCount <= 0) {
            filter = productFilterHeader.getName();
        } else {
            filter = productFilterHeader.getName() + " (" + checkCount + ")";
        }

        return filter;
    }
}