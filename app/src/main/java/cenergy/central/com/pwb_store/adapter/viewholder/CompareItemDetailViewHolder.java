package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.CompareDetailItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareItemDetailViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textView)
    PowerBuyTextView detailItem;

    public CompareItemDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(CompareDetailItem compareDetailItem){
        detailItem.setText(compareDetailItem.getType());
    }
}
