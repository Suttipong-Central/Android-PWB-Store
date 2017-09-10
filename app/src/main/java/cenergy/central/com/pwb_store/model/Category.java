package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 7/5/2017 AD.
 */

public class Category implements IViewType, Parcelable {
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
    @SerializedName("DepartmentName")
    @Expose
    private String departmentName;
    @SerializedName("DepartmentNameEN")
    @Expose
    private String departmentNameEN;
    @SerializedName("MetaKeyword")
    @Expose
    private String metaKeyword;
    @SerializedName("MetaDescription")
    @Expose
    private String metaDescription;
    @SerializedName("UrlName")
    @Expose
    private String urlName;
    @SerializedName("UrlNameEN")
    @Expose
    private String urlNameEN;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("TitleEN")
    @Expose
    private String titleEN;
    @SerializedName("ImageUrl")
    @Expose
    private String imageURL;
    @SerializedName("Imagerl")
    @Expose
    private String imagerl;

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
        this.title = title;
    }

    protected Category(Parcel in) {
        viewTypeId = in.readInt();
        imageURL = in.readString();
        title = in.readString();
        departmentId = in.readInt();
        parentId = in.readInt();
        rootDeptId = in.readInt();
        departmentName = in.readString();
        departmentNameEN = in.readString();
        metaKeyword = in.readString();
        metaDescription = in.readString();
        urlName = in.readString();
        urlNameEN = in.readString();
        titleEN = in.readString();
        imagerl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(imageURL);
        dest.writeString(title);
        dest.writeInt(departmentId);
        dest.writeInt(parentId);
        dest.writeInt(rootDeptId);
        dest.writeString(departmentName);
        dest.writeString(departmentNameEN);
        dest.writeString(metaKeyword);
        dest.writeString(metaDescription);
        dest.writeString(urlName);
        dest.writeString(urlNameEN);
        dest.writeString(titleEN);
        dest.writeString(imagerl);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentNameEN() {
        return departmentNameEN;
    }

    public void setDepartmentNameEN(String departmentNameEN) {
        this.departmentNameEN = departmentNameEN;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getUrlNameEN() {
        return urlNameEN;
    }

    public void setUrlNameEN(String urlNameEN) {
        this.urlNameEN = urlNameEN;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getImagerl() {
        return imagerl;
    }

    public void setImagerl(String imagerl) {
        this.imagerl = imagerl;
    }
}
