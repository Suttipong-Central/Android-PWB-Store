package cenergy.central.com.pwb_store.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class CancelTokenResponse implements Parcelable {
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;

    protected CancelTokenResponse(Parcel in) {
        errorMessage = in.readString();
        errorCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorMessage);
        dest.writeString(errorCode);
    }

    public static final Creator<CancelTokenResponse> CREATOR = new Creator<CancelTokenResponse>() {
        @Override
        public CancelTokenResponse createFromParcel(Parcel in) {
            return new CancelTokenResponse(in);
        }

        @Override
        public CancelTokenResponse[] newArray(int size) {
            return new CancelTokenResponse[size];
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
}
