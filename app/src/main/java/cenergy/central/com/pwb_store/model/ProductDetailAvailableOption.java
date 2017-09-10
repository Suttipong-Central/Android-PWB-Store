package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailAvailableOption implements IViewType, Parcelable {
    public static final Creator<ProductDetailAvailableOption> CREATOR = new Creator<ProductDetailAvailableOption>() {
        @Override
        public ProductDetailAvailableOption createFromParcel(Parcel in) {
            return new ProductDetailAvailableOption(in);
        }

        @Override
        public ProductDetailAvailableOption[] newArray(int size) {
            return new ProductDetailAvailableOption[size];
        }
    };
    private int viewTypeId;
    private int id;
    private String slug;
    private String name;
    private int total;
    private boolean isImages;
    private List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemList = new ArrayList<>();
    private ProductDetailAvailableOptionItem selectedProductDetailAvailableOptionItem;
    private int selectedPosition;

    public ProductDetailAvailableOption(int id, String slug, String name, int total, List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItem) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.total = total;
        this.productDetailAvailableOptionItemList = productDetailAvailableOptionItem;
    }

    protected ProductDetailAvailableOption(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        slug = in.readString();
        name = in.readString();
        total = in.readInt();
        isImages = in.readByte() != 0;
        productDetailAvailableOptionItemList = in.createTypedArrayList(ProductDetailAvailableOptionItem.CREATOR);
        selectedProductDetailAvailableOptionItem = in.readParcelable(ProductDetailAvailableOption.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeString(slug);
        dest.writeString(name);
        dest.writeInt(total);
        dest.writeByte((byte) (isImages ? 1 : 0));
        dest.writeTypedList(productDetailAvailableOptionItemList);
        dest.writeParcelable(selectedProductDetailAvailableOptionItem, flags);
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

    public boolean isImages() {
        return isImages;
    }

    public void setImages(boolean images) {
        isImages = images;
    }

    public List<ProductDetailAvailableOptionItem> getProductDetailAvailableOptionItemList() {
        return productDetailAvailableOptionItemList;
    }

    public void setProductDetailAvailableOptionItemList(List<ProductDetailAvailableOptionItem> productDetailAvailableOptionItemList) {
        this.productDetailAvailableOptionItemList = productDetailAvailableOptionItemList;
    }

    public void validateProductDetailAvailableOptionItemList() {
        ProductDetailAvailableOptionItem productDetailAvailableOptionItem;
        boolean isDefaultFound = false;
        for (int i = 0; i < productDetailAvailableOptionItemList.size(); i++) {
            productDetailAvailableOptionItem = productDetailAvailableOptionItemList.get(i);
            if (isSelectedProductDetailAvailableOptionItemAvailable()) {
                isDefaultFound = true;
                if (productDetailAvailableOptionItem.getId() == getSelectedProductDetailAvailableOptionItem().getId()) {
                    productDetailAvailableOptionItem.setSelected(true);
                    this.selectedPosition = i;
                } else {
                    productDetailAvailableOptionItem.setSelected(false);
                }
            } else {
                if (productDetailAvailableOptionItem.isDefault()) {
                    productDetailAvailableOptionItem.setSelected(true);
                    this.selectedProductDetailAvailableOptionItem = productDetailAvailableOptionItem;
                    this.selectedPosition = i;
                    isDefaultFound = true;
                } else {
                    productDetailAvailableOptionItem.setSelected(false);
                }
            }
        }

        if (!isDefaultFound && !productDetailAvailableOptionItemList.isEmpty()) {
            ProductDetailAvailableOptionItem firstProductDetailAvailableOptionItem = productDetailAvailableOptionItemList.get(0);
            firstProductDetailAvailableOptionItem.setSelected(true);
            this.selectedProductDetailAvailableOptionItem = firstProductDetailAvailableOptionItem;
            this.selectedPosition = 0;
        }
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int viewTypeId) {
        this.viewTypeId = viewTypeId;
    }

    public ProductDetailAvailableOptionItem getSelectedProductDetailAvailableOptionItem() {
        return selectedProductDetailAvailableOptionItem;
    }

    public void setSelectedProductAvailableOptionItem(ProductDetailAvailableOptionItem selectedProductDetailAvailableOptionItem) {
        this.selectedProductDetailAvailableOptionItem = selectedProductDetailAvailableOptionItem;
        validateProductDetailAvailableOptionItemList();
    }

    public boolean isSelectedProductDetailAvailableOptionItemAvailable() {
        return selectedProductDetailAvailableOptionItem != null;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
