package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class ResultCheckToken implements Parcelable {

    @SerializedName("deviceID")
    @Expose
    private String deviceId;
    @SerializedName("userID")
    @Expose
    private String userId;
    @SerializedName("storeCode")
    @Expose
    private String storeCode;
    @SerializedName("tokenExpireDate")
    private String tokenExpireDate;
    @SerializedName("tokenStatus")
    @Expose
    private String tokenStatus;

    protected ResultCheckToken(Parcel in) {
        deviceId = in.readString();
        userId = in.readString();
        storeCode = in.readString();
        tokenExpireDate = in.readString();
        tokenStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(userId);
        dest.writeString(storeCode);
        dest.writeString(tokenExpireDate);
        dest.writeString(tokenStatus);
    }

    public static final Creator<ResultCheckToken> CREATOR = new Creator<ResultCheckToken>() {
        @Override
        public ResultCheckToken createFromParcel(Parcel in) {
            return new ResultCheckToken(in);
        }

        @Override
        public ResultCheckToken[] newArray(int size) {
            return new ResultCheckToken[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getTokenExpireDate() {
        return tokenExpireDate;
    }

    public void setTokenExpireDate(String tokenExpireDate) {
        this.tokenExpireDate = tokenExpireDate;
    }

    public String getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(String tokenStatus) {
        this.tokenStatus = tokenStatus;
    }
}
