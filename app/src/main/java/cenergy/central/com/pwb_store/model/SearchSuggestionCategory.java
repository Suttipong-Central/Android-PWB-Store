package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class SearchSuggestionCategory implements Parcelable {

//    @SerializedName("total")
//    @Expose
    private int total;
//    @SerializedName("items")
//    @Expose
    private List<SearchSuggestionCategoryItem> searchSuggestionCategoryItemList = new ArrayList<>();

    public SearchSuggestionCategory(int total, List<SearchSuggestionCategoryItem> searchSuggestionCategoryItemList) {
        this.total = total;
        this.searchSuggestionCategoryItemList = searchSuggestionCategoryItemList;
    }

    protected SearchSuggestionCategory(Parcel in) {
        total = in.readInt();
        searchSuggestionCategoryItemList = in.createTypedArrayList(SearchSuggestionCategoryItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeTypedList(searchSuggestionCategoryItemList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestionCategory> CREATOR = new Creator<SearchSuggestionCategory>() {
        @Override
        public SearchSuggestionCategory createFromParcel(Parcel in) {
            return new SearchSuggestionCategory(in);
        }

        @Override
        public SearchSuggestionCategory[] newArray(int size) {
            return new SearchSuggestionCategory[size];
        }
    };

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SearchSuggestionCategoryItem> getItems() {
        return searchSuggestionCategoryItemList;
    }

    public void setItems(List<SearchSuggestionCategoryItem> searchSuggestionCategoryItemList) {
        this.searchSuggestionCategoryItemList = searchSuggestionCategoryItemList;
    }
}
