package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("ProductId")
    @Expose
    private String productId;
    private String slug;
    @SerializedName("LargeFullUrl")
    @Expose
    private String imgProduct;
    @SerializedName("ProductName")
    @Expose
    private String productName;
    @SerializedName("ProductNameEN")
    @Expose
    private String productNameEN;
    @SerializedName("UrlName")
    @Expose
    private String urlName;
    @SerializedName("UrlNameEN")
    @Expose
    private String urlNameEN;
    @SerializedName("Description")
    @Expose
    private String productDescription;
    @SerializedName("DescriptionEN")
    @Expose
    private String productDescriptionEN;
    @SerializedName("Review")
    @Expose
    private String review;
    @SerializedName("ReviewEN")
    @Expose
    private String reviewEN;
    @SerializedName("Barcode")
    @Expose
    private String barcode;
    @SerializedName("NumOfImage")
    @Expose
    private int numOfImage;
    @SerializedName("CanInstallment")
    @Expose
    private boolean canInstallment;
    @SerializedName("T1CRedeemPoint")
    @Expose
    private int t1cPoint;
    @SerializedName("DepartmentId")
    @Expose
    private int departmentId;
    @SerializedName("BrandId")
    @Expose
    private int brandId;
    @SerializedName("BrandName")
    @Expose
    private String brand;
    @SerializedName("BrandNameEn")
    @Expose
    private String brandEN;
    @SerializedName("StoreId")
    @Expose
    private String storeId;
    @SerializedName("NormalPrice")
    @Expose
    private double oldPrice;
    @SerializedName("SpecialPrice")
    @Expose
    private double newPrice;
    @SerializedName("StockOnHand")
    @Expose
    private int stockOnHand;
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
        productNameEN = in.readString();
        urlName = in.readString();
        urlNameEN = in.readString();
        productDescription = in.readString();
        productDescriptionEN = in.readString();
        review = in.readString();
        reviewEN = in.readString();
        barcode = in.readString();
        numOfImage = in.readInt();
        canInstallment = in.readByte() != 0;
        t1cPoint = in.readInt();
        departmentId = in.readInt();
        brandId = in.readInt();
        brandEN = in.readString();
        storeId = in.readString();
        oldPrice = in.readInt();
        newPrice = in.readInt();
        productId = in.readString();
        slug = in.readString();
        savePercent = in.readInt();
        stockOnHand = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(productName);
        dest.writeString(productNameEN);
        dest.writeString(urlName);
        dest.writeString(urlNameEN);
        dest.writeString(productDescription);
        dest.writeString(productNameEN);
        dest.writeString(review);
        dest.writeString(reviewEN);
        dest.writeString(barcode);
        dest.writeInt(numOfImage);
        dest.writeByte((byte) (canInstallment ? 1 : 0));
        dest.writeInt(t1cPoint);
        dest.writeInt(departmentId);
        dest.writeInt(brandId);
        dest.writeString(brandEN);
        dest.writeString(storeId);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
        dest.writeString(productId);
        dest.writeString(slug);
        dest.writeInt(savePercent);
        dest.writeInt(stockOnHand);
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

    public String getProductNameEN() {
        return productNameEN;
    }

    public void setProductNameEN(String productNameEN) {
        this.productNameEN = productNameEN;
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

    public String getProductDescriptionEN() {
        return productDescriptionEN;
    }

    public void setProductDescriptionEN(String productDescriptionEN) {
        this.productDescriptionEN = productDescriptionEN;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewEN() {
        return reviewEN;
    }

    public void setReviewEN(String reviewEN) {
        this.reviewEN = reviewEN;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getNumOfImage() {
        return numOfImage;
    }

    public void setNumOfImage(int numOfImage) {
        this.numOfImage = numOfImage;
    }

    public boolean isCanInstallment() {
        return canInstallment;
    }

    public void setCanInstallment(boolean canInstallment) {
        this.canInstallment = canInstallment;
    }

    public int getT1cPoint() {
        return t1cPoint;
    }

    public void setT1cPoint(int t1cPoint) {
        this.t1cPoint = t1cPoint;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandEN() {
        return brandEN;
    }

    public void setBrandEN(String brandEN) {
        this.brandEN = brandEN;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(int stockOnHand) {
        this.stockOnHand = stockOnHand;
    }
}
