package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailAvailableOptionItem implements IViewType, Parcelable {
    public static final Creator<ProductDetailAvailableOptionItem> CREATOR = new Creator<ProductDetailAvailableOptionItem>() {
        @Override
        public ProductDetailAvailableOptionItem createFromParcel(Parcel in) {
            return new ProductDetailAvailableOptionItem(in);
        }

        @Override
        public ProductDetailAvailableOptionItem[] newArray(int size) {
            return new ProductDetailAvailableOptionItem[size];
        }
    };
    private int viewTypeId;
    private int id;
    private int masterId;
    private String productName;
    private int originalPrice;
    private int price;
    private String productId;
    private String slug;
    private String name;
    private int stockAvailable;
    private int minQuantity;
    private int maxQuantity;
    private String imgUrl;
    private boolean isDefault;
    private ProductDetailImage mProductDetailImage;
    private boolean isSelected;

    public ProductDetailAvailableOptionItem(int id, String slug, String name, String imgUrl) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    protected ProductDetailAvailableOptionItem(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        masterId = in.readInt();
        productName = in.readString();
        productId = in.readString();
        originalPrice = in.readInt();
        price = in.readInt();
        slug = in.readString();
        name = in.readString();
        stockAvailable = in.readInt();
        minQuantity = in.readInt();
        maxQuantity = in.readInt();
        imgUrl = in.readString();
        isDefault = in.readByte() != 0;
        mProductDetailImage = in.readParcelable(ProductDetailImage.class.getClassLoader());
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeInt(masterId);
        dest.writeString(productName);
        dest.writeString(productId);
        dest.writeInt(price);
        dest.writeInt(originalPrice);
        dest.writeString(slug);
        dest.writeString(name);
        dest.writeInt(stockAvailable);
        dest.writeInt(minQuantity);
        dest.writeInt(maxQuantity);
        dest.writeString(imgUrl);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeParcelable(mProductDetailImage, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public ProductDetailImage getProductDetailImage() {
        return mProductDetailImage;
    }

    public void setProductDetailImage(ProductDetailImage productDetailImage) {
        mProductDetailImage = productDetailImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
