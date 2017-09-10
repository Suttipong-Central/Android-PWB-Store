package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class StoreFilterList implements IViewType, Parcelable {

    public static final Creator<StoreFilterList> CREATOR = new Creator<StoreFilterList>() {
        @Override
        public StoreFilterList createFromParcel(Parcel in) {
            return new StoreFilterList(in);
        }

        @Override
        public StoreFilterList[] newArray(int size) {
            return new StoreFilterList[size];
        }
    };
    private int viewTypeId;
    private int total;
    private List<StoreFilterHeader> mStoreFilterHeaders;

    public StoreFilterList(List<StoreFilterHeader> storeFilterHeaders) {
        this.mStoreFilterHeaders = storeFilterHeaders;
    }

    public StoreFilterList(StoreFilterList storeFilterList) {
        this.total = storeFilterList.getTotal();
        List<StoreFilterHeader> storeFilterHeaderList = new ArrayList<>();

        if (storeFilterList.isStoreFilterListAvailable()) {
            for (StoreFilterHeader storeFilterHeader :
                    storeFilterList.getStoreFilterHeaders()) {
                storeFilterHeaderList.add(new StoreFilterHeader(storeFilterHeader));
            }
        }

        this.mStoreFilterHeaders = storeFilterHeaderList;
    }

    protected StoreFilterList(Parcel in) {
        viewTypeId = in.readInt();
        mStoreFilterHeaders = in.createTypedArrayList(StoreFilterHeader.CREATOR);
        total = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(mStoreFilterHeaders);
        dest.writeInt(total);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<StoreFilterHeader> getStoreFilterHeaders() {
        return mStoreFilterHeaders;
    }

    public void setStoreFilterHeaders(List<StoreFilterHeader> storeFilterHeaders) {
        mStoreFilterHeaders = storeFilterHeaders;
    }

    public void updateStoreFilter(StoreFilterList storeFilterList, boolean isPreserveSelection) {
        removeNonExistingFilterItem(storeFilterList, isPreserveSelection);
        addNewFilterItem(storeFilterList);
    }

    private void removeNonExistingFilterItem(StoreFilterList storeFilterList, boolean isPreserveSelection) {
        List<StoreFilterHeader> loadedStoreFilterHeaderList = storeFilterList.getStoreFilterHeaders();
        List<String> indexList = new ArrayList<>();
        for (StoreFilterHeader storeFilterHeader :
                loadedStoreFilterHeaderList) {
            indexList.add(storeFilterHeader.getId());
        }

        ListIterator<StoreFilterHeader> it = this.mStoreFilterHeaders.listIterator();
        while (it.hasNext()) {
            StoreFilterHeader storeFilterHeader = it.next();
            if (indexList.contains(storeFilterHeader.getId())) {
                int matchIndex = indexList.indexOf(storeFilterHeader.getId());
                storeFilterHeader.replaceExisting(loadedStoreFilterHeaderList.get(matchIndex), isPreserveSelection);
            } else {
                it.remove();
            }
        }
    }

    private void addNewFilterItem(StoreFilterList storeFilterList) {
        List<String> indexList = new ArrayList<>();
        for (StoreFilterHeader loadedStoreFilterHeader :
                this.mStoreFilterHeaders) {
            indexList.add(loadedStoreFilterHeader.getId());
        }

        for (StoreFilterHeader loadedStoreFilterHeader :
                storeFilterList.getStoreFilterHeaders()) {
            if (!indexList.contains(loadedStoreFilterHeader.getId())) {
                this.mStoreFilterHeaders.add(loadedStoreFilterHeader);
            }
        }
    }

    public boolean isStoreFilterListAvailable() {
        return mStoreFilterHeaders != null && !mStoreFilterHeaders.isEmpty();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void clearAllSelectedFilters() {
        for (StoreFilterHeader storeFilterHeader :
                mStoreFilterHeaders) {
            storeFilterHeader.clearAllSelectedFilterOptions();
        }
    }

    public Map<String, String> getFilterOptionMap() {
        Map<String, String> filterOptionMap = new HashMap<>();
        String storeFilterOption;
        for (StoreFilterHeader storeFilterHeader :
                mStoreFilterHeaders) {
            storeFilterOption = storeFilterHeader.getSelectedStoreFilterOptionIfAvailable();
            if (!TextUtils.isEmpty(storeFilterOption)) {
                filterOptionMap.put(storeFilterHeader.getSlug(), storeFilterOption);
            }
        }

        return filterOptionMap;
    }
}