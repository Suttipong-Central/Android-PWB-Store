package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailOptionItem implements IViewType, Parcelable {

    public static final Creator<ProductDetailOptionItem> CREATOR = new Creator<ProductDetailOptionItem>() {
        @Override
        public ProductDetailOptionItem createFromParcel(Parcel in) {
            return new ProductDetailOptionItem(in);
        }

        @Override
        public ProductDetailOptionItem[] newArray(int size) {
            return new ProductDetailOptionItem[size];
        }
    };
    private int viewTypeId;
    @SerializedName("attribute_value_id")
    @Expose
    private int id;
    @SerializedName("master_id")
    @Expose
    private int masterId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("original_price")
    @Expose
    private double originalPrice;
    @SerializedName("pid")
    @Expose
    private String productId;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("min_quantity")
    @Expose
    private int minQuantity;
    @SerializedName("max_quantity")
    @Expose
    private int maxQuantity;
    @SerializedName("stock_available")
    @Expose
    private int stockAvailable;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("is_default")
    @Expose
    private boolean isDefault;
    @SerializedName("images")
    @Expose
    private ProductDetailImage mProductDetailImage;
    @SerializedName("available_options")
    @Expose
    private ProductDetailAvailableOption mProductDetailAvailableOption;
    private boolean isSelected;
    //private List<ProductDetailAvailableOption> availableOptions = new ArrayList<>();


    public ProductDetailOptionItem(int id, String slug, String name, String imgUrl, ProductDetailAvailableOption availableOptions) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.imgUrl = imgUrl;
        this.mProductDetailAvailableOption = availableOptions;
    }

    protected ProductDetailOptionItem(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readInt();
        masterId = in.readInt();
        productName = in.readString();
        price = in.readDouble();
        originalPrice = in.readDouble();
        productId = in.readString();
        slug = in.readString();
        name = in.readString();
        minQuantity = in.readInt();
        maxQuantity = in.readInt();
        stockAvailable = in.readInt();
        imgUrl = in.readString();
        isDefault = in.readByte() != 0;
        mProductDetailImage = in.readParcelable(ProductDetailImage.class.getClassLoader());
        mProductDetailAvailableOption = in.readParcelable(ProductDetailAvailableOption.class.getClassLoader());
        isSelected = in.readByte() != 0;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(id);
        dest.writeInt(masterId);
        dest.writeString(productName);
        dest.writeDouble(price);
        dest.writeDouble(originalPrice);
        dest.writeString(productId);
        dest.writeString(slug);
        dest.writeString(name);
        dest.writeInt(minQuantity);
        dest.writeInt(maxQuantity);
        dest.writeInt(stockAvailable);
        dest.writeString(imgUrl);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeParcelable(mProductDetailImage, flags);
        dest.writeParcelable(mProductDetailAvailableOption, flags);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
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

    public ProductDetailAvailableOption getProductDetailAvailableOption() {
        return mProductDetailAvailableOption;
    }

    public void setProductDetailAvailableOption(ProductDetailAvailableOption productDetailAvailableOption) {
        mProductDetailAvailableOption = productDetailAvailableOption;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAvailableOptionAvailable() {
        return mProductDetailAvailableOption != null;
    }

}
