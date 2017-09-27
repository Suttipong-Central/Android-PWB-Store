package cenergy.central.com.pwb_store.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import cenergy.central.com.pwb_store.model.Delivery;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class HDLResponse implements Parcelable{
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("data")
    @Expose
    private List<Delivery> mDeliveryList = new ArrayList<>();
    protected HDLResponse(Parcel in) {
        errorMessage = in.readString();
        errorCode = in.readString();
        mDeliveryList = in.createTypedArrayList(Delivery.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorMessage);
        dest.writeString(errorCode);
        dest.writeTypedList(mDeliveryList);
    }

    public static final Creator<HDLResponse> CREATOR = new Creator<HDLResponse>() {
        @Override
        public HDLResponse createFromParcel(Parcel in) {
            return new HDLResponse(in);
        }

        @Override
        public HDLResponse[] newArray(int size) {
            return new HDLResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<Delivery> getDeliveryList() {
        return mDeliveryList;
    }

    public void setDeliveryList(List<Delivery> deliveryList) {
        mDeliveryList = deliveryList;
    }
}
