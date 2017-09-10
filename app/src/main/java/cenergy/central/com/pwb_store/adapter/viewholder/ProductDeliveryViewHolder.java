package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.BookTimeBus;
import cenergy.central.com.pwb_store.model.Event;
import cenergy.central.com.pwb_store.view.CalendarViewCustom;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class ProductDeliveryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.layout_delivery)
    LinearLayout mDelivery;

    @BindView(R.id.custom_calendar)
    CalendarViewCustom mCalendarView;

    @BindView(R.id.card_view_add_time)
    CardView mCardView;

    public ProductDeliveryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setViewHolder(){
        mCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mCardView){
            EventBus.getDefault().post(new BookTimeBus(v));
        }
    }
}
