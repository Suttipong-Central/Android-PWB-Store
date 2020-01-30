package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 3/15/2017 AD.
 */

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

    private PowerBuyTextView mTextView;

    public SearchResultViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.txt_result);
    }

    public void setViewHolder(){

    }
}
