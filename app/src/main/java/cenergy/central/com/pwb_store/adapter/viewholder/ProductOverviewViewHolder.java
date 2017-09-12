package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.OverviewBus;
import cenergy.central.com.pwb_store.model.Event;
import cenergy.central.com.pwb_store.model.ReviewDetailText;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductOverviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.layout_overview)
    LinearLayout overView;

    public ProductOverviewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void setViewHolder(ReviewDetailText reviewDetailText){
        itemView.setTag(reviewDetailText);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ReviewDetailText reviewDetailText = (ReviewDetailText) itemView.getTag();
        EventBus.getDefault().post(new OverviewBus(v, reviewDetailText));
    }
}
