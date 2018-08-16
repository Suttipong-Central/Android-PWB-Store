package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class Category extends RealmObject implements IViewType, Parcelable {
    @Ignore
    private int viewTypeId;
    private String id;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("name")
    @Expose
    private String departmentName;
    @SerializedName("category_tablet_icon")
    @Expose
    private String imageURL;
    @SerializedName("include_in_menu")
    @Expose
    private int includeInMenu;
    @SerializedName("children_data")
    @Expose
    private RealmList<ProductFilterHeader> mFilterHeaders;

    public Category () {

    }

    protected Category(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        level = in.readString();
        departmentName = in.readString();
        imageURL = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(level);
        dest.writeString(departmentName);
        dest.writeString(imageURL);
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

    public void setFilterHeaders(RealmList<ProductFilterHeader> filterHeaders) {
        mFilterHeaders = filterHeaders;
    }

    public int getIncludeInMenu() {
        return includeInMenu;
    }

    public boolean IsIncludeInMenu() {
        return includeInMenu == 1;
    }

    public void setIncludeInMenu(int includeInMenu) {
        this.includeInMenu = includeInMenu;
    }
}
