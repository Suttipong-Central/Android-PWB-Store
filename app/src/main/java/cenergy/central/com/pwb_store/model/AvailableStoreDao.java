package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvailableStoreDao implements IViewType, Parcelable {
    private int viewTypeId;
    @SerializedName("products")
    @Expose
    private List<AvaliableProduct> mAvaliableProducts = new ArrayList<>();

    protected AvailableStoreDao(Parcel in) {
        viewTypeId = in.readInt();
        mAvaliableProducts = in.createTypedArrayList(AvaliableProduct.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mAvaliableProducts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AvailableStoreDao> CREATOR = new Creator<AvailableStoreDao>() {
        @Override
        public AvailableStoreDao createFromParcel(Parcel in) {
            return new AvailableStoreDao(in);
        }

        @Override
        public AvailableStoreDao[] newArray(int size) {
            return new AvailableStoreDao[size];
        }
    };

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<AvaliableProduct> getAvaliableProducts() {
        return mAvaliableProducts;
    }

    public void setAvaliableProducts(List<AvaliableProduct> avaliableProducts) {
        mAvaliableProducts = avaliableProducts;
    }
}
