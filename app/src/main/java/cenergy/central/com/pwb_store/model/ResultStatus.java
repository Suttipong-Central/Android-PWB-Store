package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class ResultStatus implements Parcelable {

    @SerializedName("token")
    @Expose
    private String tokenData;
    @SerializedName("expireDate")
    @Expose
    private String tokenExpireDate;

    public static final Creator<ResultStatus> CREATOR = new Creator<ResultStatus>() {
        @Override
        public ResultStatus createFromParcel(Parcel in) {
            return new ResultStatus(in);
        }

        @Override
        public ResultStatus[] newArray(int size) {
            return new ResultStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected ResultStatus(Parcel in) {
        tokenData = in.readString();
        tokenExpireDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tokenData);
        dest.writeString(tokenExpireDate);
    }

    public String getTokenData() {
        return tokenData;
    }

    public void setTokenData(String tokenData) {
        this.tokenData = tokenData;
    }

    public String getTokenExpireDate() {
        return tokenExpireDate;
    }

    public void setTokenExpireDate(String tokenExpireDate) {
        this.tokenExpireDate = tokenExpireDate;
    }
}
