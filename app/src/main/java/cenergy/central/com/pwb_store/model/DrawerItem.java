package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 6/29/2017 AD.
 */

public class DrawerItem implements IViewType, Parcelable{
    //private int icon;
    private int viewTypeId;
    @SerializedName("name")
    @Expose
    private String title;
    @SerializedName("entity_id")
    @Expose
    private String id;
    private ProductFilterHeader productFilterHeader;


//    public DrawerItem(String title, int departmentId, int parentId, int rootDeptId, String departmentNameEN) {
//        //this.icon = icon;
//        this.title = title;
//        this.departmentId = departmentId;
//        this.parentId = parentId;
//        this.rootDeptId = rootDeptId;
//        this.departmentNameEN = departmentNameEN;
//        //this.mStoreLists = storeLists;
//    }

        public DrawerItem(String title, String id, ProductFilterHeader productFilterHeader) {
        this.title = title;
        this.id = id;
        this.productFilterHeader = productFilterHeader;
    }

//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }

    protected DrawerItem(Parcel in) {
        viewTypeId = in.readInt();
        title = in.readString();
        id = in.readString();
        productFilterHeader = in.readParcelable(ProductFilterHeader.class.getClassLoader());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductFilterHeader getProductFilterHeader() {
        return productFilterHeader;
    }

    public void setProductFilterHeader(ProductFilterHeader productFilterHeader) {
        this.productFilterHeader = productFilterHeader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeParcelable(productFilterHeader, flags);
    }
}
