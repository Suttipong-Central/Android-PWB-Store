package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/7/2017 AD.
 */

public class ProductDao implements IViewType,Parcelable {

    private int viewTypeId;
    private int noOfElement;
    @SerializedName(value = "TotalItems", alternate = "total_count")
    @Expose
    private int totalElement;
    private int currentPage;
    private int perPage;
    private int totalPage;
    private boolean isLastPage;
    @SerializedName(value = "Products", alternate = "items")
    @Expose
    private List<ProductList> mProductListList = new ArrayList<>();

//    public ProductDao(List<ProductList> productListList, int noOfElement, int totalElement, int currentPage, int perPage, int totalPage, boolean isLastPage) {
//        this.mProductListList = productListList;
//        this.noOfElement = noOfElement;
//        this.totalElement = totalElement;
//        this.currentPage = currentPage;
//        this.perPage = perPage;
//        this.totalPage = totalPage;
//        this.isLastPage = isLastPage;
//    }

    public ProductDao(List<ProductList> productLists){
        this.mProductListList = productLists;
    }

    protected ProductDao(Parcel in) {
        viewTypeId = in.readInt();
        mProductListList = in.createTypedArrayList(ProductList.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mProductListList);
    }


    public static final Creator<ProductDao> CREATOR = new Creator<ProductDao>() {
        @Override
        public ProductDao createFromParcel(Parcel in) {
            return new ProductDao(in);
        }

        @Override
        public ProductDao[] newArray(int size) {
            return new ProductDao[size];
        }
    };

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

    public List<ProductList> getProductListList() {
        return mProductListList;
    }

    public void setProductListList(List<ProductList> productListList) {
        mProductListList = productListList;
    }

    public int getNoOfElement() {
        return noOfElement;
    }

    public void setNoOfElement(int noOfElement) {
        this.noOfElement = noOfElement;
    }

    public int getTotalElement() {
        return totalElement;
    }

    public void setTotalElement(int totalElement) {
        this.totalElement = totalElement;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }
}
