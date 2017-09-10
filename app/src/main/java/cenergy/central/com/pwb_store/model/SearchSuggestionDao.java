package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SearchSuggestionDao implements Parcelable {
//    @SerializedName("query")
//    @Expose
    private String query;
//    @SerializedName("suggestion")
//    @Expose
    private SearchSuggestion searchSuggestion;
//    @SerializedName("category")
//    @Expose
    private SearchSuggestionCategory searchSuggestionCategory;
//    @SerializedName("product_list")
//    @Expose
    private SearchSuggestionProduct searchSuggestionProduct;

    public SearchSuggestionDao(String query, SearchSuggestion searchSuggestion, SearchSuggestionCategory searchSuggestionCategory, SearchSuggestionProduct searchSuggestionProduct) {
        this.query = query;
        this.searchSuggestion = searchSuggestion;
        this.searchSuggestionCategory = searchSuggestionCategory;
        this.searchSuggestionProduct = searchSuggestionProduct;
    }

    protected SearchSuggestionDao(Parcel in) {
        query = in.readString();
        searchSuggestion = in.readParcelable(SearchSuggestion.class.getClassLoader());
        searchSuggestionCategory = in.readParcelable(SearchSuggestionCategory.class.getClassLoader());
        searchSuggestionProduct = in.readParcelable(SearchSuggestionProduct.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeParcelable(searchSuggestion, flags);
        dest.writeParcelable(searchSuggestionCategory, flags);
        dest.writeParcelable(searchSuggestionProduct, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestionDao> CREATOR = new Creator<SearchSuggestionDao>() {
        @Override
        public SearchSuggestionDao createFromParcel(Parcel in) {
            return new SearchSuggestionDao(in);
        }

        @Override
        public SearchSuggestionDao[] newArray(int size) {
            return new SearchSuggestionDao[size];
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

    public SearchSuggestionCategory getSearchSuggestionCategory() {
        return searchSuggestionCategory;
    }

    public void setSearchSuggestionCategory(SearchSuggestionCategory searchSuggestionCategory) {
        this.searchSuggestionCategory = searchSuggestionCategory;
    }

    public boolean isSearchSuggestionCategoryAvailable() {
        return searchSuggestionCategory != null && !searchSuggestionCategory.getItems().isEmpty();
    }

    public SearchSuggestionProduct getSearchSuggestionProduct() {
        return searchSuggestionProduct;
    }

    public void setSearchSuggestionProduct(SearchSuggestionProduct searchSuggestionProduct) {
        this.searchSuggestionProduct = searchSuggestionProduct;
    }

    public boolean isSearchSuggestionAvailable() {
        return searchSuggestion != null && searchSuggestion.getTotal() > 0;
    }

    public boolean isSearchSuggestionProductAvailable() {
        return searchSuggestionProduct != null && searchSuggestionProduct.getTotal() > 0;
    }

    public boolean isDataAvailable() {
        return (searchSuggestion != null && searchSuggestion.getTotal() > 0)
                || (searchSuggestionCategory != null && searchSuggestionCategory.getTotal() > 0)
                || (searchSuggestionProduct != null && searchSuggestionProduct.getTotal() > 0);
    }
}
