package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

public class FilterByBrandViewHolder extends RecyclerView.ViewHolder {

    private PowerBuyTextView txtHeader;

    public FilterByBrandViewHolder(View itemView) {
        super(itemView);
        txtHeader = itemView.findViewById(R.id.txt_header_filter);
    }

    public void setViewHolder(Brand brand) {
        txtHeader.setText(brand.getName());
    }
}
