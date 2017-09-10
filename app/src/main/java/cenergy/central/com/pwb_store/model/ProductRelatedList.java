package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by napabhat on 7/18/2017 AD.
 */


public class ProductRelatedList implements IViewType, Parcelable {
    public static final Creator<ProductRelatedList> CREATOR = new Creator<ProductRelatedList>() {
        @Override
        public ProductRelatedList createFromParcel(Parcel in) {
            return new ProductRelatedList(in);
        }

        @Override
        public ProductRelatedList[] newArray(int size) {
            return new ProductRelatedList[size];
        }
    };
    private int viewTypeId;
    private String productId;
    private String slug;
    private String imgProduct;
    private String productName;
    private String productDescription;
    private double oldPrice;
    private double newPrice;
    private int savePercent;

    public ProductRelatedList(String imgProduct, String productName, String productDescription, double oldPrice, double newPrice, String productId) {
        this.imgProduct = imgProduct;
        this.productName = productName;
        this.productDescription = productDescription;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.productId = productId;

    }

    protected ProductRelatedList(Parcel in) {
        viewTypeId = in.readInt();
        productName = in.readString();
        productDescription = in.readString();
        oldPrice = in.readInt();
        newPrice = in.readInt();
        productId = in.readString();
        slug = in.readString();
        savePercent = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
        dest.writeString(productId);
        dest.writeString(slug);
        dest.writeInt(savePercent);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getDisplayOldPrice(String unit) {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(oldPrice));
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public String getDisplayNewPrice(String unit) {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(newPrice));
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getSavePercent() {
        return savePercent;
    }

    public void setSavePercent(int savePercent) {
        this.savePercent = savePercent;
    }
}
