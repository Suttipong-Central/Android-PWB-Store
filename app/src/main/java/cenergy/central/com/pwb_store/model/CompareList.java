package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/26/2017 AD.
 */

public class CompareList implements IViewType,Parcelable {
    private int viewTypeId;
    private int total;
    private List<ProductList> mProductLists = new ArrayList<>();
    private CompareDao mCompareDao;

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

    public CompareList(int total, List<ProductList> productLists, CompareDao compareDao) {
        this.total = total;
        this.mProductLists = productLists;
        this.mCompareDao = compareDao;
    }

    protected CompareList(Parcel in) {
        viewTypeId = in.readInt();
        total = in.readInt();
        mProductLists = in.createTypedArrayList(ProductList.CREATOR);
        mCompareDao = in.readParcelable(CompareDao.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(total);
        dest.writeTypedList(mProductLists);
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

    public List<ProductList> getProductLists() {
        return mProductLists;
    }

    public void setProductLists(List<ProductList> productLists) {
        mProductLists = productLists;
    }

    public CompareDao getCompareDao() {
        return mCompareDao;
    }

    public void setCompareDao(CompareDao compareDao) {
        mCompareDao = compareDao;
    }
}
