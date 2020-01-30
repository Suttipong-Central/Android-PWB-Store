package cenergy.central.com.pwb_store.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeaderViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ProductFilterHeaderView";

    private PowerBuyTextView mTxtHeader;
    private ImageView mImgFilterHeader;


    public ProductFilterHeaderViewHolder(View itemView) {
        super(itemView);
        mTxtHeader = itemView.findViewById(R.id.txt_header_filter);
        mImgFilterHeader = itemView.findViewById(R.id.img_filter_header);
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