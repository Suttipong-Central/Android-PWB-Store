package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by napabhat on 10/6/2016 AD.
 */

public class ProductSortList implements IViewType, Parcelable {

    public static final Creator<ProductSortList> CREATOR = new Creator<ProductSortList>() {
        @Override
        public ProductSortList createFromParcel(Parcel in) {
            return new ProductSortList(in);
        }

        @Override
        public ProductSortList[] newArray(int size) {
            return new ProductSortList[size];
        }
    };
    private int viewTypeId;
    private int total;
    private String name;
    private String slug;
    private List<ProductFilterItem> mProductFilterItems;
    private boolean isExpanded;

    public ProductSortList(String name, String slug, List<ProductFilterItem> productFilterItems) {
        this.name = name;
        this.slug = slug;
        this.mProductFilterItems = productFilterItems;
    }

    protected ProductSortList(Parcel in) {
        viewTypeId = in.readInt();
        mProductFilterItems = in.createTypedArrayList(ProductFilterItem.CREATOR);
        total = in.readInt();
        name = in.readString();
        slug = in.readString();
        isExpanded = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeList(mProductFilterItems);
        dest.writeInt(total);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeByte((byte) (isExpanded ? 1 : 0));
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isSortBy() {
        return name.equalsIgnoreCase("เรียงสินค้าตาม");
    }

    public List<ProductFilterItem> getProductFilterItems() {
        return mProductFilterItems;
    }

    public void setProductFilterItems(List<ProductFilterItem> productFilterItems) {
        mProductFilterItems = productFilterItems;
    }
}
