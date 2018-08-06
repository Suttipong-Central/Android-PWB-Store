package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class Category implements IViewType, Parcelable {
    private int viewTypeId;
    private String id;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("name")
    @Expose
    private String departmentName;
    @SerializedName("image")
    @Expose
    private String imageURL;
    @SerializedName("children_data")
    @Expose
    private List<ProductFilterHeader> mFilterHeaders = new ArrayList<>();

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Category(String imageURL, String title) {
        this.imageURL = imageURL;
        this.departmentName = title;
    }

    protected Category(Parcel in) {
        viewTypeId = in.readInt();
        imageURL = in.readString();
        id = in.readString();
        level = in.readString();
        departmentName = in.readString();
        mFilterHeaders = in.createTypedArrayList(ProductFilterHeader.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(imageURL);
        dest.writeString(departmentName);
        dest.writeString(id);
        dest.writeString(level);
        dest.writeTypedList(mFilterHeaders);
    }

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<ProductFilterHeader> getFilterHeaders() {
        return mFilterHeaders;
    }

    public void setFilterHeaders(List<ProductFilterHeader> filterHeaders) {
        mFilterHeaders = filterHeaders;
    }
}
