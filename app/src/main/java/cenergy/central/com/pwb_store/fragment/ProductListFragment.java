package cenergy.central.com.pwb_store.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.BuildConfig;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ProductListAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.helpers.DialogHelper;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.api.ProductListAPI;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.FilterItem;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.model.SortingList;
import cenergy.central.com.pwb_store.model.body.FilterGroups;
import cenergy.central.com.pwb_store.model.body.SortOrder;
import cenergy.central.com.pwb_store.model.response.ProductResponse;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;

import static java.lang.Math.ceil;

public class ProductListFragment extends Fragment implements ObservableScrollViewCallbacks, View.OnClickListener, OnBrandFilterClickListener {
    private static final String TAG = ProductListFragment.class.getSimpleName();
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SEARCH = "ARG_SEARCH";
    private static final String ARG_DEPARTMENT_ID = "ARG_DEPARTMENT_ID";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
    private static final String ARG_PRODUCT_FILTER = "ARG_PRODUCT_FILTER";
//    private static final String ARG_PRODUCT_FILTER_TEMP = "ARG_PRODUCT_FILTER_TEMP";
    private static final String ARG_SORT_NAME = "ARG_SORT_NAME";
    private static final String ARG_SORT_TYPE = "ARG_SORT_TYPE";
    private static final String ARG_IS_DONE = "ARG_IS_DONE";
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
//    private static final String ARG_PRODUCT_FILTER_SUB_HEADER = "ARG_PRODUCT_FILTER_SUB_HEADER";
    private static final String ARG_KEY_WORD = "ARG_KEY_WORD";
    private static final String ARG_IS_SORTING = "ARG_IS_SORTING";

    private static final String PRODUCT_2H_FIELD = "expr-p";
    private static final String PRODUCT_2H_VALUE = "(stock.salable=1 OR (stock.ispu_salable=1 AND shipping_methods='storepickup_ispu'))";

    //View Members
    ObservableRecyclerView mRecyclerView;
    private PowerBuyTextView productCount;
    private LinearLayout layoutProgress;
    private LinearLayout mProductLayout;

    //Data Member
    private ProductListAdapter mProductListAdapter;
    private GridLayoutManager mLayoutManger;
    private ArrayList<Category> categoriesLv3;
    private List<FilterItem> brands = new ArrayList<>();
    private SortingList mSortingList;
    private String title;
    private PowerBuyPopupWindow mPowerBuyPopupWindow;
    private ProgressDialog mProgressDialog;
    private boolean isDoneFilter;
    private boolean isSearch;
    private String categoryId;
    private String brandName;

    //Sort
    private String sortName = "";
    private String sortType = "";
    private Category categoryLv2;

    //Pagination
    private static final int PER_PAGE = 20;

    // Page
    private boolean isLoadingMore;
    private boolean isSorting = false;
    private int mPreviousTotal;
    private int currentPage;
    private int totalPage;
    private int totalItem;
    private Context mContext;
    private String keyWord;
    private ProductResponse productResponse = null;

