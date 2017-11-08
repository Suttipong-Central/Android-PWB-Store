package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 11/8/2017 AD.
 */

public class ExtensionCompare implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("compare")
    @Expose
    private CompareDao mCompareDao;

    protected ExtensionCompare(Parcel in) {
        viewTypeId = in.readInt();
        mCompareDao = in.readParcelable(CompareDao.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeParcelable(mCompareDao, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExtensionCompare> CREATOR = new Creator<ExtensionCompare>() {
        @Override
        public ExtensionCompare createFromParcel(Parcel in) {
            return new ExtensionCompare(in);
        }

        @Override
        public ExtensionCompare[] newArray(int size) {
            return new ExtensionCompare[size];
        }
    };

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public CompareDao getCompareDao() {
        return mCompareDao;
    }

    public void setCompareDao(CompareDao compareDao) {
        mCompareDao = compareDao;
    }
}
