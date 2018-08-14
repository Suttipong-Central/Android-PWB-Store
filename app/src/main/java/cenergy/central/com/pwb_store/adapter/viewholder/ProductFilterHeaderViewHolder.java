package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterSubHeaderBus;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
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
    }

    public void setViewHolder(ProductFilterSubHeader productFilterSubHeader) {
        String filter = countFilter(productFilterSubHeader);
        mTxtHeader.setText(filter);
        itemView.setTag(productFilterSubHeader);
        itemView.setOnClickListener(this);
        mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
        syncIndicator(productFilterSubHeader);
    }

    public void bindItem(final ProductFilterItem productFilterItem) {
        mTxtHeader.setText(productFilterItem.getFilterName());
        mImgFilterHeader.setVisibility(View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ProductFilterItemBus(productFilterItem, getAdapterPosition()));
            }
        });
    }

    private void syncIndicator(ProductFilterSubHeader productFilterSubHeader) {
        if (!productFilterSubHeader.isExpanded()) {
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
        } else {
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
        }
    }

    @Override
    public void onClick(View view) {
        ProductFilterSubHeader productFilterSubHeader = (ProductFilterSubHeader) itemView.getTag();

        if (!productFilterSubHeader.isExpanded()) {
            productFilterSubHeader.setExpanded(true);
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
            EventBus.getDefault().post(new ProductFilterSubHeaderBus(productFilterSubHeader, getAdapterPosition()));
        } else {
            productFilterSubHeader.setExpanded(false);
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
            EventBus.getDefault().post(new ProductFilterSubHeaderBus(productFilterSubHeader, getAdapterPosition()));
        }
    }

    private String countFilter(ProductFilterSubHeader productFilterSubHeader) {
        String filter;
        int checkCount = 0;

        for (ProductFilterItem productFilterItem :
                productFilterSubHeader.getProductFilterItemList()) {
            if (productFilterItem.isSelected()) {
                checkCount++;
            }
        }


        if (checkCount <= 0) {
            filter = productFilterSubHeader.getName();
        } else {
            filter = productFilterSubHeader.getName() + " (" + checkCount + ")";
        }

        return filter;
    }
}