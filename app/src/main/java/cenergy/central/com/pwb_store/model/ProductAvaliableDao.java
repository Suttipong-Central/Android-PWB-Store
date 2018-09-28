package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 15/1/2018 AD.
 */

public class ProductAvaliableDao implements IViewType, Parcelable {
    private int viewTypeId;
    @SerializedName("products")
    private List<AvailableStoreDao> mAvailableStoreDaos = new ArrayList<>();
    protected ProductAvaliableDao(Parcel in) {
        viewTypeId = in.readInt();
        mAvailableStoreDaos = in.createTypedArrayList(AvailableStoreDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mAvailableStoreDaos);
    }

    public static final Creator<ProductAvaliableDao> CREATOR = new Creator<ProductAvaliableDao>() {
        @Override
        public ProductAvaliableDao createFromParcel(Parcel in) {
            return new ProductAvaliableDao(in);
        }

        @Override
        public ProductAvaliableDao[] newArray(int size) {
            return new ProductAvaliableDao[size];
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

    public List<AvailableStoreDao> getAvaliableStoreDaos() {
        return mAvailableStoreDaos;
    }

    public void setAvaliableStoreDaos(List<AvailableStoreDao> availableStoreDaos) {
        mAvailableStoreDaos = availableStoreDaos;
    }
}
