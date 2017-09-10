package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class SearchHistoryItem implements IViewType,Parcelable {
    private transient int viewTypeId;
    private String searchQuery;

    public SearchHistoryItem(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    protected SearchHistoryItem(Parcel in) {
        viewTypeId = in.readInt();
        searchQuery = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(searchQuery);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchHistoryItem> CREATOR = new Creator<SearchHistoryItem>() {
        @Override
        public SearchHistoryItem createFromParcel(Parcel in) {
            return new SearchHistoryItem(in);
        }

        @Override
        public SearchHistoryItem[] newArray(int size) {
            return new SearchHistoryItem[size];
        }
    };

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}
