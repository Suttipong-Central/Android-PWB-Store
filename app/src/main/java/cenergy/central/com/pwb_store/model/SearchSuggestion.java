package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestion implements Parcelable {
 //   @SerializedName("total")
//    @Expose
    private int total;
//    @SerializedName("items")
//    @Expose
    private List<SearchSuggestionItem> searchSuggestionItemList = new ArrayList<>();

    public SearchSuggestion(int total, List<SearchSuggestionItem> searchSuggestionItemList) {
        this.total = total;
        this.searchSuggestionItemList = searchSuggestionItemList;
    }

    protected SearchSuggestion(Parcel in) {
        total = in.readInt();
        searchSuggestionItemList = in.createTypedArrayList(SearchSuggestionItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeTypedList(searchSuggestionItemList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestion> CREATOR = new Creator<SearchSuggestion>() {
        @Override
        public SearchSuggestion createFromParcel(Parcel in) {
            return new SearchSuggestion(in);
        }

        @Override
        public SearchSuggestion[] newArray(int size) {
            return new SearchSuggestion[size];
        }
    };

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SearchSuggestionItem> getSearchSuggestionItemList() {
        return searchSuggestionItemList;
    }

    public void setSearchSuggestionItemList(List<SearchSuggestionItem> searchSuggestionItemList) {
        this.searchSuggestionItemList = searchSuggestionItemList;
    }
}
