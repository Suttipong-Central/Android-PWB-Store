package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import cenergy.central.com.pwb_store.adapter.viewholder.SearchSuggestionHeaderViewHolder;


public class SearchSuggestionHeader implements IViewType, Parcelable {
    private transient int viewTypeId;
    private String searchQuery;
    private SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType searchSuggestionHeaderType;

    public SearchSuggestionHeader(String searchQuery, SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType searchSuggestionHeaderType) {
        this.searchQuery = searchQuery;
        this.searchSuggestionHeaderType = searchSuggestionHeaderType;
    }

    protected SearchSuggestionHeader(Parcel in) {
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

    public static final Creator<SearchSuggestionHeader> CREATOR = new Creator<SearchSuggestionHeader>() {
        @Override
        public SearchSuggestionHeader createFromParcel(Parcel in) {
            return new SearchSuggestionHeader(in);
        }

        @Override
        public SearchSuggestionHeader[] newArray(int size) {
            return new SearchSuggestionHeader[size];
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

    public SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType getSearchSuggestionHeaderType() {
        return searchSuggestionHeaderType;
    }

    public void setSearchSuggestionHeaderType(SearchSuggestionHeaderViewHolder.SearchSuggestionHeaderType searchSuggestionHeaderType) {
        this.searchSuggestionHeaderType = searchSuggestionHeaderType;
    }
}
