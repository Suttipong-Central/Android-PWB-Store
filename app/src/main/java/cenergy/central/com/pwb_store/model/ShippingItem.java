package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class ShippingItem implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("description")
    @Expose
    private String description;

    public ShippingItem(int id, String description){
        this.id = id;
        this.description = description;
    }
    protected ShippingItem(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShippingItem> CREATOR = new Creator<ShippingItem>() {
        @Override
        public ShippingItem createFromParcel(Parcel in) {
            return new ShippingItem(in);
        }

        @Override
        public ShippingItem[] newArray(int size) {
            return new ShippingItem[size];
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
