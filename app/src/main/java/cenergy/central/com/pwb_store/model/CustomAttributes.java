package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 11/1/2017 AD.
 */

public class CustomAttributes implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("attribute_code")
    @Expose
    private String attributeCode;
    @SerializedName("value")
    @Expose
    private String value;

    protected CustomAttributes(Parcel in) {
        attributeCode = in.readString();
        value = in.readString();
        //in.readStringList(value);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attributeCode);
        dest.writeString(value);
       // dest.writeStringList(value);
    }

    public static final Creator<CustomAttributes> CREATOR = new Creator<CustomAttributes>() {
        @Override
        public CustomAttributes createFromParcel(Parcel in) {
            return new CustomAttributes(in);
        }

        @Override
        public CustomAttributes[] newArray(int size) {
            return new CustomAttributes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


//    public List<String> getValue() {
//        return value;
//    }
//
//    public void setValue(List<String> value) {
//        this.value = value;
//    }
}
