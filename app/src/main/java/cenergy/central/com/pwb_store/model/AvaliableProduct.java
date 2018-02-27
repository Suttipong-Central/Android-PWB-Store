package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 20/2/2018 AD.
 */

public class AvaliableProduct implements IViewType, Parcelable{
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("stores")
    @Expose
    private List<AvaliableStoreItem> mAvaliableStoreItems = new ArrayList<>();
    private int viewTypeId;

    protected AvaliableProduct(Parcel in) {
        sku = in.readString();
        mAvaliableStoreItems = in.createTypedArrayList(AvaliableStoreItem.CREATOR);
        viewTypeId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sku);
        dest.writeTypedList(mAvaliableStoreItems);
        dest.writeInt(viewTypeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AvaliableProduct> CREATOR = new Creator<AvaliableProduct>() {
        @Override
        public AvaliableProduct createFromParcel(Parcel in) {
            return new AvaliableProduct(in);
        }

        @Override
        public AvaliableProduct[] newArray(int size) {
            return new AvaliableProduct[size];
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<AvaliableStoreItem> getAvaliableStoreItems() {
        return mAvaliableStoreItems;
    }

    public void setAvaliableStoreItems(List<AvaliableStoreItem> avaliableStoreItems) {
        mAvaliableStoreItems = avaliableStoreItems;
    }
}
