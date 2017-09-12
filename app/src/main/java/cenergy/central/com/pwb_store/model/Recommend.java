package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by napabhat on 7/18/2017 AD.
 */

public class Recommend implements IViewType, Parcelable {

    public static final Creator<Recommend> CREATOR = new Creator<Recommend>() {
        @Override
        public Recommend createFromParcel(Parcel in) {
            return new Recommend(in);
        }

        @Override
        public Recommend[] newArray(int size) {
            return new Recommend[size];
        }
    };

    private int viewTypeId;
    private List<ProductRelatedList> mProductRelatedLists;

    public Recommend(List<ProductRelatedList> productRelatedLists) {
        this.mProductRelatedLists = productRelatedLists;
    }

    protected Recommend(Parcel in) {
        viewTypeId = in.readInt();
        mProductRelatedLists = in.createTypedArrayList(ProductRelatedList.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mProductRelatedLists);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public List<ProductRelatedList> getProductRelatedLists() {
        return mProductRelatedLists;
    }

    public void setProductRelatedLists(List<ProductRelatedList> productRelatedLists) {
        mProductRelatedLists = productRelatedLists;
    }
}
