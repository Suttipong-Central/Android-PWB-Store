package cenergy.central.com.pwb_store.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
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

    private boolean isSearch;

    public SearchProductViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mBarCode.setOnClickListener(this);
        //mSearchView.setOnFocusChangeListener(this);
        mSearchView.setOnEditorActionListener(new SearchOnEditorActionListener());
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
            //EventBus.getDefault().post(new SearchEventBus(v, true));
            Log.d(TAG, "Search");
        }
    }

//    @OnTextChanged(R.id.edit_text_search)
//    public void textMoneyChanged (CharSequence text) {
//            if (text.length() > 0){
//                EventBus.getDefault().post(new SearchEventBus(true, mSearchView.getText().toString()));
//                Log.d(TAG, "Search");
//            }
//    }

    @OnTextChanged(value = R.id.edit_text_search,
            callback = OnTextChanged.Callback.TEXT_CHANGED)
    protected void onSearchChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
           // EventBus.getDefault().post(new SearchEventBus(true, mSearchView.getText().toString()));
        }
    }

    private class SearchOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                //performSearch();

                EventBus.getDefault().post(new SearchEventBus(true, mSearchView.getText().toString()));
            }

            return true;
        }
    }
}
