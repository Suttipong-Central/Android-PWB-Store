package cenergy.central.com.pwb_store.model;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cenergy.central.com.pwb_store.model.response.ErrorParameter;
import cenergy.central.com.pwb_store.utils.ApiException;
import cenergy.central.com.pwb_store.utils.NetworkNotConnection;

/**
 * Created by napabhat on 9/6/2017 AD.
 */

public class APIError {

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_FOUND = 404;

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
    @SerializedName("parameters")
    @Expose
    private ErrorParameter errorParameter;

    /**
     * No args constructor for use in serialization
     */
    public APIError() {
    }

    public APIError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
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
            errorParameter = error.getErrorParameter();
            errorCode = String.valueOf(error.getCode());
            errorMessage = error.getMessage();
            errorUserMessage = error.getLocalizedMessage();
        } else if (e instanceof NetworkNotConnection) {
            errorCode = null;
            errorMessage = e.getMessage();
            errorUserMessage = e.getLocalizedMessage();
        } else {
            errorCode = "-1";
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

    public void setError(String error) {
        this.error = error;
    }

    public ErrorParameter getErrorParameter() {
        return errorParameter;
    }

    public void setErrorParameter(ErrorParameter errorParameter) {
        this.errorParameter = errorParameter;
    }
}
