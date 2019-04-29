package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cenergy.central.com.pwb_store.Constants;
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
    @Expose
    private String imageURL;
    @SerializedName("include_in_menu")
    @Expose
    private int includeInMenu;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("is_active")
    @Expose
    private boolean isActive;
    @Expose
    private int position;
    @Expose
    private String children;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @Expose
    private String path;

    public Category () {

    }

    protected Category(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        level = in.readString();
        departmentName = in.readString();
        imageURL = in.readString();
        includeInMenu = in.readInt();
        parentId = in.readString();
        isActive = in.readInt() == 1;
        position = in.readInt();
        children = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        path = in.readString();
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
        dest.writeInt(includeInMenu);
        dest.writeString(parentId);
        dest.writeInt(isActive ? 1 : 0);
        dest.writeInt(position);
        dest.writeString(children);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(path);
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
        return Constants.BASE_URL_MAGENTO + "/media/catalog/category/" + imageURL;
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

    public int getIncludeInMenu() {
        return includeInMenu;
    }

    public boolean IsIncludeInMenu() {
        return includeInMenu == 1;
    }

    public void setIncludeInMenu(int includeInMenu) {
        this.includeInMenu = includeInMenu;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
