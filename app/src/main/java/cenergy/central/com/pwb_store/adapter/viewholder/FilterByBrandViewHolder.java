package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.model.FilterItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

public class FilterByBrandViewHolder extends RecyclerView.ViewHolder {

    private PowerBuyTextView txtHeader;
    private OnBrandFilterClickListener listener;

    public FilterByBrandViewHolder(View itemView, OnBrandFilterClickListener listener) {
        super(itemView);
        txtHeader = itemView.findViewById(R.id.txt_header_filter);
        this.listener = listener;
    }

    public void setViewHolder(FilterItem filterItem) {
        txtHeader.setText(filterItem.getValue());
        txtHeader.setTextAppearance(itemView.getContext(), filterItem.isSelected() ? R.style.textSubCheck : R.style.textSub);
        itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickedItem(filterItem);
            }
        });
    }
}
