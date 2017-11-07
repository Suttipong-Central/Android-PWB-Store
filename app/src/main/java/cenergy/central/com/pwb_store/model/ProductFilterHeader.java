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
    @SerializedName("entity_id")
    @Expose
    private String id;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName(value = "DepartmentName", alternate = "name")
    @Expose
    private String name;
    @SerializedName(value = "UrlName", alternate = "image")
    @Expose
    private String urlName;
    private String type;
    @SerializedName(value = "Children", alternate = "children")
    @Expose
    private List<ProductFilterItem> mProductFilterItemList = new ArrayList<>();
    private boolean isExpanded;

//    public ProductFilterHeader(int departmentId, int rootDeptId, int id, String name, String nameEN, String slug,
//                               String metaDescription, String urlName, String urlNameEN, String type,
//                               List<ProductFilterItem> mProductFilterItemList) {
//        this.departmentId = departmentId;
//        this.rootDeptId = rootDeptId;
//        this.id = id;
//        this.name = name;
//        this.slug = slug;
//        this.type = type;
//        this.mProductFilterItemList = mProductFilterItemList;
//        this.isExpanded = false;
//        this.nameEN = nameEN;
//        this.metaDescription = metaDescription;
//        this.urlName = urlName;
//        this.urlNameEN = urlNameEN;
//    }

    public ProductFilterHeader(String id, String name, String level, String urlName, String type, List<ProductFilterItem> mProductFilterItemList) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.mProductFilterItemList = mProductFilterItemList;
        this.isExpanded = false;
        this.urlName = urlName;
        this.type = type;
    }

    public ProductFilterHeader(ProductFilterHeader productFilterHeader) {
        this.id = productFilterHeader.getId();
        this.name = productFilterHeader.getName();
        this.type = "single";
        this.urlName = productFilterHeader.getUrlName();

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
        id = in.readString();
        name = in.readString();
        type = in.readString();
        mProductFilterItemList = in.createTypedArrayList(ProductFilterItem.CREATOR);
        isExpanded = in.readByte() != 0;
        urlName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(mProductFilterItemList);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
        dest.writeString(urlName);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
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
        this.level = productFilterHeader.getLevel();
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
        this.level = loadedProductFilterHeader.getLevel();
        this.type = loadedProductFilterHeader.getType();

        if (isPreserveSelection) {
            for (ProductFilterItem productFilterItem :
                    mProductFilterItemList) {
                for (ProductFilterItem newProductFilterItem :
                        loadedProductFilterHeader.getProductFilterItemList()) {
                    if (productFilterItem.getId().equalsIgnoreCase(newProductFilterItem.getId())) {
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
                stringBuilder.append(productFilterItem.getId());
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