package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/25/2017 AD.
 */

public class SortingList implements IViewType, Parcelable {
    private int viewTypeId;
    private int total;
    private List<SortingHeader> mSortingHeaders = new ArrayList<>();

    public static final Creator<SortingList> CREATOR = new Creator<SortingList>() {
        @Override
        public SortingList createFromParcel(Parcel in) {
            return new SortingList(in);
        }

        @Override
        public SortingList[] newArray(int size) {
            return new SortingList[size];
        }
    };

    public SortingList(List<SortingHeader> sortingHeaders) {
        this.mSortingHeaders = sortingHeaders;
    }

    public SortingList(SortingList sortingList) {
        this.total = sortingList.getTotal();
        List<SortingHeader> sortingHeaders = new ArrayList<>();

        if (sortingList.isSortingListAvailable()) {
            for (SortingHeader sortingHeader :
                    sortingList.getSortingHeaders()) {
                sortingHeaders.add(new SortingHeader(sortingHeader));
            }
        }

        this.mSortingHeaders = sortingHeaders;
    }

    protected SortingList(Parcel in) {
        viewTypeId = in.readInt();
        mSortingHeaders = in.createTypedArrayList(SortingHeader.CREATOR);
        total = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(mSortingHeaders);
        dest.writeInt(total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SortingHeader> getSortingHeaders() {
        return mSortingHeaders;
    }

    public void setSortingHeaders(List<SortingHeader> sortingHeaders) {
        mSortingHeaders = sortingHeaders;
    }

    public void updateSortOption(SortingHeader sortingHeader, boolean isPreserveSelection) {
        SortingHeader sortingHeaders = new SortingHeader("0", sortingHeader.getName(), sortingHeader.getSlug(), "single", sortingHeader.getSortingItems());
        if (isSortingListAvailable()) {
            boolean isFound = false;
            for (SortingHeader sortingHeader1 :
                    mSortingHeaders) {
                if (sortingHeader1.getName().equalsIgnoreCase(sortingHeader.getName())) {
                    sortingHeader1.replaceSortHeader(sortingHeader, isPreserveSelection);
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                mSortingHeaders.add(sortingHeader);
            }
        } else {
            mSortingHeaders = new ArrayList<>();
            mSortingHeaders.add(sortingHeader);
        }
    }

    public boolean isSortingListAvailable() {
        return mSortingHeaders != null && !mSortingHeaders.isEmpty();
    }

}
