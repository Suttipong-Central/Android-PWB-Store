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

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("data")
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
//        tokenData = in.readString();
//        tokenExpireDate = in.readString();
        errorMessage = in.readString();
        errorCode = in.readString();
        mResultStatus = in.readParcelable(ResultStatus.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(tokenData);
//        dest.writeString(tokenExpireDate);
        dest.writeString(errorMessage);
        dest.writeString(errorCode);
        dest.writeParcelable(mResultStatus, flags);
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

    public ResultStatus getResultStatus() {
        return mResultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        mResultStatus = resultStatus;
    }
}
