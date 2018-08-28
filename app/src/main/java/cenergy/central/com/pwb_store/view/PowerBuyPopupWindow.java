package cenergy.central.com.pwb_store.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cenergy.central.com.pwb_store.R;
import cenergy.central.com.pwb_store.adapter.AvaliableStoreFilterAdapter;
import cenergy.central.com.pwb_store.adapter.FilterByBrandAdapter;
import cenergy.central.com.pwb_store.adapter.ProductFilterAdapter;
import cenergy.central.com.pwb_store.adapter.SortingAdapter;
import cenergy.central.com.pwb_store.adapter.interfaces.OnBrandFilterClickListener;
import cenergy.central.com.pwb_store.model.Brand;
import cenergy.central.com.pwb_store.model.ProductFilterItem;
import cenergy.central.com.pwb_store.model.ProductFilterList;
import cenergy.central.com.pwb_store.model.ProductFilterSubHeader;
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
    //View Members
    @BindView(R.id.recycler_view_filter)
    RecyclerView mRecyclerViewFilter;

    //Data Member
    private ProductFilterAdapter mProductFilterAdapter;
    private SortingAdapter mSortingAdapter;
    private AvaliableStoreFilterAdapter mAvaliableStoreFilterAdapter;
    private LinearLayoutManager mLayoutManager;
    private ProductFilterList mProductFilterList;
    private SortingList mSortingList;
    private List<Brand> brands;
    private StoreFilterList mStoreFilterList;
    private Context mContext;
    private int index;
    private int top;


    public PowerBuyPopupWindow(Context context, LayoutInflater layoutInflater) {
        super(layoutInflater.inflate(R.layout.popup_filter, null, false), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ButterKnife.bind(this, getContentView());
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.alignWithParent = true;
//        lp.setMargins(0, 0, 20, 0);
//        getContentView().setLayoutParams(lp);
        this.mContext = context;
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.background_popup_window));
        setOutsideTouchable(true);
    }

    public void setRecyclerViewFilter(ProductFilterList productFilterList) {
        this.mProductFilterList = productFilterList;
        mProductFilterAdapter = new ProductFilterAdapter(mContext);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mProductFilterAdapter.setProductFilter(productFilterList);
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mProductFilterAdapter);
    }

    public void setRecyclerViewSorting(SortingList sorting) {
        this.mSortingList = sorting;
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

//    public void setFilterItem(ProductFilterHeader productFilterHeader) {
//        if (productFilterHeader.isExpanded()) {
//            mProductFilterAdapter.addProductFilterItem(productFilterHeader);
//        } else {
//            mProductFilterAdapter.removeProductFilterItem(productFilterHeader);
//        }
//    }

    public void setFilterItem(ProductFilterSubHeader productFilterSubHeader) {
        if (productFilterSubHeader.isExpanded()) {
            mProductFilterAdapter.addProductLevel2(productFilterSubHeader);
        } else {
            mProductFilterAdapter.removeProductLevel3(mProductFilterList);
        }
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
//        Rect location = locateView(anchor);
//        showAtLocation((View) anchor.getParent(), Gravity.TOP|Gravity.END, location.left - location.right, location.bottom);
//        //super.showAsDropDown(anchor, 25, 25, Gravity.BOTTOM);
//        this.showAtLocation(anchor, Gravity.CENTER, 45, 0);
        super.showAsDropDown(anchor, 25, 25, Gravity.BOTTOM | Gravity.RIGHT);

        // setup shadow
        View container;
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            container = (View) getContentView().getParent().getParent();
        } else {
            container = (View) getContentView().getParent();
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.1f;
        //p.x = 405;
        wm.updateViewLayout(container, p);
    }

    public void setLayoutParams(RelativeLayout.LayoutParams layoutParams) {

    }

    public void updateSingleSortingItem(SortingItem sortingItem) {
        mSortingAdapter.updateSingleProductFilterItem(sortingItem);
    }

    public void updateSingleProductFilterItem(ProductFilterItem productFilterItem) {
        mProductFilterAdapter.updateSingleProductFilterItem(productFilterItem);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Popup Click");
    }

    public void setRecyclerViewFilterByBrand(List<Brand> brands, OnBrandFilterClickListener listener) {
        this.brands = brands;
        FilterByBrandAdapter mFilterByBrandAdapter = new FilterByBrandAdapter(listener);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mFilterByBrandAdapter.setBrandForFilter(brands);
        mRecyclerViewFilter.setLayoutManager(mLayoutManager);
        mRecyclerViewFilter.setAdapter(mFilterByBrandAdapter);
    }

    private Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }
}
