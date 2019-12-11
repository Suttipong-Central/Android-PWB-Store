package cenergy.central.com.pwb_store.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.SearchHistoryClearBus;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchHistoryClearViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.img_clear)
    ImageView mClear;

    @BindView(R.id.img_barcode)
    ImageView mBarCode;

    public SearchHistoryClearViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mClear.setOnClickListener(this);
        mBarCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mClear){
            EventBus.getDefault().post(new SearchHistoryClearBus(true));
        }else if (v == mBarCode){

        }

    }
}
