package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/31/2017 AD.
 */

public class CompareDetail implements IViewType,Parcelable {

    private int viewTypeId;
    @SerializedName("compareHeader")
    @Expose
    private String name;
    @SerializedName("compareItem")
    @Expose
    private List<CompareDetailItem> mCompareDetailItems = new ArrayList<>();

    public static final Creator<CompareDetail> CREATOR = new Creator<CompareDetail>() {
        @Override
        public CompareDetail createFromParcel(Parcel in) {
            return new CompareDetail(in);
        }

        @Override
        public CompareDetail[] newArray(int size) {
            return new CompareDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected CompareDetail(Parcel in) {
        viewTypeId = in.readInt();
        mCompareDetailItems = in.createTypedArrayList(CompareDetailItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mCompareDetailItems);
    }

    public CompareDetail(String name, List<CompareDetailItem> compareDetailItems){
        this.name = name;
        this.mCompareDetailItems = compareDetailItems;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CompareDetailItem> getCompareDetailItems() {
        return mCompareDetailItems;
    }

    public void setCompareDetailItems(List<CompareDetailItem> compareDetailItems) {
        mCompareDetailItems = compareDetailItems;
    }
}
