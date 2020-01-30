package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class SortingHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private PowerBuyTextView mTxtHeader;
    private ImageView mImgFilterHeader;
    private ImageView mImgBack;


    public SortingHeaderViewHolder(View itemView) {
        super(itemView);
        mTxtHeader = itemView.findViewById(R.id.txt_header_filter);
        mImgFilterHeader = itemView.findViewById(R.id.img_filter_header);
        mImgBack = itemView.findViewById(R.id.img_fill_back);
        mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
    }

    public void setViewHolder(SortingHeader sortingHeader) {
        itemView.setTag(sortingHeader);
        itemView.setOnClickListener(this);
        syncIndicator(sortingHeader);
    }

    private void syncIndicator(SortingHeader sortingHeader) {
        if (!sortingHeader.isExpanded()) {
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
        } else {
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
        }
    }

    @Override
    public void onClick(View view) {
        SortingHeader sortingHeader = (SortingHeader) itemView.getTag();

        if (!sortingHeader.isExpanded()) {
            sortingHeader.setExpanded(true);
            mImgFilterHeader.setBackgroundResource(R.drawable.background_popup_window);
            mImgBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left);
            EventBus.getDefault().post(new SortingHeaderBus(sortingHeader, getAdapterPosition()));
        } else {
            sortingHeader.setExpanded(false);
            mImgFilterHeader.setBackgroundResource(R.drawable.ic_keyboard_arrow_right);
            mImgBack.setBackgroundResource(R.drawable.background_popup_window);
            EventBus.getDefault().post(new SortingHeaderBus(sortingHeader, getAdapterPosition()));
        }
    }

}