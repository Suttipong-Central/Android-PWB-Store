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

public class Shipping implements IViewType, Parcelable {
    private int viewTypeId;
    @SerializedName("shippingDate")
    @Expose
    private String shippingDate;
    @SerializedName("slot")
    @Expose
    private List<ShippingItem> mShippingItems = new ArrayList<>();

    protected Shipping(Parcel in) {
        viewTypeId = in.readInt();
        shippingDate = in.readString();
        mShippingItems = in.createTypedArrayList(ShippingItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(shippingDate);
        dest.writeTypedList(mShippingItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Shipping> CREATOR = new Creator<Shipping>() {
        @Override
        public Shipping createFromParcel(Parcel in) {
            return new Shipping(in);
        }

        @Override
        public Shipping[] newArray(int size) {
            return new Shipping[size];
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

    public String getShippingDate() {
        return shippingDate == null ? "" : shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public List<ShippingItem> getShippingItems() {
        return mShippingItems;
    }

    public void setShippingItems(List<ShippingItem> shippingItems) {
        mShippingItems = shippingItems;
    }
}
