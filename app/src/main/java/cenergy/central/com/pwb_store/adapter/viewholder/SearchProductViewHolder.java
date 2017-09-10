package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchEventBus;
import cenergy.central.com.pwb_store.view.PowerBuyEditText;

/**
 * Created by napabhat on 7/11/2017 AD.
 */

public class SearchProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener{
    private static final String TAG = SearchProductViewHolder.class.getSimpleName();

    @BindView(R.id.edit_text_search)
    PowerBuyEditText mSearchView;

    @BindView(R.id.image_view_barcode)
    ImageView mBarCode;

    public SearchProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mBarCode.setOnClickListener(this);
        mSearchView.setOnFocusChangeListener(this);
    }

    public void setViewHolder(){

    }

    @Override
    public void onClick(View v) {
        if (v == mBarCode){
            EventBus.getDefault().post(new BarcodeBus(true));
            Log.d(TAG , " Click");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mSearchView){
            EventBus.getDefault().post(new SearchEventBus(v, true));
            Log.d(TAG, "Search");
        }
    }
}
