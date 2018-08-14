package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareList implements IViewType,Parcelable {
    private int viewTypeId;
    private int total;
    @SerializedName("items")
    @Expose
    private List<ProductCompareList> mProductCompareLists = new ArrayList<>();
    private CompareDao mCompareDao;

    // TBD- for mockup display product
    private List<CompareProduct> compareProducts = null;

    public static final Creator<CompareList> CREATOR = new Creator<CompareList>() {
        @Override
        public CompareList createFromParcel(Parcel in) {
            return new CompareList(in);
        }

        @Override
        public CompareList[] newArray(int size) {
            return new CompareList[size];
        }
    };

    public CompareList(int total, List<ProductCompareList> productLists, CompareDao compareDao) {
        this.total = total;
        this.mProductCompareLists = productLists;
        this.mCompareDao = compareDao;
    }

    public CompareList(List<CompareProduct> compareProducts,  CompareDao compareDao) {
        this.compareProducts = compareProducts;
        this.mCompareDao = compareDao;
    }

    public CompareList(List<CompareProduct> compareProducts) {
        this.compareProducts = compareProducts;
    }

    protected CompareList(Parcel in) {
        viewTypeId = in.readInt();
        total = in.readInt();
        mProductCompareLists = in.createTypedArrayList(ProductCompareList.CREATOR);
        mCompareDao = in.readParcelable(CompareDao.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(total);
        dest.writeTypedList(mProductCompareLists);
        dest.writeParcelable(mCompareDao, flags);
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

    public List<ProductCompareList> getProductCompareLists() {
        return mProductCompareLists;
    }

    public void setProductCompareLists(List<ProductCompareList> productLists) {
        mProductCompareLists = productLists;
    }

    public CompareDao getCompareDao() {
        return mCompareDao;
    }

    public void setCompareDao(CompareDao compareDao) {
        mCompareDao = compareDao;
    }

    public List<CompareProduct> getCompareProducts() {
        return compareProducts;
    }

    public void setCompareProducts(List<CompareProduct> compareProducts) {
        this.compareProducts = compareProducts;
    }
}
