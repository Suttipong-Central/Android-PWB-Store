package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class CustomDetailRequest implements Parcelable {
    @SerializedName("deliveryType")
    @Expose
    private String deliveryType;
    @SerializedName("deliveryToStore")
    @Expose
    private String deliveryToStore;
    @SerializedName("deliveryByStore")
    @Expose
    private String deliveryByStore;

    protected CustomDetailRequest(Parcel in) {
        deliveryType = in.readString();
        deliveryByStore = in.readString();
        deliveryToStore = in.readString();
    }

    public CustomDetailRequest(String deliveryType, String deliveryToStore, String deliveryByStore){
        this.deliveryType = deliveryType;
        this.deliveryToStore = deliveryToStore;
        this.deliveryByStore = deliveryByStore;
    }

    public static final Creator<CustomDetailRequest> CREATOR = new Creator<CustomDetailRequest>() {
        @Override
        public CustomDetailRequest createFromParcel(Parcel in) {
            return new CustomDetailRequest(in);
        }

        @Override
        public CustomDetailRequest[] newArray(int size) {
            return new CustomDetailRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(deliveryType);
        parcel.writeString(deliveryToStore);
        parcel.writeString(deliveryByStore);
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryToStore() {
        return deliveryToStore;
    }

    public void setDeliveryToStore(String deliveryToStore) {
        this.deliveryToStore = deliveryToStore;
    }

    public String getDeliveryByStore() {
        return deliveryByStore;
    }

    public void setDeliveryByStore(String deliveryByStore) {
        this.deliveryByStore = deliveryByStore;
    }
}
