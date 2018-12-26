package cenergy.central.com.pwb_store.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.BarcodeScanActivity;
import cenergy.central.com.pwb_store.activity.BaseActivity;
import cenergy.central.com.pwb_store.activity.MainActivity;
import cenergy.central.com.pwb_store.activity.ProductListActivity;
import cenergy.central.com.pwb_store.adapter.SearchListSuggestionAdapter;
import cenergy.central.com.pwb_store.manager.bus.event.BackSearchBus;
import cenergy.central.com.pwb_store.manager.bus.event.BarcodeBus;
import cenergy.central.com.pwb_store.manager.bus.event.SearchQueryBus;
import cenergy.central.com.pwb_store.model.ProductList;
import cenergy.central.com.pwb_store.model.SearchSuggestion;
import cenergy.central.com.pwb_store.model.SearchSuggestionDao;
import cenergy.central.com.pwb_store.model.SearchSuggestionItem;
import cenergy.central.com.pwb_store.model.SearchSuggestionProduct;
import cenergy.central.com.pwb_store.view.ClearAbleEditText;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class SearchSuggestionFragment extends Fragment {
    public static final String TAG = SearchSuggestionFragment.class.getSimpleName();
    private static final String ARG_QUERY = "ARG_QUERY";
    private static final String ARG_SEARCH_SUGGESTION_DAO = "ARG_SEARCH_SUGGESTION_DAO";

    //View Members
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.text_search)
    ClearAbleEditText mTextSearch;

    @BindView(R.id.image_view_barcode)
    ImageView mBarCode;

    //Data Members
    private String mQuery = "";
    private SearchSuggestionDao mSearchSuggestionDao;
    private SearchSuggestion mSearchSuggestion;
    private GridLayoutManager mLayoutManager;
    //private SearchSuggestionAdapter mAdapter;
    private SearchListSuggestionAdapter mAdapter;
    private Handler mHandler;
    private Runnable mSearchSuggestionRunnable;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_suggestion, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        List<SearchSuggestionItem> searchSuggestionItemList = new ArrayList<>();
        searchSuggestionItemList.add(new SearchSuggestionItem(1, "1", mQuery, "boutique", "in Central Department Store"));
        searchSuggestionItemList.add(new SearchSuggestionItem(2, "2", mQuery, "boutique", "in Robinson"));
        searchSuggestionItemList.add(new SearchSuggestionItem(3, "3", mQuery, "silimar-word", ""));
        searchSuggestionItemList.add(new SearchSuggestionItem(4, "4", mQuery.toUpperCase(), "silimar-word", ""));
        searchSuggestionItemList.add(new SearchSuggestionItem(5, "5", mQuery, "shop", "in Beauty & Health"));
        mSearchSuggestion = new SearchSuggestion(5, searchSuggestionItemList);

        List<ProductList> productList = new ArrayList<>();
        productList.add(new ProductList("1", "https://ekit.co.uk/GalleryEntries/eCommerce_solutions_and_services/MedRes_Product-presentation-2.jpg?q=27012012153123","DeFry", "เสื้อ Shirt รุ่น xxxxxxxx", 350, 240));
        productList.add(new ProductList("2", "http://s3-us-west-2.amazonaws.com/hypebeast-wordpress/image/2008/10/lupe-fiasco-product-red-converse-all-star-01.jpg", "RockPort", "รองเท้าบู๊ท รุ่น xxxxx",  600, 350));
        productList.add(new ProductList("3", "http://blog.tamar.com/wp-content/uploads/2015/03/simple.b-cssdisabled-png.hdf3b6763ae7262168fb0c41e99bafbbf.png", "Laura Mercier", "Face illumanator Powder", 6000, 2500));
        productList.add(new ProductList("4", "http://cdn.macrumors.com/article-new/2014/11/ipadprodesign-1-800x376.jpg", "Lancome", "Juicy Shaker Lip Oil #341", 18000, 12500));
        productList.add(new ProductList("5", "http://smart.glyphs.co/medialoot/Minimal_Apple_Product_Templates_Preview1.png?w=1600&h=1036&file%5B%5D=Minimal_Apple_Product_Templates_Preview2.png&pos=1", "Name", "Brand", 90000, 45000));
        SearchSuggestionProduct searchSuggestionProduct = new SearchSuggestionProduct(5, productList);

        mSearchSuggestionDao = new SearchSuggestionDao(mQuery, mSearchSuggestion, null, searchSuggestionProduct);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
        hideSoftKeyboard(mTextSearch);
//        mAdapter = new SearchListSuggestionAdapter(getContext());
//        //mAdapter.showLoading();
//        mLayoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
//
//        //if (!TextUtils.isEmpty(mQuery) && mSearchSuggestionDao != null) {
//        //    mAdapter.setSearchSuggestion(mSearchSuggestionDao);
//        //} else if (!TextUtils.isEmpty(mQuery)) {
//            //HttpManager.getInstance().getSearchService().getSearchSuggestion(mQuery).enqueue(CALLBACK_SEARCH_SUGGESTION);
//        //} else {
//        //    mAdapter.setSearchHistory(new TheCentralSQLiteHelper(getContext()).getAllSearchHistory());
//        //}
//
//        mAdapter.setSearch(mSearchSuggestionDao);
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);
//        mHandler = new Handler();
//        mSearchSuggestionRunnable = new Runnable() {
//            @Override
//            public void run() {
//                //HttpManager.getInstance().getSearchService().getSearchSuggestion(mQuery).enqueue(CALLBACK_SEARCH_SUGGESTION);
//            }
//        };

        //addTextListener();
        mTextSearch.setOnEditorActionListener(new SearchOnEditorActionListener());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    private void hideSoftKeyboard(View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.image_view_barcode)
    protected void onBarcodeClick(ImageView imageView) {
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
        Intent intent = new Intent(getContext(), ProductListActivity.class);
        intent.putExtra(ProductListActivity.ARG_KEY_WORD, mTextSearch.getText().toString());
        intent.putExtra(ProductListActivity.ARG_SEARCH, true);
        startActivityForResult(intent, MainActivity.REQUEST_UPDATE_LANGUAGE);
    }
}
