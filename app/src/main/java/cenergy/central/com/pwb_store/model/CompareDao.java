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

public class CompareDao implements IViewType,Parcelable {

    private int viewTypeId;
    private int total;
    @SerializedName("compare")
    @Expose
    private List<CompareDetail> mCompareDetails = new ArrayList<>();

    public static final Creator<CompareDao> CREATOR = new Creator<CompareDao>() {
        @Override
        public CompareDao createFromParcel(Parcel in) {
            return new CompareDao(in);
        }

        @Override
        public CompareDao[] newArray(int size) {
            return new CompareDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected CompareDao(Parcel in) {
        viewTypeId = in.readInt();
        total = in.readInt();
        mCompareDetails = in.createTypedArrayList(CompareDetail.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(total);
        dest.writeTypedList(mCompareDetails);
    }

    public CompareDao(int total, List<CompareDetail> compareDetails){
        this.total = total;
        this.mCompareDetails = compareDetails;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CompareDetail> getCompareDetails() {
        return mCompareDetails;
    }

    public void setCompareDetails(List<CompareDetail> compareDetails) {
        mCompareDetails = compareDetails;
    }
}
