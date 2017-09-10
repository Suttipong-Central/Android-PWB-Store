package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 7/13/2017 AD.
 */

public class ProductFilterHeader implements IViewType, Parcelable {
    public static final Creator<ProductFilterHeader> CREATOR = new Creator<ProductFilterHeader>() {
        @Override
        public ProductFilterHeader createFromParcel(Parcel in) {
            return new ProductFilterHeader(in);
        }

        @Override
        public ProductFilterHeader[] newArray(int size) {
            return new ProductFilterHeader[size];
        }
    };
    private static final String TAG = "ProductFilterHeader";
    private int viewTypeId;
    @SerializedName("DepartmentId")
    @Expose
    private int departmentId;
    @SerializedName("ParentId")
    @Expose
    private int id;
    @SerializedName("RootDeptId")
    @Expose
    private int rootDeptId;
    @SerializedName("DepartmentName")
    @Expose
    private String name;
    @SerializedName("DepartmentNameEN")
    @Expose
    private String nameEN;
    @SerializedName("MetaKeyword")
    @Expose
    private String slug;
    @SerializedName("MetaDescription")
    @Expose
    private String metaDescription;
    @SerializedName("UrlName")
    @Expose
    private String urlName;
    @SerializedName("UrlNameEN")
    @Expose
    private String urlNameEN;
    private String type;
    @SerializedName("Children")
    @Expose
    private List<ProductFilterItem> mProductFilterItemList = new ArrayList<>();
    private boolean isExpanded;

    public ProductFilterHeader(int departmentId, int rootDeptId, int id, String name, String nameEN, String slug,
                               String metaDescription, String urlName, String urlNameEN, String type,
                               List<ProductFilterItem> mProductFilterItemList) {
        this.departmentId = departmentId;
        this.rootDeptId = rootDeptId;
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.type = type;
        this.mProductFilterItemList = mProductFilterItemList;
        this.isExpanded = false;
        this.nameEN = nameEN;
        this.metaDescription = metaDescription;
        this.urlName = urlName;
        this.urlNameEN = urlNameEN;
    }

    public ProductFilterHeader(ProductFilterHeader productFilterHeader) {
        this.id = productFilterHeader.getId();
        this.name = productFilterHeader.getName();
        this.slug = productFilterHeader.getSlug();
        this.type = "single";
        this.departmentId = productFilterHeader.getDepartmentId();
        this.nameEN = productFilterHeader.getNameEN();
        this.rootDeptId = productFilterHeader.getRootDeptId();
        this.metaDescription = productFilterHeader.getMetaDescription();
        this.urlName = productFilterHeader.getUrlName();
        this.urlNameEN = productFilterHeader.getUrlName();

        List<ProductFilterItem> productFilterItemList = new ArrayList<>();
        if (productFilterHeader.isProductFilterItemListAvailable())
            for (ProductFilterItem productFilterItem :
                    productFilterHeader.getProductFilterItemList()) {
                productFilterItemList.add(new ProductFilterItem(productFilterItem));
            }

        this.mProductFilterItemList = productFilterItemList;
        this.isExpanded = false;
    }

    protected ProductFilterHeader(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        name = in.readString();
        slug = in.readString();
        type = in.readString();
        mProductFilterItemList = in.createTypedArrayList(ProductFilterItem.CREATOR);
        isExpanded = in.readByte() != 0;
        departmentId = in.readInt();
        rootDeptId = in.readInt();
        nameEN = in.readString();
        metaDescription = in.readString();
        urlName = in.readString();
        urlNameEN = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(type);
        dest.writeTypedList(mProductFilterItemList);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
        dest.writeInt(departmentId);
        dest.writeInt(rootDeptId);
        dest.writeString(nameEN);
        dest.writeString(metaDescription);
        dest.writeString(urlName);
        dest.writeString(urlNameEN);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSortBy() {
        return name.equalsIgnoreCase("เรียงสินค้าตาม");
    }

    public boolean isMultipleType() {
        return type.equalsIgnoreCase("multiple");
    }

    public List<ProductFilterItem> getProductFilterItemList() {
        return mProductFilterItemList;
    }

    public void setProductFilterItemList(List<ProductFilterItem> productFilterItemList) {
        this.mProductFilterItemList = productFilterItemList;
    }

    public boolean isProductFilterItemListAvailable() {
        return mProductFilterItemList != null && !mProductFilterItemList.isEmpty();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }


    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRootDeptId() {
        return rootDeptId;
    }

    public void setRootDeptId(int rootDeptId) {
        this.rootDeptId = rootDeptId;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
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

    public void clearAllSelectedFilterOptions() {
        for (ProductFilterItem productFilterItem :
                mProductFilterItemList) {
            productFilterItem.setSelected(false);
        }
    }

    public void replaceSortHeader(ProductFilterHeader productFilterHeader, boolean isPreserveSelection) {
        this.id = productFilterHeader.getId();
        this.name = productFilterHeader.getName();
        this.slug = productFilterHeader.getSlug();
        this.type = productFilterHeader.getType();
        if (isPreserveSelection) {
            for (ProductFilterItem productFilterItem :
                    mProductFilterItemList) {
                for (ProductFilterItem newProductFilterItem :
                        productFilterHeader.getProductFilterItemList()) {
                    if (productFilterItem.getSlug().equalsIgnoreCase(newProductFilterItem.getSlug())) {
                        newProductFilterItem.setSelected(productFilterItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mProductFilterItemList = productFilterHeader.getProductFilterItemList();
    }

    public void replaceExisting(ProductFilterHeader loadedProductFilterHeader, boolean isPreserveSelection) {
        this.id = loadedProductFilterHeader.getId();
        this.name = loadedProductFilterHeader.getName();
        this.slug = loadedProductFilterHeader.getSlug();
        this.type = loadedProductFilterHeader.getType();

        if (isPreserveSelection) {
            for (ProductFilterItem productFilterItem :
                    mProductFilterItemList) {
                for (ProductFilterItem newProductFilterItem :
                        loadedProductFilterHeader.getProductFilterItemList()) {
                    if (productFilterItem.getSlug().equalsIgnoreCase(newProductFilterItem.getSlug())) {
                        newProductFilterItem.setSelected(productFilterItem.isSelected());
                        break;
                    }
                }
            }
        }

        this.mProductFilterItemList = loadedProductFilterHeader.getProductFilterItemList();
    }

    public String getSelectedProductFilterOptionIfAvailable() {
        StringBuilder stringBuilder = new StringBuilder();

        for (ProductFilterItem productFilterItem :
                mProductFilterItemList) {
            if (productFilterItem.isSelected()) {
                stringBuilder.append(productFilterItem.getSlug());
                stringBuilder.append(",");
            }
        }

        String resultString = stringBuilder.toString();

        if (TextUtils.isEmpty(resultString)) {
            return "";
        }

        return resultString.substring(0, resultString.length() - 1);
    }
}