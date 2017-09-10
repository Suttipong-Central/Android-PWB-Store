package cenergy.central.com.pwb_store.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cenergy.central.com.pwb_store.model.ResultStatus;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class TokenResponse implements Parcelable{

    @SerializedName("tokenData")
    @Expose
    private String tokenData;
    @SerializedName("tokenExpireDate")
    @Expose
    private String tokenExpireDate;
    @SerializedName("resultStatus")
    @Expose
    private ResultStatus mResultStatus;

    public static final Creator<TokenResponse> CREATOR = new Creator<TokenResponse>() {
        @Override
        public TokenResponse createFromParcel(Parcel in) {
            return new TokenResponse(in);
        }

        @Override
        public TokenResponse[] newArray(int size) {
            return new TokenResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected TokenResponse(Parcel in) {
        tokenData = in.readString();
        tokenExpireDate = in.readString();
        mResultStatus = in.readParcelable(ResultStatus.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tokenData);
        dest.writeString(tokenExpireDate);
        dest.writeParcelable(mResultStatus, flags);
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

    public ResultStatus getResultStatus() {
        return mResultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        mResultStatus = resultStatus;
    }
}
