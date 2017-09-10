package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableStoreItem implements IViewType,Parcelable {

    private int viewTypeId;
    private String storeName;
    private String address;
    private int stock;

    public AvaliableStoreItem(String storeName, String address, int stock) {
        this.storeName = storeName;
        this.address = address;
        this.stock = stock;
    }

    protected AvaliableStoreItem(Parcel in) {
        viewTypeId = in.readInt();
        storeName = in.readString();
        address = in.readString();
        stock = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(storeName);
        dest.writeString(address);
        dest.writeInt(stock);
    }

    public static final Creator<AvaliableStoreItem> CREATOR = new Creator<AvaliableStoreItem>() {
        @Override
        public AvaliableStoreItem createFromParcel(Parcel in) {
            return new AvaliableStoreItem(in);
        }

        @Override
        public AvaliableStoreItem[] newArray(int size) {
            return new AvaliableStoreItem[size];
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
