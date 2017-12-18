package cenergy.central.com.pwb_store.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.RelativeLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.activity.ProductDetailActivity;
import cenergy.central.com.pwb_store.adapter.ProductListAdapter;
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration;
import cenergy.central.com.pwb_store.manager.HttpManagerMagento;
import cenergy.central.com.pwb_store.manager.UserInfoManager;
import cenergy.central.com.pwb_store.manager.bus.event.ProductBackBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingHeaderBus;
import cenergy.central.com.pwb_store.manager.bus.event.SortingItemBus;
import cenergy.central.com.pwb_store.model.APIError;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.ProductDao;
import cenergy.central.com.pwb_store.model.ProductFilterHeader;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterList;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.model.SortingList;
import cenergy.central.com.pwb_store.utils.APIErrorUtils;
import cenergy.central.com.pwb_store.utils.DialogUtils;
import cenergy.central.com.pwb_store.view.PowerBuyPopupWindow;
import cenergy.central.com.pwb_store.view.PowerBuyTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.ceil;

/**
 * Created by napabhat on 7/6/2017 AD.
 */

public class ProductListFragment extends Fragment implements ObservableScrollViewCallbacks {
    private static final String TAG = ProductListFragment.class.getSimpleName();
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_SEARCH = "ARG_SEARCH";
    private static final String ARG_PRODUCT = "ARG_PRODUCT";
    private static final String ARG_DEPARTMENT_ID = "ARG_DEPARTMENT_ID";
    private static final String ARG_STORE_ID = "ARG_STORE_ID";
    private static final String ARG_PRODUCT_FILTER = "ARG_PRODUCT_FILTER";
    private static final String ARG_PRODUCT_FILTER_TEMP = "ARG_PRODUCT_FILTER_TEMP";
    private static final String ARG_SORT_NAME = "ARG_SORT_NAME";
    private static final String ARG_SORT_TYPE = "ARG_SORT_TYPE";
    private static final String ARG_IS_DONE = "ARG_IS_DONE";
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_CATEGORY = "ARG_CATEGORY";
    private static final String ARG_KEY_WORD = "ARG_KEY_WORD";
    private static final String ARG_IS_SORTING = "ARG_IS_SORTING";

    //View Members
    @BindView(R.id.recycler_view_list)
    ObservableRecyclerView mRecyclerView;

    @BindView(R.id.txt_title_product)
    PowerBuyTextView productTitle;

    @BindView(R.id.layout_filter)
    RelativeLayout layoutFilter;

    @BindView(R.id.txt_product_count)
    PowerBuyTextView productCount;

    @BindView(R.id.layout_product)
    LinearLayout productLayout;

    @BindView(R.id.layout_sort)
    LinearLayout sortLayout;

    //Data Member
    private ProductListAdapter mProductListAdapter;
    private GridLayoutManager mLayoutManger;
    private ProductDao mProductDao;
    private ProductFilterList mProductFilterList;
    private ProductFilterList mTempProductFilterList;
    private SortingList mSortingList;
    private SortingList mTempSortingList;
    private String title;
    private PowerBuyPopupWindow mPowerBuyPopupWindow;
    private ProgressDialog mProgressDialog;
    private boolean isDoneFilter;
    private boolean isSearch;
    private String departmentId;
    private String storeId;
    private String sortName;
    private Category mCategory;
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
    private List<ProductDao> mProductDaoList = new ArrayList<>();

    public ProductListFragment() {
        super();
    }

    final PowerBuyPopupWindow.OnDismissListener ON_POPUP_DISMISS_LISTENER = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            if (!isDoneFilter) {
                Log.d(TAG, "DISMISS" + isDoneFilter);
                mProductFilterList = mTempProductFilterList;
                mSortingList = mTempSortingList;
            }
            if (mProductFilterList != null) {
                if (mProductFilterList.getProductFilterHeaders() != null) {
                    for (ProductFilterHeader productFilterHeader : mProductFilterList.getProductFilterHeaders()) {
                        productFilterHeader.setExpanded(false);
                    }
                }
            } else {
                for (SortingHeader sortingHeader : mSortingList.getSortingHeaders()) {
                    sortingHeader.setExpanded(false);
                }
            }

