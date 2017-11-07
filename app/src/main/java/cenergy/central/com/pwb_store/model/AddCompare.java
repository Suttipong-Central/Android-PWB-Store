package cenergy.central.com.pwb_store.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by napabhat on 9/13/2017 AD.
 */

public class AddCompare extends RealmObject {
    @PrimaryKey
    private String productId;
    private String productSku;
    private String productName;
    private String productNameEN;
    private String urlName;
    private String urlNameEN;
    private String description;
    private String descriptionEN;
    private String review;
    private String reviewEN;
    private String barcode;
    private int numOfImage;
    private boolean canInstallment;
    private int t1cPoint;
    private int departmentId;
    private int brandId;
    private String brand;
    private String brandEN;
    //private List<ProductDetailImageItem> mProductDetailImageItems = new ArrayList<>();
    private double originalPrice;
    private double price;
    private String storeId;
    private int stockAvailable;
    private String url;

    public AddCompare(){

    }

//    public AddCompare getByPrimaryKey(Realm realm, String id) {
//        return realm.where(getClass()).equalTo("productId", id).findFirst();
//    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
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

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
}
