package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class ShippingRequest implements Parcelable {
    @SerializedName("postalId")
    @Expose
    private String postalId;
    @SerializedName("skus")
    @Expose
    private List<SkuDataRequest> mSkuDataRequests = new ArrayList<>();
    @SerializedName("period")
    @Expose
    private PeriodRequest mPeriodRequest;
    @SerializedName("customDetail")
    @Expose
    private CustomDetailRequest mCustomDetailRequest;

    public ShippingRequest(String postalId, List<SkuDataRequest> skuDataRequests, PeriodRequest periodRequest, CustomDetailRequest customDetailRequest){
        this.postalId = postalId;
        this.mSkuDataRequests = skuDataRequests;
        this.mPeriodRequest = periodRequest;
        this.mCustomDetailRequest = customDetailRequest;
    }

    protected ShippingRequest(Parcel in) {
        postalId = in.readString();
        mSkuDataRequests = in.createTypedArrayList(SkuDataRequest.CREATOR);
        mPeriodRequest = in.readParcelable(PeriodRequest.class.getClassLoader());
        mCustomDetailRequest = in.readParcelable(CustomDetailRequest.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postalId);
        parcel.writeTypedList(mSkuDataRequests);
        parcel.writeParcelable(mPeriodRequest, i);
        parcel.writeParcelable(mCustomDetailRequest, i);
    }

    public static final Creator<ShippingRequest> CREATOR = new Creator<ShippingRequest>() {
        @Override
        public ShippingRequest createFromParcel(Parcel in) {
            return new ShippingRequest(in);
        }

        @Override
        public ShippingRequest[] newArray(int size) {
            return new ShippingRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPostalId() {
        return postalId;
    }

    public void setPostalId(String postalId) {
        this.postalId = postalId;
    }

    public List<SkuDataRequest> getSkuDataRequests() {
        return mSkuDataRequests;
    }

    public void setSkuDataRequests(List<SkuDataRequest> skuDataRequests) {
        mSkuDataRequests = skuDataRequests;
    }

    public PeriodRequest getPeriodRequest() {
        return mPeriodRequest;
    }

    public void setPeriodRequest(PeriodRequest periodRequest) {
        mPeriodRequest = periodRequest;
    }

    public CustomDetailRequest getCustomDetailRequest() {
        return mCustomDetailRequest;
    }

    public void setCustomDetailRequest(CustomDetailRequest customDetailRequest) {
        mCustomDetailRequest = customDetailRequest;
    }
}
