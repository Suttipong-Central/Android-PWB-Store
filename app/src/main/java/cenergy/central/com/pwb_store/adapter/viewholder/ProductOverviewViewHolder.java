package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductOverviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

//    @BindView(R.id.txt_available)
//    PowerBuyTextView mAvailable;
//
//    @BindView(R.id.txt_compare)
//    PowerBuyTextView mCompare;
//
//    @BindView(R.id.txt_view_detail)
//    PowerBuyTextView mViewDetail;

    @BindView(R.id.layout_overview)
    LinearLayout overView;

    public ProductOverviewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void setViewHolder(){

    }

    @Override
    public void onClick(View v) {

    }
}
