package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.CompareDetail;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareHeaderDetailViewHolder extends RecyclerView.ViewHolder {

    PowerBuyTextView header;

    public CompareHeaderDetailViewHolder(View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.textView);
    }

    public void setViewHolder(CompareDetail compareDetail){
        header.setText(compareDetail.getName());
    }
}
