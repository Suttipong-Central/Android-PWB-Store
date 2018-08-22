package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

public class FilterByBrandViewHolder extends RecyclerView.ViewHolder {

    PowerBuyTextView txtHeader;

    public FilterByBrandViewHolder(View itemView) {
        super(itemView);
        txtHeader = itemView.findViewById(R.id.txt_header_filter);
    }

    public void setViewHolder(String brand) {
        txtHeader.setText(brand);
    }
}
