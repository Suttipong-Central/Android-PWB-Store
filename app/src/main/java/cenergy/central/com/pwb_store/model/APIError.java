package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.api.ApiException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 9/6/2017 AD.
 */

public class APIError implements Parcelable {

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_FOUND = 404;

    public static final Creator<APIError> CREATOR = new Creator<APIError>() {
        @Override
        public APIError createFromParcel(Parcel in) {
            return new APIError(in);
        }

        @Override
        public APIError[] newArray(int size) {
            return new APIError[size];
        }
    };

    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("message")
    @Expose
    private String errorMessage;
    @SerializedName("errorMessage")
    @Expose
    private String errorUserMessage;
    @SerializedName("error")
    @Expose
    private String error;

    /**
     * No args constructor for use in serialization
     */
    public APIError() {
    }

    /**
     * @param errorMessage
     * @param errorCode
     * @param errorUserMessage
     */
    public APIError(String errorCode, String errorMessage, String errorUserMessage, String error) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorUserMessage = errorUserMessage;
        this.error = error;
    }

    public APIError(Throwable e) {
        if (e instanceof ApiException) {
            ApiException error = (ApiException) e;

            errorCode = String.valueOf(error.getStatusCode());
            errorMessage = error.getMessage();
            errorUserMessage = error.getLocalizedMessage();
        } else {
            errorMessage = e.getMessage();
            errorUserMessage = e.getLocalizedMessage();
        }
    }

    protected APIError(Parcel in) {
        errorCode = in.readString();
        errorMessage = in.readString();
        errorUserMessage = in.readString();
        error = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(errorCode);
        dest.writeString(errorMessage);
        dest.writeString(errorUserMessage);
        dest.writeString(error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @return The errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode The error_code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return The errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public String getError() {
        return error;
    }

    /**
     * @param errorMessage The error_message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return The errorUserMessage
     */
    public String getErrorUserMessage() {
        return errorUserMessage;
    }

    /**
     * @param errorUserMessage The error_user_message
     */
    public void setErrorUserMessage(String errorUserMessage) {
        this.errorUserMessage = errorUserMessage;
    }
}