            isDoneFilter = false;
        }
    };


//    //Listeners
//    final RecyclerView.OnScrollListener SCROLL_LISTENER = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int totalItemCount = mLayoutManger.getItemCount();
//            int visibleItemCount = mLayoutManger.getChildCount();
//            int firstVisibleItem = mLayoutManger.findFirstVisibleItemPosition();
//
//            if (isLoadingMore && totalItemCount > mPreviousTotal) {
//                isLoadingMore = false;
//                mPreviousTotal = totalItemCount;
//            }
//            int visibleThreshold = 10;
//            if (!isLoadingMore
//                    && (totalItemCount) <= (firstVisibleItem + visibleItemCount + visibleThreshold)
//                    && isStillHavePages()) {
//
//                if (isSearch == true) {
//                    HttpManagerMagento.getInstance().getProductService().getProductSearch("quick_search_container", "search_term",
//                            keyWord, PER_PAGE, getNextPage(), getString(R.string.product_list), UserInfoManager.getInstance().getUserId()).enqueue(CALLBACK_PRODUCT);
//                } else {
//                    HttpManagerMagento.getInstance().getProductService().getProductList("category_id", departmentId, "in", "in_stores", storeId,
//                            "finset", PER_PAGE, getNextPage(), sortName, sortType, getString(R.string.product_list)).enqueue(CALLBACK_PRODUCT);
//                }
//
//                isLoadingMore = true;
//            }
//        }
//    };

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

            if (isSearch == true) {
                HttpManagerMagento.getInstance().getProductService().getProductSearch("quick_search_container", "search_term",
                        keyWord, PER_PAGE, getNextPage(), getString(R.string.product_list), UserInfoManager.getInstance().getUserId()).enqueue(CALLBACK_PRODUCT);
            } else {
                HttpManagerMagento.getInstance().getProductService().getProductList("category_id", departmentId, "in", "in_stores", storeId,
                        "finset", PER_PAGE, getNextPage(), sortName, sortType, getString(R.string.product_list)).enqueue(CALLBACK_PRODUCT);
            }

            isLoadingMore = true;
        }
    }
};

    final Callback<ProductDao> CALLBACK_PRODUCT = new Callback<ProductDao>() {
        @Override
        public void onResponse(Call<ProductDao> call, Response<ProductDao> response) {
            if (response.isSuccessful()) {
                //mProductDao = response.body();
                ProductDao productDao = response.body();
                //TODO Test Total Page.
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

                totalItem = productDao.getTotalElement();
                totalPage = totalPageCal(totalItem);
                Log.d(TAG, " totalPage :" + totalPage);
                if (productDao.getProductListList() != null) {
                    currentPage = getNextPage();
                    if (productDao != null) {
                        productDao.setCurrentPage(currentPage);
                    }
                    mProductListAdapter.setProduct(productDao);
                } else if (productDao.getProductListList() == null) {
                    mProductListAdapter.setError();
                } else {
                    mProductListAdapter.setError();
                }
                setTextHeader(totalItem, title);
                mProgressDialog.dismiss();
            } else {
                mProductListAdapter.setError();
                setTextHeader(totalItem, title);
                APIError error = APIErrorUtils.parseError(response);
                Log.e(TAG, "onResponse: " + error.getErrorMessage());
//                showAlertDialog(error.getErrorMessage(), false);
                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<ProductDao> call, Throwable t) {
            Log.e(TAG, "onFailure: ", t);
            mProgressDialog.dismiss();
            setTextHeader(totalItem, title);
        }
    };

//    final Callback<List<ProductFilterHeader>> CALLBACK_PRODUCT_FILTER = new Callback<List<ProductFilterHeader>>() {
//        @Override
//        public void onResponse(Call<List<ProductFilterHeader>> call, Response<List<ProductFilterHeader>> response) {
//            if (response.isSuccessful()) {
//                mProductFilterList = new ProductFilterList(response.body());
//                List<ProductFilterHeader> productFilterHeaders = new ArrayList<>();
//                for (ProductFilterHeader productFilterHeader : mProductFilterList.getProductFilterHeaders()){
//                    productFilterHeaders.add(new ProductFilterHeader(productFilterHeader.getDepartmentId(), productFilterHeader.getRootDeptId(),
//                            productFilterHeader.getId(), productFilterHeader.getName(), productFilterHeader.getNameEN(),
//                            productFilterHeader.getSlug(), productFilterHeader.getMetaDescription(), productFilterHeader.getUrlName(),
//                            productFilterHeader.getUrlNameEN(), "single", productFilterHeader.getProductFilterItemList()));
//                }
//                mProductFilterList = new ProductFilterList(productFilterHeaders);
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<ProductFilterHeader>> call, Throwable t) {
//            Log.e(TAG, "onFailure: ", t);
//        }
//    };

    @Subscribe
    public void onEvent(ProductFilterHeaderBus productFilterHeaderBus) {
        showProgressDialog();
        isDoneFilter = true;
        isSorting = false;
        ProductFilterHeader productFilterHeader = productFilterHeaderBus.getProductFilterHeader();
        callFilter(productFilterHeader.getId(), sortName, sortType);
        title = productFilterHeader.getName();
        departmentId = productFilterHeader.getId();
        mPowerBuyPopupWindow.setFilterItem(productFilterHeaderBus.getProductFilterHeader());
        Log.d(TAG, "productFilterHeaderBus" + isDoneFilter);
    }

    @Subscribe
    public void onEvent(ProductFilterItemBus productFilterItemBus) {
        showProgressDialog();
        isDoneFilter = true;
        ProductFilterItem productFilterItem = productFilterItemBus.getProductFilterItem();
        callFilter(productFilterItem.getId(), sortName, sortType);
        title = productFilterItem.getFilterName();
        departmentId = productFilterItem.getId();
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
        callFilter(departmentId, sortingItem.getSlug(), sortingItem.getValue());
        mPowerBuyPopupWindow.updateSingleSortingItem(sortingItemBus.getSortingItem());
    }

    @Subscribe
    public void onEvent(ProductDetailBus productDetailBus) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_ID, productDetailBus.getProductId());
        ActivityCompat.startActivity(getContext(), intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(productDetailBus.getView(), 0, 0, productDetailBus.getView().getWidth(), productDetailBus.getView().getHeight())
                        .toBundle());
    }

    @SuppressWarnings("unused")
    public static ProductListFragment newInstance(String title, boolean search, String departmentId, String storeId, Category category, String keyWord) {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            departmentId = getArguments().getString(ARG_DEPARTMENT_ID);
            storeId = getArguments().getString(ARG_STORE_ID);
            mCategory = getArguments().getParcelable(ARG_CATEGORY);
            keyWord = getArguments().getString(ARG_KEY_WORD);
        }
        resetPage();

