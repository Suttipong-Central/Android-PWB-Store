package cenergy.central.com.pwb_store.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.AvaliableStoreFilterAdapter;
import cenergy.central.com.pwb_store.adapter.FilterByBrandAdapter;
import cenergy.central.com.pwb_store.adapter.ProductFilterAdapter;
import cenergy.central.com.pwb_store.adapter.SortingAdapter;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.model.Category;
import cenergy.central.com.pwb_store.model.FilterItem;
import cenergy.central.com.pwb_store.model.SortingHeader;
import cenergy.central.com.pwb_store.model.SortingItem;
import cenergy.central.com.pwb_store.model.SortingList;
import cenergy.central.com.pwb_store.model.StoreFilterHeader;
import cenergy.central.com.pwb_store.model.StoreFilterList;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class PowerBuyPopupWindow extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "PowerBuyPopupWindow";

    private RecyclerView mRecyclerViewFilter;

    //Data Member
    private FilterByBrandAdapter mFilterByBrandAdapter;
    private ProductFilterAdapter mProductFilterAdapter;
    private SortingAdapter mSortingAdapter;
    private AvaliableStoreFilterAdapter mAvaliableStoreFilterAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Category>  mProductFilterList;
    private StoreFilterList mStoreFilterList;
    private Context mContext;

    @SuppressLint("InflateParams")
    public PowerBuyPopupWindow(Context context, LayoutInflater layoutInflater) {
        super(layoutInflater.inflate(R.layout.popup_filter, null, false),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mRecyclerViewFilter = getContentView().findViewById(R.id.recycler_view_filter);
        this.mContext = context;
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.background_popup_window));
        setOutsideTouchable(true);
    }

    public void setRecyclerViewFilter(ArrayList<Category> categoriesLv3) {
        this.mProductFilterList = categoriesLv3;
        mProductFilterAdapter = new ProductFilterAdapter();
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mProductFilterAdapter.setProductFilter(categoriesLv3);
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mProductFilterAdapter);
    }

    public void setRecyclerViewSorting(SortingList sorting) {
        mSortingAdapter = new SortingAdapter(mContext);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mSortingAdapter.setSorting(sorting);
        for (SortingHeader sortingHeader : sorting.getSortingHeaders()) {
            mSortingAdapter.addSortLevel2(sortingHeader.getSortingItems());
        }
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mSortingAdapter);
    }

    public void setRecyclerViewStore(StoreFilterList storeFilterList) {
        this.mStoreFilterList = storeFilterList;
        mAvaliableStoreFilterAdapter = new AvaliableStoreFilterAdapter(mContext);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAvaliableStoreFilterAdapter.setStoreFilter(storeFilterList);
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mAvaliableStoreFilterAdapter);
    }

    public void setRecyclerViewFilterByBrand(List<FilterItem> filterItems, OnBrandFilterClickListener listener) {
        mFilterByBrandAdapter = new FilterByBrandAdapter(listener);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mFilterByBrandAdapter.setBrandForFilter(filterItems);
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mFilterByBrandAdapter);
    }

    public void setSortingItem(SortingHeader sortingHeader) {
        //if (sortingHeader.isExpanded()) {
        //           mSortingAdapter.addSortLevel2(sortingHeader);
//        } else {
//            mSortingAdapter.removeSortingLevel3(mSortingList);
//        }
    }

    public void setStoreItem(StoreFilterHeader storeFilterHeader) {
        if (storeFilterHeader.isExpanded()) {
            mAvaliableStoreFilterAdapter.addStoreLevel2(storeFilterHeader);
        } else {
            mAvaliableStoreFilterAdapter.removeStoreLevel3(mStoreFilterList);
        }
    }

    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor, 0, 25, Gravity.BOTTOM|Gravity.END);

        // setup shadow
        View container;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            container = (View) getContentView().getParent().getParent();
        } else {
            container = (View) getContentView().getParent();
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.1f;
        assert wm != null;
        wm.updateViewLayout(container, p);
    }

    public void updateSingleSortingItem(SortingItem sortingItem) {
        mSortingAdapter.updateSingleProductFilterItem(sortingItem);
    }

    public void updateSingleProductFilterItem(Category category) {
        mProductFilterAdapter.updateSingleProductFilterItem(category);
    }

    public void updateSingleBrandFilterItem(FilterItem filterItem) {
        mFilterByBrandAdapter.updateSingleBrandFilterItem(filterItem);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Popup Click");
    }
}
