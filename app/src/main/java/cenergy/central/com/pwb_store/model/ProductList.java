package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by napabhat on 7/7/2017 AD.
 */

public class ProductList implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName(value = "ProductId", alternate = "sku")
    @Expose
    private String productId;
//    @SerializedName(value = "MediumFullUrl", alternate = "image")
//    @Expose
    private String imageUrl;
    @SerializedName(value = "ProductName", alternate = "name")
    @Expose
    private String name;
//    @SerializedName("ProductNameEN")
//    @Expose
//    private String productNameEN;
//    @SerializedName(value = "Description" , alternate = "description")
//    @Expose
    private String description;
//    @SerializedName("DescriptionEN")
//    @Expose
//    private String descriptionEN;
    @SerializedName(value = "NormalPrice", alternate = "price")
    @Expose
    private double oldPrice;
//    @SerializedName("SpecialPrice")
//    @Expose
    private double newPrice;
//    @SerializedName("UrlName")
//    @Expose
    private String urlName;
//    @SerializedName("UrlNameEN")
//    @Expose
    private String urlNameEN;
//    @SerializedName("Review")
//    @Expose
    private String review;
//    @SerializedName("ReviewEN")
//    @Expose
    private String reviewEN;
//    @SerializedName("Barcode")
//    @Expose
    private String barcode;
//    @SerializedName("NumOfImage")
//    @Expose
    private int numOfImage;
//    @SerializedName("CanInstallment")
//    @Expose
    private boolean canInstallment;
//    @SerializedName("T1CRedeemPoint")
//    @Expose
    private int t1CRedeemPoint;
//    @SerializedName("DepartmentId")
//    @Expose
    private int departmentId;
//    @SerializedName("BrandId")
//    @Expose
    private int brandId;
//    @SerializedName("BrandName")
//    @Expose
    private String brandName;
//    @SerializedName("BrandNameEn")
//    @Expose
    private String brandNameEN;
//    @SerializedName("StoreId")
//    @Expose
    private String storeId;
//    @SerializedName("StockOnHand")
//    @Expose
    private int stockOnHand;
    @SerializedName("extension_attributes")
    @Expose
    private Extension mExtension;
//    @SerializedName("custom_attributes")
//    @Expose
    private List<CustomAttributes> mCustomAttributes = new ArrayList<>();

    public ProductList(String productId, String imageUrl, String name, String description, double oldPrice, double newPrice) {
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }

    protected ProductList(Parcel in) {
        viewTypeId = in.readInt();
        productId = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        description = in.readString();
        //descriptionEN = in.readString();
        oldPrice = in.readInt();
        newPrice = in.readInt();
        //productNameEN = in.readString();
        urlName = in.readString();
        urlNameEN = in.readString();
        review = in.readString();
        reviewEN = in.readString();
        barcode = in.readString();
        numOfImage = in.readInt();
        canInstallment = in.readByte() != 0;
        t1CRedeemPoint = in.readInt();
        departmentId = in.readInt();
        brandId = in.readInt();
        brandName = in.readString();
        brandNameEN = in.readString();
        storeId = in.readString();
        stockOnHand = in.readInt();
        mExtension = in.readParcelable(Extension.class.getClassLoader());
        mCustomAttributes = in.createTypedArrayList(CustomAttributes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(productId);
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(description);
       // dest.writeString(descriptionEN);
        dest.writeDouble(oldPrice);
        dest.writeDouble(newPrice);
      //  dest.writeString(productNameEN);
        dest.writeString(urlName);
        dest.writeString(urlNameEN);
        dest.writeString(review);
        dest.writeString(reviewEN);
        dest.writeString(barcode);
        dest.writeInt(numOfImage);
        dest.writeByte((byte) (canInstallment ? 1 : 0));
        dest.writeInt(t1CRedeemPoint);
        dest.writeInt(departmentId);
        dest.writeInt(brandId);
        dest.writeString(brandName);
        dest.writeString(brandNameEN);
        dest.writeString(storeId);
        dest.writeInt(stockOnHand);
        dest.writeParcelable(mExtension, flags);
        dest.writeTypedList(mCustomAttributes);
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDisplayOldPrice(String unit) {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(oldPrice));
    }

    public String getDisplayNewPrice(String unit) {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(newPrice));
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

    public int getT1CRedeemPoint() {
        return t1CRedeemPoint;
    }

    public void setT1CRedeemPoint(int t1CRedeemPoint) {
        this.t1CRedeemPoint = t1CRedeemPoint;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandNameEN() {
        return brandNameEN;
    }

    public void setBrandNameEN(String brandNameEN) {
        this.brandNameEN = brandNameEN;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public Extension getExtension() {
        return mExtension;
    }

    public void setExtension(Extension extension) {
        mExtension = extension;
    }

    public List<CustomAttributes> getCustomAttributes() {
        return mCustomAttributes;
    }

    public void setCustomAttributes(List<CustomAttributes> customAttributes) {
        mCustomAttributes = customAttributes;
    }
}
