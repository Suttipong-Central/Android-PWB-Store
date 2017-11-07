package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus;
import cenergy.central.com.pwb_store.manager.bus.event.CompareMenuBus;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

/**
 * Created by napabhat on 9/19/2017 AD.
 */

public class DrawerCompareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = DrawerCompareViewHolder.class.getSimpleName();

    @BindView(R.id.icon)
    ImageView mIcon;

    @BindView(R.id.title)
    PowerBuyTextView mTitle;

    @BindView(R.id.layout_compare)
    LinearLayout mLinearLayout;

    public DrawerCompareViewHolder(View itemView) {
        super(itemView);
    }

    public void setViewHolder() {
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "View Holder");
        EventBus.getDefault().post(new CompareMenuBus(v));
    }
}