//        List<ProductList> mProductListList = new ArrayList<>();
//        mProductListList.add(new ProductList("1111","http://www.mx7.com/i/004/aOc9VL.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/0e5/oFp5mm.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2345","http://www.mx7.com/i/1cb/TL8yVR.png","Lenovo","Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)",35000,30000));
//        mProductListList.add(new ProductList("1122","http://www.mx7.com/i/215/WAY0dD.png","EPSON","เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)",9490,9000));
//        mProductListList.add(new ProductList("2233","http://www.mx7.com/i/004/aOc9VL.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/0e5/oFp5mm.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/1cb/TL8yVR.png","Lenovo","Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)",35000,30000));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/215/WAY0dD.png","EPSON","เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)",9490,9000));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/004/aOc9VL.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/0e5/oFp5mm.png","iPhone SE","หัวใจหลักของ iPhone SE ก็คือชิพ A9 ซึ่งเป็น\n" +
//                "ชิพอันล้ำสมัยแบบเดียวกับที่ใช้ใน iPhone 6s",16500,12600));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/1cb/TL8yVR.png","Lenovo","Lenovo Yoga 720 เป็นแล็ปท็อป Windows 10 มีสองโมเดลคือ 13 นิ้ว (น้ำหนัก 1.3 กิโลกรัม)",35000,30000));
//        mProductListList.add(new ProductList("2222","http://www.mx7.com/i/215/WAY0dD.png","EPSON","เครื่องพิมพ์มัลติฟังก์ชั่นอิงค์เจ็ท Print/ Copy/ Scan/ Fax(With ADF)",9490,9000));
//        mProductDao = new ProductDao(mProductListList, 20, 20, 10, 5, 5, false);

        // filter
