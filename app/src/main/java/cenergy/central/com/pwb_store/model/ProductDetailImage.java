package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailImage implements IViewType, Parcelable {

    public static final Creator<ProductDetailImage> CREATOR = new Creator<ProductDetailImage>() {
        @Override
        public ProductDetailImage createFromParcel(Parcel in) {
            return new ProductDetailImage(in);
        }

        @Override
        public ProductDetailImage[] newArray(int size) {
            return new ProductDetailImage[size];
        }
    };
    private int viewTypeId;
    private int total;
    private List<ProductDetailImageItem> mProductDetailImageItems;
    private ProductDetailImageItem selectedProductDetailImageItem;

    public ProductDetailImage(int total, List<ProductDetailImageItem> productDetailImageItems) {
        this.total = total;
        this.mProductDetailImageItems = productDetailImageItems;

    }

    protected ProductDetailImage(Parcel in) {
        viewTypeId = in.readInt();
        total = in.readInt();
        mProductDetailImageItems = in.createTypedArrayList(ProductDetailImageItem.CREATOR);
        selectedProductDetailImageItem = in.readParcelable(ProductDetailImageItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(total);
        dest.writeList(mProductDetailImageItems);
        dest.writeParcelable(selectedProductDetailImageItem, flags);
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

    public List<ProductDetailImageItem> getProductDetailImageItems() {
        return mProductDetailImageItems;
    }

    public void setProductDetailImageItems(List<ProductDetailImageItem> productDetailImageItems) {
        mProductDetailImageItems = productDetailImageItems;
    }

    public void validateProductDetailImageItemList() {
        ProductDetailImageItem productDetailImageItem;
        for (int i = 0; i < mProductDetailImageItems.size(); i++) {
            productDetailImageItem = mProductDetailImageItems.get(i);
            if (isSelectedProductDetailImageItemAvailable()) {
                if (productDetailImageItem.getImgUrl().equalsIgnoreCase(getSelectedProductDetailImageItem().getImgUrl())) {
                    productDetailImageItem.setSelected(true);
                } else {
                    productDetailImageItem.setSelected(false);
                }
            }
        }
    }

    public boolean isSelectedProductDetailImageItemAvailable() {
        return selectedProductDetailImageItem != null;
    }

    public ProductDetailImageItem getSelectedProductDetailImageItem() {
        return selectedProductDetailImageItem;
    }

    public void setSelectedProductDetailImageItem(ProductDetailImageItem selectedProductDetailImageItem) {
        this.selectedProductDetailImageItem = selectedProductDetailImageItem;
        validateProductDetailImageItemList();
    }

}
