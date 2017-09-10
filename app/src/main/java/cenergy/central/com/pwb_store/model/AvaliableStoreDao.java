package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 8/16/2017 AD.
 */

public class AvaliableStoreDao implements IViewType, Parcelable {
    private int viewTypeId;
    private List<AvaliableStoreItem> mAvaliableStoreItems = new ArrayList<>();

    public AvaliableStoreDao(List<AvaliableStoreItem> avaliableStoreItems) {
        this.mAvaliableStoreItems = avaliableStoreItems;
    }

    protected AvaliableStoreDao(Parcel in) {
        viewTypeId = in.readInt();
        mAvaliableStoreItems = in.createTypedArrayList(AvaliableStoreItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mAvaliableStoreItems);
    }

    public static final Creator<AvaliableStoreDao> CREATOR = new Creator<AvaliableStoreDao>() {
        @Override
        public AvaliableStoreDao createFromParcel(Parcel in) {
            return new AvaliableStoreDao(in);
        }

        @Override
        public AvaliableStoreDao[] newArray(int size) {
            return new AvaliableStoreDao[size];
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

    public List<AvaliableStoreItem> getAvaliableStoreItems() {
        return mAvaliableStoreItems;
    }

    public void setAvaliableStoreItems(List<AvaliableStoreItem> avaliableStoreItems) {
        mAvaliableStoreItems = avaliableStoreItems;
    }
}
