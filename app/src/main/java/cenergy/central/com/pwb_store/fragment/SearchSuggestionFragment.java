package cenergy.central.com.pwb_store.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.BarcodeScanActivity;
import cenergy.central.com.pwb_store.activity.BaseActivity;
import cenergy.central.com.pwb_store.activity.MainActivity;
import cenergy.central.com.pwb_store.activity.ProductListActivity;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchQueryBus;
import cenergy.central.com.pwb_store.view.ClearAbleEditText;

public class SearchSuggestionFragment extends Fragment {
    public static final String TAG = SearchSuggestionFragment.class.getSimpleName();

    private ClearAbleEditText mTextSearch;
    private ImageView mBarCode;

    public SearchSuggestionFragment() {
        super();
    }

    @Subscribe
    public void onEvent(SearchQueryBus searchQueryBus){
        Intent intent = new Intent(getContext(), ProductListActivity.class);
        intent.putExtra(ProductListActivity.ARG_PRODUCT_ID, "");
        intent.putExtra(ProductListActivity.ARG_SEARCH, true);
        startActivityForResult(intent, BaseActivity.REQUEST_UPDATE_LANGUAGE);
    }

    @Subscribe
    public void onEvent(BarcodeBus barcodeBus){
        if (barcodeBus.isBarcode() == true){
            IntentIntegrator integrator = new IntentIntegrator(getActivity()).setCaptureActivity(BarcodeScanActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.initiateScan();
        }
    }

    @SuppressWarnings("unused")
    public static SearchSuggestionFragment newInstance() {
        SearchSuggestionFragment fragment = new SearchSuggestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_suggestion, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        mTextSearch = rootView.findViewById(R.id.text_search);
        mBarCode = rootView.findViewById(R.id.image_view_barcode);
        hideSoftKeyboard(mTextSearch);
        mTextSearch.setOnEditorActionListener(new SearchOnEditorActionListener());
        if (BuildConfig.FLAVOR != "pwbOmniTv"){
            mBarCode.setOnClickListener(view -> onBarcodeClick(mBarCode));
        } else {
            mBarCode.setOnClickListener(null);
            mBarCode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    private void hideSoftKeyboard(View view) {
    if (view != null && getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void onBarcodeClick(ImageView imageView) {
        EventBus.getDefault().post(new BarcodeBus(true));
    }

    private class SearchOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                performSearch();
            }

            return true;
        }
    }

    private void performSearch(){
        if (mTextSearch.getText() != null){
            Intent intent = new Intent(getContext(), ProductListActivity.class);
            intent.putExtra(ProductListActivity.ARG_KEY_WORD, mTextSearch.getText().toString());
            intent.putExtra(ProductListActivity.ARG_SEARCH, true);
            startActivityForResult(intent, MainActivity.REQUEST_UPDATE_LANGUAGE);
        }
    }
}
