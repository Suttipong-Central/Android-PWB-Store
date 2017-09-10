package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 8/9/2017 AD.
 */

public class CreateTokenRequest implements Parcelable {

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

    protected CreateTokenRequest(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
