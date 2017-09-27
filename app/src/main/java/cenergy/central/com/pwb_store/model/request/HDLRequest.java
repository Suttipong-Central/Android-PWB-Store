package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class HDLRequest implements Parcelable {

    @SerializedName("subDistrict")
    @Expose
    private String subDistrict;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("postalCode")
    @Expose
    private String postalCode;
    @SerializedName("period")
    @Expose
    private String period;
    @SerializedName("cartData")
    @Expose
    private List<CartDataRequest> mCartDataRequests = new ArrayList<>();

    public HDLRequest(String subDistrict, String district, String province, String postalCode,
                      String period, List<CartDataRequest> cartDataRequests){
        this.subDistrict = subDistrict;
        this.district = district;
        this.province = province;
        this.postalCode = postalCode;
        this.period = period;
        this.mCartDataRequests = cartDataRequests;

    }

    protected HDLRequest(Parcel in) {
        subDistrict = in.readString();
        district = in.readString();
        province = in.readString();
        postalCode = in.readString();
        period = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subDistrict);
        dest.writeString(district);
        dest.writeString(province);
        dest.writeString(postalCode);
        dest.writeString(period);
    }

    public static final Creator<HDLRequest> CREATOR = new Creator<HDLRequest>() {
        @Override
        public HDLRequest createFromParcel(Parcel in) {
            return new HDLRequest(in);
        }

        @Override
        public HDLRequest[] newArray(int size) {
            return new HDLRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
