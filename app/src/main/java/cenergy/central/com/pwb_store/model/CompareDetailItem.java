package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareDetailItem implements IViewType,Parcelable {

    private int viewTypeId;
    @SerializedName("detail")
    @Expose
    private String type;
    public static final Creator<CompareDetailItem> CREATOR = new Creator<CompareDetailItem>() {
        @Override
        public CompareDetailItem createFromParcel(Parcel in) {
            return new CompareDetailItem(in);
        }

        @Override
        public CompareDetailItem[] newArray(int size) {
            return new CompareDetailItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected CompareDetailItem(Parcel in) {
        viewTypeId = in.readInt();
        type = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(type);
    }

    public CompareDetailItem(String type){
        this.type = type;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
