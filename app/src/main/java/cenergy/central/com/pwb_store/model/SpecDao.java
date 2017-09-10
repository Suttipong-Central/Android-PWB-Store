package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/4/2017 AD.
 */

public class SpecDao implements IViewType,Parcelable {
    private int viewTypeId;
    private List<SpecItem> mSpecItems = new ArrayList<>();

    public static final Creator<SpecDao> CREATOR = new Creator<SpecDao>() {
        @Override
        public SpecDao createFromParcel(Parcel in) {
            return new SpecDao(in);
        }

        @Override
        public SpecDao[] newArray(int size) {
            return new SpecDao[size];
        }
    };

    public SpecDao(List<SpecItem> specItems){
       this.mSpecItems = specItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected SpecDao(Parcel in) {
        viewTypeId = in.readInt();
        mSpecItems = in.createTypedArrayList(SpecItem.CREATOR);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mSpecItems);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<SpecItem> getSpecItems() {
        return mSpecItems;
    }

    public void setSpecItems(List<SpecItem> specItems) {
        mSpecItems = specItems;
    }
}
