package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.layout_header_home)
    LinearLayout mLinearLayout;

    public DrawerHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public void setViewHolder() {
        ButterKnife.bind(this, itemView);
        mLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new HomeBus(true));
    }
}
