package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.SpecAddToCompareBus;

/**
 * Created by napabhat on 8/4/2017 AD.
 */

public class SpecAddCompareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.card_view_add_compare)
    CardView mCardView;

    public SpecAddCompareViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(){
        mCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new SpecAddToCompareBus(v, true));
    }
}
