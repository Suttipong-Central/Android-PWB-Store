package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class ShippingDao implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("shippingSlot")
    @Expose
    private List<Shipping> mShippings = new ArrayList<>();
    protected ShippingDao(Parcel in) {
        viewTypeId = in.readInt();
        mShippings = in.createTypedArrayList(Shipping.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mShippings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShippingDao> CREATOR = new Creator<ShippingDao>() {
        @Override
        public ShippingDao createFromParcel(Parcel in) {
            return new ShippingDao(in);
        }

        @Override
        public ShippingDao[] newArray(int size) {
            return new ShippingDao[size];
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

    public List<Shipping> getShippings() {
        return mShippings;
    }

    public void setShippings(List<Shipping> shippings) {
        mShippings = shippings;
    }
}
