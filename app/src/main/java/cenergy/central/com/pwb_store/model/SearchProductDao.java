package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchProductDao extends ProductDao implements IViewType, Parcelable {
    private transient int viewTypeId;
//    @SerializedName("query")
//    @Expose
    private String query;
//    @SerializedName("suggestion_word")
//    @Expose
    private SearchSuggestion searchSuggestion;

//    public SearchProductDao(List<ProductList> productList, int noOfElement, int totalElement, int currentPage, int perPage, int totalPage, boolean isLastPage, String query, SearchSuggestion searchSuggestion) {
//        super(productList, noOfElement, totalElement, currentPage, perPage, totalPage, isLastPage);
//        this.query = query;
//        this.searchSuggestion = searchSuggestion;
//    }

    protected SearchProductDao(Parcel in) {
        super(in);
        query = in.readString();
        searchSuggestion = in.readParcelable(SearchSuggestion.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(query);
        dest.writeParcelable(searchSuggestion, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchProductDao> CREATOR = new Creator<SearchProductDao>() {
        @Override
        public SearchProductDao createFromParcel(Parcel in) {
            return new SearchProductDao(in);
        }

        @Override
        public SearchProductDao[] newArray(int size) {
            return new SearchProductDao[size];
        }
    };

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SearchSuggestion getSearchSuggestion() {
        return searchSuggestion;
    }

    public void setSearchSuggestion(SearchSuggestion searchSuggestion) {
        this.searchSuggestion = searchSuggestion;
    }

    public boolean isSuggestionExist() {
        return searchSuggestion != null && searchSuggestion.getTotal() > 0;
    }

    public boolean isProductListExist() {
        return getProductListList() != null && getProductListList().size() > 0;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }
}