//        List<ProductFilterItem> productFilterItems1 = new ArrayList<>();
//        productFilterItems1.add(new ProductFilterItem(1, "DSL", "dsl", "single", "", false));
//        productFilterItems1.add(new ProductFilterItem(1, "Digital Compact", "digital_compact", "single", "", false));
//        productFilterItems1.add(new ProductFilterItem(1, "Compact", "compact", "single", "", false));
//        productFilterItems1.add(new ProductFilterItem(1, "DSLR", "dslr", "single", "", false));
//        productFilterItems1.add(new ProductFilterItem(1, "SLR", "slr", "single", "", false));
//
//        List<ProductFilterItem> productFilterItems2 = new ArrayList<>();
//        productFilterItems2.add(new ProductFilterItem(2, "Tablet 10", "tablet_10", "single", "", false));
//        productFilterItems2.add(new ProductFilterItem(2, "iPhone", "iphone", "single", "", false));
//        productFilterItems2.add(new ProductFilterItem(2, "Samsung", "samsung", "single", "", false));
//        productFilterItems2.add(new ProductFilterItem(2, "OPPO", "oppo", "single", "", false));
//        productFilterItems2.add(new ProductFilterItem(2, "Sony", "sony", "single", "", false));
//
//        List<ProductFilterHeader> productFilterHeaders = new ArrayList<>();
//        productFilterHeaders.add(new ProductFilterHeader("1", "Camera", "camera", "multiple", productFilterItems1));
//        productFilterHeaders.add(new ProductFilterHeader("2", "Mobile & Tablet", "mobile_tablet", "multiple", productFilterItems2));
//
//        mProductFilterList = new ProductFilterList(productFilterHeaders);

        // sorting
        List<SortingItem> sortingItems = new ArrayList<>();
        sortingItems.add(new SortingItem(1, "Price : Low to High", "price", "ASC", "1", false));
        sortingItems.add(new SortingItem(2, "Price : High to Low", "price", "DESC", "2", false));
        sortingItems.add(new SortingItem(3, "Brand : Name(A-Z)", "brand", "ASC", "3", false));
        sortingItems.add(new SortingItem(4, "Brand : Name(Z-A)", "brand", "DESC", "4", false));
//        sortingItems.add(new SortingItem(5, "Discount : Low to High", "ASC", "ASC", "5", false));
//        sortingItems.add(new SortingItem(6, "Discount : High to Low", "DESC", "DESC", "6", false));

        List<SortingHeader> sortingHeaders = new ArrayList<>();
        sortingHeaders.add(new SortingHeader("0", "Sorting", "sorting", "single", sortingItems));

        mSortingList = new SortingList(sortingHeaders);
        for (SortingHeader sortingHeader : sortingHeaders) {
            mSortingList.updateSortOption(sortingHeader, true);
        }

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ButterKnife.bind(this, rootView);
        mProductListAdapter = new ProductListAdapter(getContext());
        mProductListAdapter.showLoading();
        productTitle.setText(title);
        productCount.setText(title);

        if (isSearch == true) {
            layoutFilter.setVisibility(View.GONE);
        }

        popUpShow();

        mLayoutManger = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        mLayoutManger.setSpanSizeLookup(mProductListAdapter.getSpanSize());
        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, LinearLayoutManager.VERTICAL));
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setAdapter(mProductListAdapter);

        if (savedInstanceState == null) {
            if (isSearch == true) {
                showProgressDialog();
                HttpManagerMagento.getInstance().getProductService().getProductSearch("quick_search_container",
                        "search_term", keyWord, PER_PAGE, 1, getString(R.string.product_list), UserInfoManager.getInstance().getUserId()).enqueue(CALLBACK_PRODUCT);
            } else {
                showProgressDialog();
                HttpManagerMagento.getInstance().getProductService().getProductList("category_id", departmentId, "in", "in_stores", storeId,
                        "finset", PER_PAGE, 1, sortName, sortType, getString(R.string.product_list)).enqueue(CALLBACK_PRODUCT);
            }
            if (mCategory != null) {
                mProductFilterList = new ProductFilterList(mCategory.getFilterHeaders());
            }
        }
