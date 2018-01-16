package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class PeriodRequest implements Parcelable {
    @SerializedName("year")
    @Expose
    private int year;
    @SerializedName("month")
    @Expose
    private int month;

    public PeriodRequest(int year, int month){
       this.year = year;
       this.month = month;
    }

    protected PeriodRequest(Parcel in) {
        year = in.readInt();
        month = in.readInt();
    }

    public static final Creator<PeriodRequest> CREATOR = new Creator<PeriodRequest>() {
        @Override
        public PeriodRequest createFromParcel(Parcel in) {
            return new PeriodRequest(in);
        }

        @Override
        public PeriodRequest[] newArray(int size) {
            return new PeriodRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeInt(month);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
