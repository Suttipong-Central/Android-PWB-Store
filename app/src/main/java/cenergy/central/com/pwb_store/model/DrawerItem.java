package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerItem implements IViewType,Parcelable{
    //private int icon;
    @SerializedName("DepartmentName")
    @Expose
    private String title;
    private int viewTypeId;
    @SerializedName("DepartmentId")
    @Expose
    private int departmentId;
    @SerializedName("ParentId")
    @Expose
    private int parentId;
    @SerializedName("RootDeptId")
    @Expose
    private int rootDeptId;
    @SerializedName("DepartmentNameEN")
    @Expose
    private String departmentNameEN;

    protected DrawerItem(Parcel in) {
        //icon = in.readInt();
        title = in.readString();
        departmentId = in.readInt();
        parentId = in.readInt();
        rootDeptId = in.readInt();
        departmentNameEN = in.readString();
    }

    public DrawerItem(String title, int departmentId, int parentId, int rootDeptId, String departmentNameEN) {
        //this.icon = icon;
        this.title = title;
        this.departmentId = departmentId;
        this.parentId = parentId;
        this.rootDeptId = rootDeptId;
        this.departmentNameEN = departmentNameEN;
        //this.mStoreLists = storeLists;
    }

    public static final Creator<DrawerItem> CREATOR = new Creator<DrawerItem>() {
        @Override
        public DrawerItem createFromParcel(Parcel in) {
            return new DrawerItem(in);
        }

        @Override
        public DrawerItem[] newArray(int size) {
            return new DrawerItem[size];
        }
    };

//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(icon);
        dest.writeString(title);
        dest.writeInt(departmentId);
        dest.writeInt(parentId);
        dest.writeInt(rootDeptId);
        dest.writeString(departmentNameEN);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getRootDeptId() {
        return rootDeptId;
    }

    public void setRootDeptId(int rootDeptId) {
        this.rootDeptId = rootDeptId;
    }

    public String getDepartmentNameEN() {
        return departmentNameEN;
    }

    public void setDepartmentNameEN(String departmentNameEN) {
        this.departmentNameEN = departmentNameEN;
    }
}
