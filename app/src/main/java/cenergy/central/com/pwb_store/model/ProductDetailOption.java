package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailOption implements IViewType, Parcelable {
    public static final Creator<ProductDetailOption> CREATOR = new Creator<ProductDetailOption>() {
        @Override
        public ProductDetailOption createFromParcel(Parcel in) {
            return new ProductDetailOption(in);
        }

        @Override
        public ProductDetailOption[] newArray(int size) {
            return new ProductDetailOption[size];
        }
    };
    private int viewTypeId;
    private int id;
    private String slug;
    private String name;
    private int total;
    private List<ProductDetailOptionItem> productDetailOptionItemList = new ArrayList<>();
    private ProductDetailOptionItem selectedProductDetailOptionItem;
    private int selectedPosition;

    public ProductDetailOption(int id, String slug, String name, int total, List<ProductDetailOptionItem> productDetailOptionItemList) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.total = total;
        this.productDetailOptionItemList = productDetailOptionItemList;
    }

    protected ProductDetailOption(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        slug = in.readString();
        name = in.readString();
        total = in.readInt();
        productDetailOptionItemList = in.createTypedArrayList(ProductDetailOptionItem.CREATOR);
        selectedProductDetailOptionItem = in.readParcelable(ProductDetailOptionItem.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeString(slug);
        dest.writeString(name);
        dest.writeInt(total);
        dest.writeTypedList(productDetailOptionItemList);
        dest.writeParcelable(selectedProductDetailOptionItem, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ProductDetailOptionItem> getProductDetailOptionItemList() {
        return productDetailOptionItemList;
    }

    public void setProductDetailOptionItemList(List<ProductDetailOptionItem> productDetailOptionItemList) {
        this.productDetailOptionItemList = productDetailOptionItemList;
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public void validateProductDetailOptionItemList() {
        ProductDetailOptionItem productDetailOptionItem;
        for (int i = 0; i < productDetailOptionItemList.size(); i++) {
            productDetailOptionItem = productDetailOptionItemList.get(i);
            if (isSelectedProductDetailOptionItemAvailable()) {
                if (productDetailOptionItem.getId() == getSelectedProductDetailOptionItem().getId()) {
                    productDetailOptionItem.setSelected(true);
                    this.selectedPosition = i;
                } else {
                    productDetailOptionItem.setSelected(false);
                }
            } else {
                if (productDetailOptionItem.isDefault()) {
                    productDetailOptionItem.setSelected(true);
                    this.selectedProductDetailOptionItem = productDetailOptionItem;
                    this.selectedPosition = i;
                } else {
                    productDetailOptionItem.setSelected(false);
                }
            }
        }
    }

    public boolean isSelectedProductDetailOptionItemAvailable() {
        return selectedProductDetailOptionItem != null;
    }

    public ProductDetailOptionItem getSelectedProductDetailOptionItem() {
        return selectedProductDetailOptionItem;
    }

    public void setSelectedProductDetailOptionItem(ProductDetailOptionItem selectedProductDetailOptionItem) {
        this.selectedProductDetailOptionItem = selectedProductDetailOptionItem;
        validateProductDetailOptionItemList();
    }

    public void setSelectedProductDetailAvailableOptionItem(ProductDetailAvailableOptionItem selectedProductDetailAvailableOptionItem) {
        if (selectedProductDetailOptionItem != null && selectedProductDetailOptionItem.isAvailableOptionAvailable()) {
            selectedProductDetailOptionItem.getProductDetailAvailableOption().setSelectedProductAvailableOptionItem(selectedProductDetailAvailableOptionItem);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
