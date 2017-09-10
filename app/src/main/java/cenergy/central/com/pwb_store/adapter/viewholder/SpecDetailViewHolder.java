package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.model.SpecItem;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 8/4/2017 AD.
 */

public class SpecDetailViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_header)
    PowerBuyTextView header;

    @BindView(R.id.text_detail)
    PowerBuyTextView detail;

    public SpecDetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(SpecItem specItem){
        header.setText(specItem.getHeader());
        detail.setText(specItem.getDetail());
    }
}
