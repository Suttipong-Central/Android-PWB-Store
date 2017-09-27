package cenergy.central.com.pwb_store.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cenergy.central.com.pwb_store.model.ResultCheckToken;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class CheckTokenResponse implements Parcelable {
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("data")
    @Expose
    private ResultCheckToken mResultCheckToken;

    protected CheckTokenResponse(Parcel in) {
        errorMessage = in.readString();
        errorCode = in.readString();
        mResultCheckToken = in.readParcelable(ResultCheckToken.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorMessage);
        dest.writeString(errorCode);
        dest.writeParcelable(mResultCheckToken, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CheckTokenResponse> CREATOR = new Creator<CheckTokenResponse>() {
        @Override
        public CheckTokenResponse createFromParcel(Parcel in) {
            return new CheckTokenResponse(in);
        }

        @Override
        public CheckTokenResponse[] newArray(int size) {
            return new CheckTokenResponse[size];
        }
    };

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

    public ResultCheckToken getResultCheckToken() {
        return mResultCheckToken;
    }

    public void setResultCheckToken(ResultCheckToken resultCheckToken) {
        mResultCheckToken = resultCheckToken;
    }
}
