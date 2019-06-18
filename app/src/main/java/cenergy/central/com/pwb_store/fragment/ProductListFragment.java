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
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.FilterItem;
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
    private String sortName = "view_count";
    private String sortType = "DESC";
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

    final PowerBuyPopupWindow.OnDismissListener ON_POPUP_DISMISS_LISTENER = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
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
                    getProductsFromSearch(keyWord, sortName, sortType);
                } else {
                    if (brandName != null) {
                        retrieveProductList(categoryId, brandName, sortName, sortType);
                    } else {
                        retrieveProductList(categoryId, sortName, sortType);
                    }
                }

                isLoadingMore = true;
            }
        }
    };

    @Subscribe
    public void onEvent(ProductFilterItemBus productFilterItemBus) {
        showProgressDialog();
        isDoneFilter = true;
        isSorting = true;
        currentPage = 1; // clear current page
        Category categoryLv3 = productFilterItemBus.getProductFilterItem();
        Log.d(TAG, "productFilterItemBus" + categoryLv3.getId());
        callFilter(categoryLv3.getId(), sortName, sortType);
        title = categoryLv3.getDepartmentName();
        categoryId = categoryLv3.getId();
        mPowerBuyPopupWindow.updateSingleProductFilterItem(categoryLv3);
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
                if (categoriesLv3 == null) {
                    mPowerBuyPopupWindow.dismiss();
                } else {
                    mPowerBuyPopupWindow.setRecyclerViewFilter(categoriesLv3);
                    mPowerBuyPopupWindow.showAsDropDown(v);
                }
                break;
            case R.id.layout_sort:
                if (mSortingList == null) {
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
            if (isSearch) {
                showProgressDialog();
                getProductsFromSearch(keyWord, sortName, sortType);
            } else {
                showProgressDialog();
                retrieveProductList(categoryId, sortName, sortType);
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
            sortName = "view_count";
            sortType = "DESC";
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

    private void callFilter(String categoryId, String sortNameT, String sortTypeT) {
        resetPage();
        sortName = sortNameT;
        sortType = sortTypeT;
        if(isSearch){
            getProductsFromSearch(keyWord, sortName, sortType);
        } else {
            if (brandName != null) {
                retrieveProductList(categoryId, brandName, sortName, sortType);
            } else {
                retrieveProductList(categoryId, sortName, sortType);
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

    private void retrieveProductList(String categoryId, String sortName, String sortType) {
        if (getContext() != null) {

            Filter filterCategory = Filter.Companion.createFilter("category_id", categoryId, "eq");
            ArrayList<Filter> filterByCategory = new ArrayList<>();
            filterByCategory.add(filterCategory);
            FilterGroups filterGroupCategory = new FilterGroups(filterByCategory);

            Filter filterStock = Filter.Companion.createFilter("stock.salable", "1", "eq");
            ArrayList<Filter> filterByStock = new ArrayList<>();
            filterByStock.add(filterStock);
            FilterGroups filterGroupStock = new FilterGroups(filterByStock);

            Filter filterPrice = Filter.Companion.createFilter("price", "0", "gt");
            ArrayList<Filter> filterByPrice = new ArrayList<>();
            filterByPrice.add(filterPrice);
            FilterGroups filterGroupPrice = new FilterGroups(filterByPrice);

            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroupCategory);
            filterGroupsList.add(filterGroupStock);
            filterGroupsList.add(filterGroupPrice);

            SortOrder sortOrder = SortOrder.Companion.createSortOrder(sortName, sortType);
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    PER_PAGE,
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (response != null) {
                                        updateProductList(response);
                                    } else {
                                        Log.d("productResponse", "productResponse is null");
                                        layoutProgress.setVisibility(View.GONE);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull final APIError error) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(
                                        () -> {
                                            layoutProgress.setVisibility(View.GONE);
                                            mProgressDialog.dismiss();
                                            // show error dialog
                                            if (getContext() != null) {
                                                new DialogHelper(getContext()).showErrorDialog(error);
                                            }
                                        });
                            }
                        }
                    }
            );
        }
    }

    private void retrieveProductList(String categoryId, String brandName, String sortName, String sortType) {
        if (getContext() != null) {

            Filter filterCategory = Filter.Companion.createFilter("category_id", categoryId, "eq");
            ArrayList<Filter> filterByCategory = new ArrayList<>();
            filterByCategory.add(filterCategory);
            FilterGroups filterGroupCategory = new FilterGroups(filterByCategory);

            Filter filterStock = Filter.Companion.createFilter("stock.salable", "1", "eq");
            ArrayList<Filter> filterByStock = new ArrayList<>();
            filterByStock.add(filterStock);
            FilterGroups filterGroupStock = new FilterGroups(filterByStock);

            Filter filterPrice = Filter.Companion.createFilter("price", "0", "gt");
            ArrayList<Filter> filterByPrice = new ArrayList<>();
            filterByPrice.add(filterPrice);
            FilterGroups filterGroupPrice = new FilterGroups(filterByPrice);

            Filter filterBrand = Filter.Companion.createFilter("brand", brandName, "eq");
            ArrayList<Filter> filterByBrand = new ArrayList<>();
            filterByBrand.add(filterBrand);
            FilterGroups filterGroupBrand = new FilterGroups(filterByBrand);

            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroupCategory);
            filterGroupsList.add(filterGroupStock);
            filterGroupsList.add(filterGroupPrice);
            filterGroupsList.add(filterGroupBrand);

            SortOrder sortOrder = SortOrder.Companion.createSortOrder(sortName, sortType);
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    PER_PAGE,
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (response != null) {
                                        updateProductList(response);
                                    } else {
                                        Log.d("productResponse", "productResponse is null");
                                        layoutProgress.setVisibility(View.GONE);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull final APIError error) {
                            if(getActivity() != null){
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
                    }
            );
        }
    }


    private void getProductsFromSearch(String keyWord, String sortName, String sortType) {
        if (getContext() != null) {
            Filter filter = Filter.Companion.createFilter("search_term", keyWord, "eq");
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(filter);
            FilterGroups filterGroupSearch = new FilterGroups(filters);

            Filter filterStock = Filter.Companion.createFilter("stock.salable", "1", "eq");
            ArrayList<Filter> filterByStock = new ArrayList<>();
            filterByStock.add(filterStock);
            FilterGroups filterGroupStock = new FilterGroups(filterByStock);

            Filter filterPrice = Filter.Companion.createFilter("price", "0", "gt");
            ArrayList<Filter> filterByPrice = new ArrayList<>();
            filterByPrice.add(filterPrice);
            FilterGroups filterGroupPrice = new FilterGroups(filterByPrice);

            ArrayList<FilterGroups> filterGroupsList = new ArrayList<>();
            filterGroupsList.add(filterGroupSearch);
            filterGroupsList.add(filterGroupStock);
            filterGroupsList.add(filterGroupPrice);

            SortOrder sortOrder = SortOrder.Companion.createSortOrder(sortName, sortType);
            ArrayList<SortOrder> sortOrders = new ArrayList<>();
            sortOrders.add(sortOrder);

            HttpManagerMagento.Companion.getInstance(getContext()).retrieveProducts(
                    PER_PAGE,
                    getNextPage(),
                    filterGroupsList,
                    sortOrders,
                    new ApiResponseCallback<ProductResponse>() {
                        @Override
                        public void success(@org.jetbrains.annotations.Nullable final ProductResponse response) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    if (response != null) {
                                        updateProductList(response);
                                    } else {
                                        Log.d("productResponse", "productResponse is null");
                                        layoutProgress.setVisibility(View.GONE);
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(@NotNull final APIError error) {
                            if (getActivity() != null){
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
                    }
            );
        }
    }

    private void updateProductList(ProductResponse response) {
        if (response != null) {
            if (!response.getFilters().isEmpty()){
                brands = response.getFilters().get(0).getItems();
            }
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

//        if (!isSearch) {
//            getBrands(categoryId);
//        } else {
//            layoutProgress.setVisibility(View.GONE);
//            mProgressDialog.dismiss();
//        }

        layoutProgress.setVisibility(View.GONE);
        mProgressDialog.dismiss();
    }

    // region {@link OnBrandFilterClickListener}
    @Override
    public void onClickedItem(@NotNull FilterItem filterItem) {
        isDoneFilter = true;
        resetPage();
        brandName = filterItem.getValue(); // brand name
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            showProgressDialog();
        }
        if (mPowerBuyPopupWindow.isShowing()) {
            mPowerBuyPopupWindow.dismiss();
        }
        retrieveProductList(categoryId, brandName, sortName, sortType);
    }
    // endregion

    private void loadCategoryLv3(Category categoryLv2) {
        if (getContext() == null) return;
        HttpManagerMagento.Companion.getInstance(getContext()).retrieveCategory(categoryLv2.getId(),
                true, new ApiResponseCallback<List<Category>>() {
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
                        Log.e(TAG, "onFailure: " + error.getErrorUserMessage());
                    }
                });
    }
}