    final PowerBuyPopupWindow.OnDismissListener ON_POPUP_DISMISS_LISTENER = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            isDoneFilter = false;
        }
    };

    final RecyclerView.OnScrollListener SCROLL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = mLayoutManger.getItemCount();
            int visibleItemCount = mLayoutManger.getChildCount();
            int firstVisibleItem = mLayoutManger.findFirstVisibleItemPosition();

            if (isLoadingMore && totalItemCount > mPreviousTotal) {
                isLoadingMore = false;
                mPreviousTotal = totalItemCount;
            }
            int visibleThreshold = 10;
            if (!isLoadingMore
                    && (totalItemCount) <= (firstVisibleItem + visibleItemCount + visibleThreshold)
                    && isStillHavePages()) {

                layoutProgress.setVisibility(View.VISIBLE);
                retrieveProductList();
                isLoadingMore = true;
            }
        }
    };

    @Subscribe
    public void onEvent(ProductFilterItemBus productFilterItemBus) {
        showProgressDialog();
        isDoneFilter = true;
        isSorting = true;
        brandName = ""; // clear filter brand name
        currentPage = 1; // clear current page
        Category categoryLv3 = productFilterItemBus.getProductFilterItem();
        int clickPosition = productFilterItemBus.getPosition();
        if (categoryLv3 != null && clickPosition != 0) {
            title = categoryLv3.getDepartmentName();
            categoryId = categoryLv3.getId();
            // Don't update because categoryLv3 is Header
//            mPowerBuyPopupWindow.updateSingleProductFilterItem(categoryLv3);
        } else  {
            // clear filter
            title = categoryLv2.getDepartmentName();
            categoryId = categoryLv2.getId();
        }
        resetPage();
        retrieveProductList();
        if(mPowerBuyPopupWindow.isShowing()){
            mPowerBuyPopupWindow.dismiss();
        }
    }

    @Subscribe
    public void onEvent(SortingHeaderBus sortingHeaderBus) {
        mPowerBuyPopupWindow.setSortingItem(sortingHeaderBus.getSortingHeader());
    }

    @Subscribe
    public void onEvent(SortingItemBus sortingItemBus) {
        showProgressDialog();
        SortingItem sortingItem = sortingItemBus.getSortingItem();
        isDoneFilter = true;
        isSorting = true;
        resetPage();
        sortName = sortingItem.getSlug();
        sortType = sortingItem.getValue();
        retrieveProductList();
        mPowerBuyPopupWindow.updateSingleSortingItem(sortingItem);
        if(mPowerBuyPopupWindow.isShowing()){
            mPowerBuyPopupWindow.dismiss();
        }
    }

    @SuppressWarnings("unused")
    public static ProductListFragment newInstance(String title, boolean search, String departmentId,
                                                  String storeId, Category category, String keyWord) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putBoolean(ARG_SEARCH, search);
        args.putString(ARG_DEPARTMENT_ID, departmentId);
        args.putString(ARG_STORE_ID, storeId);
        args.putParcelable(ARG_CATEGORY, category);
        args.putString(ARG_KEY_WORD, keyWord);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListFragment newInstance(Category category, boolean search, String keyWord) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, category.getDepartmentName());
        args.putBoolean(ARG_SEARCH, search);
        args.putString(ARG_DEPARTMENT_ID, category.getId());
        args.putString(ARG_KEY_WORD, keyWord);
        args.putParcelable(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        initInstances(rootView, savedInstanceState);
        resetPage();
        return rootView;
    }

    private void init() {
        // Init Fragment level's variable(s) here
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            isSearch = getArguments().getBoolean(ARG_SEARCH);
            categoryId = getArguments().getString(ARG_DEPARTMENT_ID);
            categoryLv2 = getArguments().getParcelable(ARG_CATEGORY);
            keyWord = getArguments().getString(ARG_KEY_WORD);

            // no search
            if (!isSearch) {
                // setup product filter list
                loadCategoryLv3(categoryLv2);
            }
        }
        resetPage();

//        mProductFilterList = new ProductFilterList(productFilterHeaders);

        // sorting
        List<SortingItem> sortingItems = new ArrayList<>();
        sortingItems.add(new SortingItem(1, getString(R.string.low_to_high), "price", "ASC", "1", false));
        sortingItems.add(new SortingItem(2, getString(R.string.high_to_low), "price", "DESC", "2", false));
        sortingItems.add(new SortingItem(3, getString(R.string.a_to_z), "brand", "ASC", "3", false));
        sortingItems.add(new SortingItem(4, getString(R.string.z_to_a), "brand", "DESC", "4", false));
