package cenergy.central.com.pwb_store.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.ProductListAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.helpers.DialogHelper;
import cenergy.central.com.pwb_store.manager.ApiResponseCallback;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.bus.event.CategoryTwoBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterSubHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductDao;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterList;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.model.SortingList;
import cenergy.central.com.pwb_store.model.body.Filter;
import cenergy.central.com.pwb_store.model.body.FilterGroups;
import cenergy.central.com.pwb_store.model.body.SortOrder;
import cenergy.central.com.pwb_store.model.response.ProductResponse;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import static java.lang.Math.ceil;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListFragment extends Fragment implements ObservableScrollViewCallbacks, View.OnClickListener, OnBrandFilterClickListener {
    private static final String TAG = ProductListFragment.class.getSimpleName();
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SEARCH = "ARG_SEARCH";
    private static final String ARG_PRODUCT = "ARG_PRODUCT";
    private static final String ARG_DEPARTMENT_ID = "ARG_DEPARTMENT_ID";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
    private static final String ARG_PRODUCT_FILTER = "ARG_PRODUCT_FILTER";
//    private static final String ARG_PRODUCT_FILTER_TEMP = "ARG_PRODUCT_FILTER_TEMP";
    private static final String ARG_SORT_NAME = "ARG_SORT_NAME";
    private static final String ARG_SORT_TYPE = "ARG_SORT_TYPE";
    private static final String ARG_IS_DONE = "ARG_IS_DONE";
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_PRODUCT_FILTER_SUB_HEADER = "ARG_PRODUCT_FILTER_SUB_HEADER";
    private static final String ARG_KEY_WORD = "ARG_KEY_WORD";
    private static final String ARG_IS_SORTING = "ARG_IS_SORTING";

    //View Members
    ObservableRecyclerView mRecyclerView;
    private PowerBuyTextView productCount;
    private LinearLayout layoutProgress;

    //Data Member
    private ProductListAdapter mProductListAdapter;
    private GridLayoutManager mLayoutManger;
    private ProductDao mProductDao;
    private ProductFilterList mProductFilterList;
    //    private ProductFilterList mTempProductFilterList;
    private List<Brand> brands = new ArrayList<>();
    private SortingList mSortingList;
    private String title;
    private PowerBuyPopupWindow mPowerBuyPopupWindow;
    private ProgressDialog mProgressDialog;
    private boolean isDoneFilter;
    private boolean isSearch;
    private String categoryId;
    private Long brandId;
    private String sortName;

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
    private String sortType;

    final PowerBuyPopupWindow.OnDismissListener ON_POPUP_DISMISS_LISTENER = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
//            if (!isDoneFilter) {
//                Log.d(TAG, "DISMISS" + isDoneFilter);
//                mProductFilterList = mTempProductFilterList;
//                mSortingList = mTempSortingList;
//            }
            isDoneFilter = false;
        }
    };

    final RecyclerView.OnScrollListener SCROLL = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                if (isSearch) {
                    getProductsFromSearch(keyWord);
                } else {
                    if (brandId != null) {
                        retrieveProductList(categoryId, brandId);
                    } else {
//                    getProductList(categoryId);
                        retrieveProductList(categoryId);
                    }
                }

                isLoadingMore = true;
            }
        }
    };

