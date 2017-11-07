package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 11/2/2017 AD.
 */

public class ProductDetailDao implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("items")
    @Expose
    private List<ProductDetail> mProductDetails = new ArrayList<>();

    protected ProductDetailDao(Parcel in) {
        viewTypeId = in.readInt();
        mProductDetails = in.createTypedArrayList(ProductDetail.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mProductDetails);
    }

    public static final Creator<ProductDetailDao> CREATOR = new Creator<ProductDetailDao>() {
        @Override
        public ProductDetailDao createFromParcel(Parcel in) {
            return new ProductDetailDao(in);
        }

        @Override
        public ProductDetailDao[] newArray(int size) {
            return new ProductDetailDao[size];
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

    public List<ProductDetail> getProductDetails() {
        return mProductDetails;
    }

    public void setProductDetails(List<ProductDetail> productDetails) {
        mProductDetails = productDetails;
    }
}