//        sortingItems.add(new SortingItem(5, "Discount : Low to High", "ASC", "ASC", "5", false));
//        sortingItems.add(new SortingItem(6, "Discount : High to Low", "DESC", "DESC", "6", false));

        List<SortingHeader> sortingHeaders = new ArrayList<>();
        sortingHeaders.add(new SortingHeader("0", "Sorting", "sorting", "single", sortingItems));

        mSortingList = new SortingList(sortingHeaders);
        for (SortingHeader sortingHeader : sortingHeaders) {
            mSortingList.updateSortOption(sortingHeader, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_title:
                EventBus.getDefault().post(new CategoryTwoBus());
                break;
            case R.id.layout_product:
                if (categoriesLv3 == null || categoriesLv3.isEmpty()) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
                    mPowerBuyPopupWindow.setRecyclerViewFilter(categoriesLv3);
                    mPowerBuyPopupWindow.showAsDropDown(v);
                }
                break;
            case R.id.layout_sort:
                // Create productResponse for check because we mock up sort items
                if (mSortingList == null || productResponse == null || productResponse.getProducts().isEmpty()) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
                    mPowerBuyPopupWindow.setRecyclerViewSorting(mSortingList);
                    mPowerBuyPopupWindow.showAsDropDown(v);
                }
                break;
            case R.id.layout_brand:
                if (brands.isEmpty()) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
                    mPowerBuyPopupWindow.setRecyclerViewFilterByBrand(brands, this);
                    mPowerBuyPopupWindow.showAsDropDown(v);
                }
                break;
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
//        ButterKnife.bind(this, rootView);
        mProductListAdapter = new ProductListAdapter(getContext());
        mProductListAdapter.showLoading();

        // setup widget view
        PowerBuyTextView productTitle = rootView.findViewById(R.id.txt_title_product);
        ConstraintLayout layoutFilter = rootView.findViewById(R.id.layout_filter);
        productCount = rootView.findViewById(R.id.txt_product_count);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_list);
        layoutProgress = rootView.findViewById(R.id.layout_progress);

        productTitle.setText(title);
        productCount.setText(title);

        // setup onClick
        LinearLayout titleLayout = rootView.findViewById(R.id.layout_title);
        mProductLayout = rootView.findViewById(R.id.layout_product);
        LinearLayout sortLayout = rootView.findViewById(R.id.layout_sort);
        LinearLayout brandLayout = rootView.findViewById(R.id.layout_brand);

        titleLayout.setOnClickListener(this);
        mProductLayout.setOnClickListener(this);
        sortLayout.setOnClickListener(this);
        brandLayout.setOnClickListener(this);

        if (isSearch) {
            layoutFilter.setVisibility(View.GONE);
        }

        if (categoriesLv3 == null) {
            mProductLayout.setVisibility(View.GONE);
        } else {
            mProductLayout.setVisibility(View.VISIBLE);
        }

        popUpShow();

        mLayoutManger = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        mLayoutManger.setSpanSizeLookup(mProductListAdapter.getSpanSize());
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setAdapter(mProductListAdapter);

        if (savedInstanceState == null) {
            showProgressDialog();
            retrieveProductList();
        }

        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.addOnScrollListener(SCROLL);
    }

    private void resetPage() {
        // mProductDao.getProductListList().clear();
        currentPage = 0;
        totalPage = 1;
        isLoadingMore = true;
        mPreviousTotal = 0;
        if (!isSorting) {
            sortName = "";
            sortType = "";
        }
    }

    private int getNextPage() {
        //return currentPage + PER_PAGE;
        return currentPage + 1;
    }

    private boolean isStillHavePages() {
        return currentPage < totalPage;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(getContext());
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        mContext = context;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putParcelable(ARG_CATEGORY, categoryLv2);
        outState.putParcelableArrayList(ARG_PRODUCT_FILTER, categoriesLv3);
//        outState.putParcelable(ARG_PRODUCT_FILTER_TEMP, mTempProductFilterList);
        outState.putString(ARG_DEPARTMENT_ID, categoryId);
        outState.putString(ARG_SORT_NAME, sortName);
        outState.putString(ARG_SORT_TYPE, sortType);
        outState.putBoolean(ARG_IS_DONE, isDoneFilter);
        outState.putString(ARG_TITLE, title);
        outState.putInt(ARG_PAGE, currentPage);
        outState.putString(ARG_KEY_WORD, keyWord);
        outState.putBoolean(ARG_SEARCH, isSearch);
        outState.putBoolean(ARG_IS_SORTING, isSorting);
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
        categoryLv2 = savedInstanceState.getParcelable(ARG_CATEGORY);
        categoriesLv3 = savedInstanceState.getParcelable(ARG_PRODUCT_FILTER);
//        mTempProductFilterList = savedInstanceState.getParcelable(ARG_PRODUCT_FILTER_TEMP);
        categoryId = savedInstanceState.getString(ARG_DEPARTMENT_ID);
        sortName = savedInstanceState.getString(ARG_SORT_NAME);
        sortType = savedInstanceState.getString(ARG_SORT_TYPE);
        isDoneFilter = savedInstanceState.getBoolean(ARG_IS_DONE);
        title = savedInstanceState.getString(ARG_TITLE);
        currentPage = savedInstanceState.getInt(ARG_PAGE);
        keyWord = savedInstanceState.getString(ARG_KEY_WORD);
        isSearch = savedInstanceState.getBoolean(ARG_SEARCH);
        isSorting = savedInstanceState.getBoolean(ARG_IS_SORTING);
    }

    private void popUpShow() {
        if(getContext() != null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            mPowerBuyPopupWindow = new PowerBuyPopupWindow(getActivity(), layoutInflater);
            mPowerBuyPopupWindow.setOnDismissListener(ON_POPUP_DISMISS_LISTENER);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextHeader(int total, String name) {
        productCount.setText(name + " " + mContext.getString(R.string.filter_count, String.valueOf(total)));
    }

    private int totalPageCal(int total) {
        int num;
        float x = ((float) total / PER_PAGE);
        Log.d(TAG, "Calculator : " + x);
        num = (int) ceil(x);
        return num;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void retrieveProductList() {
//        if (isSearch) {
//            getProductsFromSearch();
//        } else {
//            getProducts();
//        }

        getProducts();
    }

    private void getProducts(){
        if (getContext() != null){
            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            if (isSearch){
                filterGroupsList.add(FilterGroups.Companion.createFilterGroups("search_term", keyWord, "eq"));
            } else {
                filterGroupsList.add(FilterGroups.Companion.createFilterGroups("category_id", categoryId, "eq"));
            }
            if (BuildConfig.FLAVOR != "cds") {
                filterGroupsList.add(FilterGroups.Companion.createFilterGroups(PRODUCT_2H_FIELD, PRODUCT_2H_VALUE, "eq"));
            }
            filterGroupsList.add(FilterGroups.Companion.createFilterGroups("status", "1", "eq"));
            filterGroupsList.add(FilterGroups.Companion.createFilterGroups("visibility", "4", "eq"));
            filterGroupsList.add(FilterGroups.Companion.createFilterGroups("price", "0", "gt"));
            if (brandName != null && !brandName.isEmpty()) {
                filterGroupsList.add(FilterGroups.Companion.createFilterGroups("brand", brandName, "eq"));
            }

            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            if (!sortName.isEmpty() && !sortType.isEmpty()) {
                SortOrder sortOrder = SortOrder.Companion.createSortOrder(sortName, sortType);
                sortOrders.add(sortOrder);
            }

            ProductListAPI.retrieveProducts(getContext(), PER_PAGE,
                    getNextPage(), filterGroupsList, sortOrders, new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> updateProductList(response));
                            }
                        }

                        @Override
                        public void failure(@NotNull final APIError error) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    layoutProgress.setVisibility(View.GONE);
                                    mProgressDialog.dismiss();
                                    // show error dialog
                                    if (getContext() != null) {
                                        new DialogHelper(getContext()).showErrorDialog(error);
                                    }
                                });
                            }
                        }
                    });
        }
    }

