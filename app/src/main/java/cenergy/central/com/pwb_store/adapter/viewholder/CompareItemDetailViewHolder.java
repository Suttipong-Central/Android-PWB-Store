package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareItemDetailViewHolder extends RecyclerView.ViewHolder {

    private PowerBuyTextView detailItem;

    public CompareItemDetailViewHolder(View itemView) {
        super(itemView);
        detailItem = itemView.findViewById(R.id.textView);
    }

    public void setViewHolder(CompareDetailItem compareDetailItem){
        detailItem.setText(compareDetailItem.getType());
    }
}
