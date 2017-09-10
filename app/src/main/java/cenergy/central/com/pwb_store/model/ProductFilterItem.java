package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterItem implements IViewType, Parcelable {
    public static final Creator<ProductFilterItem> CREATOR = new Creator<ProductFilterItem>() {
        @Override
        public ProductFilterItem createFromParcel(Parcel in) {
            return new ProductFilterItem(in);
        }

        @Override
        public ProductFilterItem[] newArray(int size) {
            return new ProductFilterItem[size];
        }
    };
    private static final String TAG = "ProductFilterItem";
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
    private String filterName;
    @SerializedName("DepartmentNameEN")
    @Expose
    private String filterNameEN;
    @SerializedName("MetaKeyword")
    @Expose
    private String slug;

    private String value;

    private boolean isSelected;

    private int filterId;

//    public ProductFilterItem(int filterId, String filterName, String slug, String value, int parentId, boolean isSelected){
//        this.filterId = filterId;
//        this.filterName = filterName;
//        this.slug = slug;
//        this.value = value;
//        this.parentId = parentId;
//        this.isSelected = isSelected;
//    }

    public ProductFilterItem(ProductFilterItem productFilterItem) {
        this.filterId = productFilterItem.getFilterId();
        this.filterName = productFilterItem.getFilterName();
        this.slug = productFilterItem.getSlug();
        this.isSelected = productFilterItem.isSelected();
        this.filterNameEN = productFilterItem.getFilterNameEN();
        this.parentId = productFilterItem.getParentId();
        this.departmentId = productFilterItem.getDepartmentId();
    }

    protected ProductFilterItem(Parcel in) {
        viewTypeId = in.readInt();
        filterId = in.readInt();
        filterName = in.readString();
        slug = in.readString();
        parentId = in.readInt();
        isSelected = in.readByte() != 0;
        value = in.readString();
        departmentId = in.readInt();
        rootDeptId = in.readInt();
        filterNameEN = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(filterId);
        dest.writeString(filterName);
        dest.writeString(slug);
        dest.writeInt(parentId);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(value);
        dest.writeInt(departmentId);
        dest.writeInt(rootDeptId);
        dest.writeString(filterNameEN);
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
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSlug() {
        if (!TextUtils.isEmpty(value)) {
            return value;
        }

        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

//    public String getParentId() {
//        return parentId;
//    }
//
//    public void setParentId(String parentId) {
//        this.parentId = parentId;
//    }


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getRootDeptId() {
        return rootDeptId;
    }

    public void setRootDeptId(int rootDeptId) {
        this.rootDeptId = rootDeptId;
    }

    public String getFilterNameEN() {
        return filterNameEN;
    }

    public void setFilterNameEN(String filterNameEN) {
        this.filterNameEN = filterNameEN;
    }
}