//    private void getProductsFromSearch(){
//        if (getContext() != null) {
//            SearchProductsAPI.retrieveProductsFromSearch(getContext(), PER_PAGE, getNextPage(), PRODUCT_2H_FIELD,
//                    PRODUCT_2H_VALUE, keyWord, sortName, sortType, new ApiResponseCallback<ProductResponse>() {
//                        @Override
//                        public void success(@org.jetbrains.annotations.Nullable ProductResponse response) {
//                            if (getActivity() != null) {
//                                getActivity().runOnUiThread(() -> updateProductList(response));
//                            }
//                        }
//
//                        @Override
//                        public void failure(@NotNull APIError error) {
//                            if (getActivity() != null) {
//                                getActivity().runOnUiThread(() -> {
//                                    layoutProgress.setVisibility(View.GONE);
//                                    mProgressDialog.dismiss();
//                                    // show error dialog
//                                    if (getContext() != null) {
//                                        new DialogHelper(getContext()).showErrorDialog(error);
//                                    }
//                                });
//                            }
//                        }
//                    });
//        }
//    }

    private void updateProductList(ProductResponse response) {
        productResponse = response;
        if (response != null) {
            if (!response.getFilters().isEmpty()){
                brands = response.getFilters().get(0).getItems();
                for (FilterItem filterItem : brands) {
                    if (brandName != null && brandName.equals(filterItem.getValue())) {
                        filterItem.setSelected(true);
                    }
                }
            }
            if(!response.getProducts().isEmpty()){
                totalItem = response.getTotalCount();
                totalPage = totalPageCal(totalItem);
                currentPage = getNextPage();
                response.setCurrentPage(currentPage);
                mProductListAdapter.setProduct(response);
            } else {
                mProductListAdapter.setError();
            }
            setTextHeader(totalItem, title);
        } else {
            if (mProductListAdapter.getItemCount() == 0) {
                mProductListAdapter.setError();
            }
            setTextHeader(totalItem, title);
        }

        layoutProgress.setVisibility(View.GONE);
        mProgressDialog.dismiss();
    }

    // region {@link OnBrandFilterClickListener}
    @Override
    public void onClickedItem(FilterItem filterItem) {
        isDoneFilter = true;
        resetPage();

        if (filterItem != null) {
            brandName = filterItem.getValue(); // brand name
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                showProgressDialog();
            }
        if (mPowerBuyPopupWindow.isShowing()) {
            mPowerBuyPopupWindow.dismiss();
        }
            retrieveProductList();
            mPowerBuyPopupWindow.updateSingleBrandFilterItem(filterItem);
        } else  {
           clearBrandFilter();
        }
    }

    private void clearBrandFilter() {
        brandName = ""; // clear brand
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            showProgressDialog();
        }
        retrieveProductList();
        mPowerBuyPopupWindow.updateSingleBrandFilterItem(null);
    }
    // endregion

    private void loadCategoryLv3(Category categoryLv2) {
        if (getContext() == null) return;
        HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategory(categoryLv2.getId(),
                true, new ArrayList<>(),new ApiResponseCallback<List<Category>>() {
                    @Override
                    public void success(final List<Category> categories) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                categoriesLv3 = new ArrayList<>(); // clear category lv3 list
                                categoriesLv3.addAll(categories);
                                mProductLayout.setVisibility(View.VISIBLE); // show product layout
                            });
                        }
                    }

                    @Override
                    public void failure(@NotNull APIError error) {
                        if(getActivity() != null){
                            getActivity().runOnUiThread(() -> {
                                Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                                categoriesLv3 = null;
                                mProductLayout.setVisibility(View.GONE);
                            });
                        }
                    }
                });
    }
}
