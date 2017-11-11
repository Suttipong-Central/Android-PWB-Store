package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 11/8/2017 AD.
 */

public class ExtensionCompare implements IViewType,Parcelable {
    private int viewTypeId;


    @SerializedName(value = "MediumFullUrl", alternate = "image")
    @Expose
    private String imageUrl;

    @SerializedName(value = "Description" , alternate = "description")
    @Expose
    private String description;
    @SerializedName("by_store")
    @Expose
    private List<CompareListStore> mCompareListStores = new ArrayList<>();

    protected ExtensionCompare(Parcel in) {
        viewTypeId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CompareListStore> getCompareListStores() {
        return mCompareListStores;
    }

    public void setCompareListStores(List<CompareListStore> compareListStores) {
        mCompareListStores = compareListStores;
    }
}
