package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 7/19/2017 AD.
 */

public class CompareCount implements Parcelable {

    public static final Creator<CompareCount> CREATOR = new Creator<CompareCount>() {
        @Override
        public CompareCount createFromParcel(Parcel in) {
            return new CompareCount(in);
        }

        @Override
        public CompareCount[] newArray(int size) {
            return new CompareCount[size];
        }
    };

    //    @SerializedName("id")
//    @Expose
    private String id;
    //    @SerializedName("count")
//    @Expose
    private int count;

    public CompareCount(String id, int count) {
        this.id = id;
        this.count = count;
    }

    protected CompareCount(Parcel in) {
        id = in.readString();
        count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
