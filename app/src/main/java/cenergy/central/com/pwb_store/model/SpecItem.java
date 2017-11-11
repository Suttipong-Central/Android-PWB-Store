package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/4/2017 AD.
 */

public class SpecItem implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("attribute_code")
    @Expose
    private String header;
    @SerializedName("value")
    @Expose
    private String detail;

    public static final Creator<SpecItem> CREATOR = new Creator<SpecItem>() {
        @Override
        public SpecItem createFromParcel(Parcel in) {
            return new SpecItem(in);
        }

        @Override
        public SpecItem[] newArray(int size) {
            return new SpecItem[size];
        }
    };

    public SpecItem(String header, String detail){
        this.header = header;
        this.detail = detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected SpecItem(Parcel in) {
        viewTypeId = in.readInt();
        header = in.readString();
        detail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(header);
        dest.writeString(detail);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
