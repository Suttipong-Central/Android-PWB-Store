package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class CreateTokenRequest implements Parcelable {

    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("storeCode")
    @Expose
    private String storeCode;

    public static final Creator<CreateTokenRequest> CREATOR = new Creator<CreateTokenRequest>() {
        @Override
        public CreateTokenRequest createFromParcel(Parcel in) {
            return new CreateTokenRequest(in);
        }

        @Override
        public CreateTokenRequest[] newArray(int size) {
            return new CreateTokenRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public CreateTokenRequest(String deviceId, String userId, String password, String storeCode){
        this.deviceId = deviceId;
        this.userId = userId;
        this.password = password;
        this.storeCode = storeCode;
    }

    protected CreateTokenRequest(Parcel in) {
        deviceId = in.readString();
        userId = in.readString();
        password = in.readString();
        storeCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceId);
        dest.writeString(userId);
        dest.writeString(password);
        dest.writeString(storeCode);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }
}
