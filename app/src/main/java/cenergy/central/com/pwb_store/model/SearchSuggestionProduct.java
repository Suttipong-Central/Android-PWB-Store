package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionProduct implements Parcelable {
//    @SerializedName("total")
//    @Expose
    private int total;
//    @SerializedName("items")
//    @Expose
    private List<ProductList> productList = new ArrayList<>();

    public SearchSuggestionProduct(int total, List<ProductList> productList) {
        this.total = total;
        this.productList = productList;
    }

    protected SearchSuggestionProduct(Parcel in) {
        total = in.readInt();
        productList = in.createTypedArrayList(ProductList.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total);
        dest.writeTypedList(productList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchSuggestionProduct> CREATOR = new Creator<SearchSuggestionProduct>() {
        @Override
        public SearchSuggestionProduct createFromParcel(Parcel in) {
            return new SearchSuggestionProduct(in);
        }

        @Override
        public SearchSuggestionProduct[] newArray(int size) {
            return new SearchSuggestionProduct[size];
        }
    };

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ProductList> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductList> productList) {
        this.productList = productList;
    }
}
