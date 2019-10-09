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
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeaderViewHolder extends RecyclerView.ViewHolder {
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

    public void bindItem(Category category, Category selectedFilter) {
        mTxtHeader.setText(category.getDepartmentName());
        mImgFilterHeader.setVisibility(View.GONE);
        if (selectedFilter != null) {
            mTxtHeader.setTextAppearance(itemView.getContext(), category.getId().equals(selectedFilter.getId()) ? R.style.textSubCheck : R.style.textSub);
        }
        itemView.setOnClickListener(v -> EventBus.getDefault().post(new ProductFilterItemBus(category, getAdapterPosition())));
    }
}