////        else {
////            mProductListAdapter.setProduct(mProductDao);
////        }

        if (mCategory != null) {
            mProductFilterList = new ProductFilterList(mCategory.getFilterHeaders());
        }

        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.addOnScrollListener(SCROLL);

        //Log.d(TAG, "start Page" + isDoneFilter);
    }

    private void resetPage() {
        // mProductDao.getProductListList().clear();
        currentPage = 0;
        totalPage = 1;
        isLoadingMore = true;
        mPreviousTotal = 0;
        if (isSorting == false){
            sortName = "name";
            sortType = "ASC";
        }
        if (mProductDao != null){
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
        outState.putParcelable(ARG_PRODUCT, mProductDao);
        outState.putParcelable(ARG_PRODUCT_FILTER, mProductFilterList);
        outState.putParcelable(ARG_PRODUCT_FILTER_TEMP, mTempProductFilterList);
        outState.putString(ARG_DEPARTMENT_ID, departmentId);
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
        mTempProductFilterList = savedInstanceState.getParcelable(ARG_PRODUCT_FILTER_TEMP);
        departmentId = savedInstanceState.getString(ARG_DEPARTMENT_ID);
        sortName = savedInstanceState.getString(ARG_SORT_NAME);
        sortType = savedInstanceState.getString(ARG_SORT_TYPE);
        isDoneFilter = savedInstanceState.getBoolean(ARG_IS_DONE);
        title = savedInstanceState.getString(ARG_TITLE);
        currentPage = savedInstanceState.getInt(ARG_PAGE);
        keyWord = savedInstanceState.getString(ARG_KEY_WORD);
        isSearch = savedInstanceState.getBoolean(ARG_SEARCH);
        isSorting = savedInstanceState.getBoolean(ARG_IS_SORTING);
    }

    @OnClick(R.id.layout_title)
    public void onTitleClick(LinearLayout linearLayout) {
        EventBus.getDefault().post(new ProductBackBus(true));
    }

    @OnClick(R.id.layout_product)
    public void onProductFilterClick(LinearLayout linearLayout) {
        if (mProductFilterList == null) {
            mPowerBuyPopupWindow.dismiss();
        } else {
            mTempProductFilterList = new ProductFilterList(mProductFilterList);
            mPowerBuyPopupWindow.setRecyclerViewFilter(mProductFilterList);
            mPowerBuyPopupWindow.showAsDropDown(linearLayout);
            mTempSortingList = new SortingList(mSortingList);
        }
    }

    @OnClick(R.id.layout_sort)
    public void onSortClick(LinearLayout linearLayout) {
        if (mSortingList == null) {
            mPowerBuyPopupWindow.dismiss();
        } else {
            mTempSortingList = new SortingList(mSortingList);
            mPowerBuyPopupWindow.setRecyclerViewSorting(mSortingList);
            mPowerBuyPopupWindow.showAsDropDown(linearLayout);
            mTempProductFilterList = new ProductFilterList(mProductFilterList);
        }
    }

    private void popUpShow() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPowerBuyPopupWindow = new PowerBuyPopupWindow(getActivity(), layoutInflater);
        mPowerBuyPopupWindow.setOnDismissListener(ON_POPUP_DISMISS_LISTENER);
    }

    private void callFilter(String departmentId, String sortNameT, String sortTypeT) {
        resetPage();
        sortName = sortNameT;
        sortType = sortTypeT;
        HttpManagerMagento.getInstance().getProductService().getProductList("category_id", departmentId, "in", "in_stores", storeId,
                "finset", PER_PAGE, 1, sortName, sortType, getString(R.string.product_list)).enqueue(CALLBACK_PRODUCT);
    }

    private void setTextHeader(int total, String name) {
        productCount.setText(name + " " + mContext.getString(R.string.filter_count, String.valueOf(total)));
    }

    private int totalPageCal(int total) {
        int num;
        float x = ((float) total / 20);
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
}
