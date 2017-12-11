package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 11/12/2017 AD.
 */

public class StaffLogin implements Parcelable {
    private String userId;
    private String password;

    protected StaffLogin(Parcel in) {
        userId = in.readString();
        password = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StaffLogin> CREATOR = new Creator<StaffLogin>() {
        @Override
        public StaffLogin createFromParcel(Parcel in) {
            return new StaffLogin(in);
        }

        @Override
        public StaffLogin[] newArray(int size) {
            return new StaffLogin[size];
        }
    };

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
}
