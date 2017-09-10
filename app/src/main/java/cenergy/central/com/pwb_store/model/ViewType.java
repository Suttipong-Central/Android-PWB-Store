package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 6/30/2017 AD.
 */

public class ViewType implements IViewType, Parcelable {
    public static final Creator<ViewType> CREATOR = new Creator<ViewType>() {
        @Override
        public ViewType createFromParcel(Parcel in) {
            return new ViewType(in);
        }

        @Override
        public ViewType[] newArray(int size) {
            return new ViewType[size];
        }
    };
    // Data members
    private int mViewType = -1;

    public ViewType(int mViewType) {
        this.mViewType = mViewType;
    }

    protected ViewType(Parcel in) {
        mViewType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mViewType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int getViewTypeId() {
        return mViewType;
    }

    @Override
    public void setViewTypeId(int id) {
        mViewType = id;
    }
}
