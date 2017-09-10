package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class ResultStatus implements Parcelable {
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("errorDesc")
    @Expose
    private String errorDesc;

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
        errorCode = in.readString();
        errorDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorCode);
        dest.writeString(errorDesc);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