//    final Callback<ProductDao> CALLBACK_PRODUCT = new Callback<ProductDao>() {
//        @Override
//        public void onResponse(Call<ProductDao> call, Response<ProductDao> response) {
//            if (response.isSuccessful()) {
//                //mProductDao = response.body();
//                ProductDao productDao = response.body();
//                //TODO Test Total Page.
//                if (currentPage == 0) {
//                    currentPage = getNextPage();
//                    if (productDao != null) {
//                        productDao.setCurrentPage(currentPage);
//                    }
//                } else {
//                    currentPage = getNextPage();
//                    if (productDao != null) {
//                        productDao.setCurrentPage(currentPage);
//                    }
//                }
//
//                totalItem = productDao.getTotalElement();
//                totalPage = totalPageCal(totalItem);
//                Log.d(TAG, " totalPage :" + totalPage);
//                if (productDao.getProductListList() != null) {
//                    currentPage = getNextPage();
//                    if (productDao != null) {
//                        productDao.setCurrentPage(currentPage);
//                    }
//                    mProductListAdapter.setProduct(productDao);
//                } else if (productDao.getProductListList() == null) {
//                    mProductListAdapter.setError();
//                } else {
//                    mProductListAdapter.setError();
//                }
//                setTextHeader(totalItem, title);
//                layoutProgress.setVisibility(View.GONE);
//                mProgressDialog.dismiss();
//            } else {
//                mProductListAdapter.setError();
//                setTextHeader(totalItem, title);
//                APIError error = APIErrorUtils.parseError(response);
//                Log.e(TAG, "onResponse: " + error.getErrorMessage());
////                showAlertDialog(error.getErrorMessage(), false);
//                mProgressDialog.dismiss();
//                layoutProgress.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ProductDao> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//            mProgressDialog.dismiss();
//            layoutProgress.setVisibility(View.GONE);
//            setTextHeader(totalItem, title);
//        }
//    };

    @Subscribe
    public void onEvent(ProductFilterSubHeaderBus productFilterSubHeaderBus) {
        showProgressDialog();
        isDoneFilter = true;
        isSorting = false;
        ProductFilterSubHeader productFilterSubHeader = productFilterSubHeaderBus.getProductFilterSubHeader();
        callFilter(productFilterSubHeader.getId(), sortName, sortType);
        title = productFilterSubHeader.getName();
        categoryId = productFilterSubHeader.getId();
        mPowerBuyPopupWindow.setFilterItem(productFilterSubHeaderBus.getProductFilterSubHeader());
        Log.d(TAG, "productFilterHeaderBus" + isDoneFilter);
    }

    @Subscribe
    public void onEvent(ProductFilterItemBus productFilterItemBus) {
        showProgressDialog();
        isDoneFilter = true;
        isSorting = false;
        currentPage = 1; // clear current page
        ProductFilterItem productFilterItem = productFilterItemBus.getProductFilterItem();
        Log.d(TAG, "productFilterItemBus" + productFilterItem.getId());
        callFilter(productFilterItem.getId(), sortName, sortType);
        title = productFilterItem.getFilterName();
        categoryId = productFilterItem.getId();
        mPowerBuyPopupWindow.updateSingleProductFilterItem(productFilterItem);
        Log.d(TAG, "productFilterItemBus" + isDoneFilter);
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
        if(isSearch){
            callFilter(keyWord, sortingItem.getSlug(), sortingItem.getValue());
        } else {
            callFilter(categoryId, sortingItem.getSlug(), sortingItem.getValue());
        }
        mPowerBuyPopupWindow.updateSingleSortingItem(sortingItemBus.getSortingItem());
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

    public static ProductListFragment newInstance(ProductFilterHeader productFilterHeader,
                                                  boolean search, String storeId, String keyWord) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, productFilterHeader.getName());
        args.putBoolean(ARG_SEARCH, search);
        args.putString(ARG_DEPARTMENT_ID, productFilterHeader.getId());
        args.putString(ARG_STORE_ID, storeId);
        args.putString(ARG_KEY_WORD, keyWord);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListFragment newInstance(ProductFilterSubHeader productFilterSubHeader,
                                                  boolean search, String storeId, String keyWord) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, productFilterSubHeader.getName());
        args.putBoolean(ARG_SEARCH, search);
        args.putString(ARG_DEPARTMENT_ID, productFilterSubHeader.getId());
        args.putString(ARG_STORE_ID, storeId);
        args.putParcelable(ARG_PRODUCT_FILTER_SUB_HEADER, productFilterSubHeader);
        args.putString(ARG_KEY_WORD, keyWord);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListFragment newInstance(Category category, boolean search, String storeId, String keyWord) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, category.getDepartmentName());
        args.putBoolean(ARG_SEARCH, search);
        args.putString(ARG_DEPARTMENT_ID, category.getId());
        args.putString(ARG_STORE_ID, storeId);
        args.putString(ARG_KEY_WORD, keyWord);
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
            ProductFilterSubHeader mProductFilterSubHeader = getArguments().getParcelable(ARG_PRODUCT_FILTER_SUB_HEADER);

            // setup filter list
            if (mProductFilterSubHeader != null) {
                mProductFilterList = new ProductFilterList(mProductFilterSubHeader.getProductFilterItemList());
            }

            keyWord = getArguments().getString(ARG_KEY_WORD);
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
//                EventBus.getDefault().post(new ProductBackBus(true));
                EventBus.getDefault().post(new CategoryTwoBus());
                break;
            case R.id.layout_product:
                if (mProductFilterList == null) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
