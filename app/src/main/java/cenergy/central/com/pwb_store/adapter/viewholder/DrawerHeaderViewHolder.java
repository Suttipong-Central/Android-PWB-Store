package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.HomeBus;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView labelTextView = itemView.findViewById(R.id.txt_view_sign_in);
    public DrawerHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public void setViewHolder() {
        labelTextView.setText(itemView.getContext().getString(R.string.home));
        itemView.findViewById(R.id.layout_header_home).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new HomeBus(true));
    }
}