//                    mTempProductFilterList = new ProductFilterList(mProductFilterList);
//                    mTempProductFilterList = mProductFilterList;
                    mPowerBuyPopupWindow.setRecyclerViewFilter(mProductFilterList);
                    mPowerBuyPopupWindow.showAsDropDown(v);
//                    mTempSortingList = new SortingList(mSortingList);
                }
                break;
            case R.id.layout_sort:
                if (mSortingList == null) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
//                    mTempSortingList = new SortingList(mSortingList);
                    mPowerBuyPopupWindow.setRecyclerViewSorting(mSortingList);
                    mPowerBuyPopupWindow.showAsDropDown(v);
//                    mTempProductFilterList = mProductFilterList;
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
        LinearLayout productLayout = rootView.findViewById(R.id.layout_product);
        LinearLayout sortLayout = rootView.findViewById(R.id.layout_sort);
        LinearLayout brandLayout = rootView.findViewById(R.id.layout_brand);

        titleLayout.setOnClickListener(this);
        productLayout.setOnClickListener(this);
        sortLayout.setOnClickListener(this);
        brandLayout.setOnClickListener(this);

        if (isSearch) {
            layoutFilter.setVisibility(View.GONE);
        }

        if (mProductFilterList == null) {
            productLayout.setVisibility(View.GONE);
        } else {
            productLayout.setVisibility(View.VISIBLE);
        }

        popUpShow();

        mLayoutManger = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        mLayoutManger.setSpanSizeLookup(mProductListAdapter.getSpanSize());
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setAdapter(mProductListAdapter);

        if (savedInstanceState == null) {
            if (isSearch) {
                showProgressDialog();
                getProductsFromSearch(keyWord);
            } else {
                showProgressDialog();
                retrieveProductList(categoryId);
            }
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
        if (mProductDao != null) {
            mProductDao.getProductListList().clear();
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
        outState.putParcelable(ARG_PRODUCT, mProductDao);
        outState.putParcelable(ARG_PRODUCT_FILTER, mProductFilterList);
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
        mProductDao = savedInstanceState.getParcelable(ARG_PRODUCT);
        mProductFilterList = savedInstanceState.getParcelable(ARG_PRODUCT_FILTER);
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

    private void callFilter(String categoryId, String sortNameT, String sortTypeT) {
        resetPage();
        sortName = sortNameT;
        sortType = sortTypeT;
        if(isSearch){
            getProductsFromSearch(keyWord);
        } else {
            if (brandId != null) {
                retrieveProductList(categoryId, brandId);
            } else {
                retrieveProductList(categoryId);
            }
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

    private void retrieveProductList(final String categoryId) {
        if (getContext() != null) {

            new Filter();
            Filter filter = Filter.Companion.createFilter("category_id", categoryId, "eq");
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(filter);
            FilterGroups filterGroups = new FilterGroups(filters);
            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroups);


            new SortOrder();
            SortOrder sortOrder = SortOrder.Companion.createSortOrder("view_count", "DESC");
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (response != null) {
                                            updateProductList(response);
                                        } else {
                                            Log.d("productResponse", "productResponse is null");
                                            layoutProgress.setVisibility(View.GONE);
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull APIError error) {
                            layoutProgress.setVisibility(View.GONE);
                            mProgressDialog.dismiss();
                            // show error dialog
                            if (getContext() != null) {
                                new DialogHelper(getContext()).showErrorDialog(error);
                            }
                        }
                    }
            );
        }
    }

    private void retrieveProductList(String categoryId, Long brandId) {
        if (getContext() != null) {

            new Filter();
            Filter filter = Filter.Companion.createFilter("category_id", categoryId, "eq");
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(filter);
            FilterGroups filterGroups = new FilterGroups(filters);
            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroups);


            new SortOrder();
            SortOrder sortOrder = SortOrder.Companion.createSortOrder("view_count", "DESC");
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (response != null) {
                                            updateProductList(response);
                                        } else {
                                            Log.d("productResponse", "productResponse is null");
                                            layoutProgress.setVisibility(View.GONE);
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull APIError error) {
                            layoutProgress.setVisibility(View.GONE);
                            mProgressDialog.dismiss();
                            // show error dialog
                            if (getContext() != null) {
                                new DialogHelper(getContext()).showErrorDialog(error);
                            }
                        }
                    }
            );
        }
    }


    private void getProductsFromSearch(String keyWord) {
        if (getContext() != null) {
            new Filter();
            Filter filter = Filter.Companion.createFilter("search_term", keyWord, "eq");
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(filter);
            FilterGroups filterGroups = new FilterGroups(filters);
            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroups);


            new SortOrder();
            SortOrder sortOrder = SortOrder.Companion.createSortOrder("view_count", "DESC");
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (response != null) {
                                            updateProductList(response);
                                        } else {
                                            Log.d("productResponse", "productResponse is null");
                                            layoutProgress.setVisibility(View.GONE);
                                            mProgressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull APIError error) {
                            layoutProgress.setVisibility(View.GONE);
                            mProgressDialog.dismiss();
                            // show error dialog
                            if (getContext() != null) {
                                new DialogHelper(getContext()).showErrorDialog(error);
                            }
                        }
                    }
            );
        }
    }


    private void getBrands(String departmentId) {
        if (getContext() != null) {
            HttpManagerMagento.Companion.getInstance(getContext()).getBrands(departmentId, new ApiResponseCallback<List<Brand>>() {
                @Override
                public void success(@org.jetbrains.annotations.Nullable List<Brand> response) {
                    if (response != null) {
                        ProductListFragment.this.brands = response;
                    }
                    layoutProgress.setVisibility(View.GONE);
                    mProgressDialog.dismiss();
                }

                @Override
                public void failure(@NotNull APIError error) {
                    Log.d("getBrands", error.toString());
                    layoutProgress.setVisibility(View.GONE);
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    private void updateProductList(ProductResponse response) {
        if (response != null) {
            totalItem = response.getTotalCount();
            totalPage = totalPageCal(totalItem);
            currentPage = getNextPage();
            response.setCurrentPage(currentPage);
            mProductListAdapter.setProduct(response);
            setTextHeader(totalItem, title);
        } else {
            if (mProductListAdapter.getItemCount() == 0) {
                mProductListAdapter.setError();
            }
            setTextHeader(totalItem, title);
        }

        if (!isSearch) {
            getBrands(categoryId);
        } else {
            layoutProgress.setVisibility(View.GONE);
            mProgressDialog.dismiss();
        }
    }

    // region {@link OnBrandFilterClickListener}
    @Override
    public void onClickedItem(@NotNull Brand brand) {
        isDoneFilter = true;
        isSorting = false;
        brandId = brand.getBrandId();
        currentPage = 0; // clear current page
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            showProgressDialog();
        }
        if (mPowerBuyPopupWindow.isShowing()) {
            mPowerBuyPopupWindow.dismiss();
        }
        retrieveProductList(categoryId, brandId);
    }
    // endregion
}
