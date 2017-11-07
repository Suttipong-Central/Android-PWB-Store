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
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetail implements IViewType, Parcelable {

    public static final Creator<ProductDetail> CREATOR = new Creator<ProductDetail>() {
        @Override
        public ProductDetail createFromParcel(Parcel in) {
            return new ProductDetail(in);
        }

        @Override
        public ProductDetail[] newArray(int size) {
            return new ProductDetail[size];
        }
    };

    private int viewTypeId;
    private int masterId;
    @SerializedName(value = "ProductId", alternate = "id")
    @Expose
    private String productCode;
    @SerializedName(value = "ProductName", alternate = "name")
    @Expose
    private String productName;
    @SerializedName("sku")
    @Expose
    private String sku;
    private String productNameEN;
    private String urlName;
    private String urlNameEN;
    private String slug;
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
    private String bu;
    private List<ProductDetailImageItem> mProductDetailImageItems;
    private ProductDetailImage productImageList;
    private double savePercent;
    private double originalPrice;
    private double price;
    private String storeId;
    private int quantity;
    private int minQuantity;
    private int maxQuantity;
    private int stockAvailable;
    private List<ProductRelatedList> mProductRelatedLists;
    private Recommend mRecommend;
    private TheOneCardProductDetail theOneCardDetailList;
    private List<ProductDetailPromotion> detailPromotionList;
    private ProductDetailOption productDetailOption;
    private SpecDao mSpecDao;
    @SerializedName("extension_attributes")
    @Expose
    private ExtensionProductDetail mExtensionProductDetail;
//    @SerializedName("custom_attributes")
//    @Expose
    private List<CustomAttributesProductDetail> mCustomAttributesProductDetails = new ArrayList<>();

    public ProductDetail(String slug, String brand, String productName, String productCode, String bu, ProductDetailImage productDetailImages,
                         double savePercent, double originalPrice, double price, int quantity, int minQuantity, int maxQuantity, TheOneCardProductDetail theOneCardDetailList,
                         List<ProductDetailPromotion> detailPromotionList, ProductDetailOption productDetailOption, SpecDao specDao) {
        this.slug = slug;
        this.brand = brand;
        this.productName = productName;
        this.productCode = productCode;
        this.bu = bu;
        this.productImageList = productDetailImages;
        this.savePercent = savePercent;
        this.originalPrice = originalPrice;
        this.price = price;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.theOneCardDetailList = theOneCardDetailList;
        this.detailPromotionList = detailPromotionList;
        this.productDetailOption = productDetailOption;
        this.mSpecDao = specDao;
    }

    protected ProductDetail(Parcel in) {
        viewTypeId = in.readInt();
        masterId = in.readInt();
        slug = in.readString();
        brand = in.readString();
        productName = in.readString();
        productNameEN = in.readString();
        urlName = in.readString();
        urlNameEN = in.readString();
        productImageList = in.readParcelable(ProductDetailImage.class.getClassLoader());
        savePercent = in.readDouble();
        originalPrice = in.readDouble();
        price = in.readDouble();
        quantity = in.readInt();
        minQuantity = in.readInt();
        maxQuantity = in.readInt();
        productCode = in.readString();
        description = in.readString();
        descriptionEN = in.readString();
        //descriptionHtml = in.readString();
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
        stockAvailable = in.readInt();
        mRecommend = in.readParcelable(Recommend.class.getClassLoader());
        theOneCardDetailList = in.readParcelable(TheOneCardProductDetail.class.getClassLoader());
        detailPromotionList = in.createTypedArrayList(ProductDetailPromotion.CREATOR);
        productDetailOption = in.readParcelable(ProductDetailOption.class.getClassLoader());
        mSpecDao = in.readParcelable(SpecDao.class.getClassLoader());
        mProductDetailImageItems = in.createTypedArrayList(ProductDetailImageItem.CREATOR);
        mProductRelatedLists = in.createTypedArrayList(ProductRelatedList.CREATOR);
        mExtensionProductDetail = in.readParcelable(ExtensionProductDetail.class.getClassLoader());
        mCustomAttributesProductDetails = in.createTypedArrayList(CustomAttributesProductDetail.CREATOR);
    }

    public void replaceProduct(ProductDetailOptionItem productDetailOptionItem) {
        this.productCode = productDetailOptionItem.getProductId();
        this.productName = productDetailOptionItem.getProductName();
        this.price = productDetailOptionItem.getPrice();
        this.originalPrice = productDetailOptionItem.getOriginalPrice();
        this.stockAvailable = productDetailOptionItem.getStockAvailable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeInt(masterId);
        dest.writeString(slug);
        dest.writeString(brand);
        dest.writeString(productName);
        dest.writeString(productNameEN);
        dest.writeString(urlName);
        dest.writeString(urlNameEN);
        dest.writeParcelable(productImageList, flags);
        dest.writeDouble(savePercent);
        dest.writeDouble(originalPrice);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeInt(minQuantity);
        dest.writeInt(maxQuantity);
        dest.writeString(productCode);
        dest.writeString(description);
        dest.writeString(descriptionEN);
        //dest.writeString(descriptionHtml);
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
        dest.writeInt(stockAvailable);
        dest.writeParcelable(mRecommend, flags);
        dest.writeParcelable(theOneCardDetailList, flags);
        dest.writeList(detailPromotionList);
        dest.writeParcelable(productDetailOption, flags);
        dest.writeParcelable(mSpecDao, flags);
        dest.writeList(mProductDetailImageItems);
        dest.writeList(mProductRelatedLists);
        dest.writeParcelable(mExtensionProductDetail, flags);
        dest.writeTypedList(mCustomAttributesProductDetails);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    public ProductDetailImage getProductImageList() {
        if (productImageList == null) {
            List<ProductDetailImageItem> productDetailImageItems = new ArrayList<>();
            productDetailImageItems.add(new ProductDetailImageItem("1", ""));
            productDetailImageItems.add(new ProductDetailImageItem("2", ""));
            productDetailImageItems.add(new ProductDetailImageItem("3", ""));
            productDetailImageItems.add(new ProductDetailImageItem("4", ""));
            productDetailImageItems.add(new ProductDetailImageItem("5", ""));
            productDetailImageItems.add(new ProductDetailImageItem("6", ""));

            productImageList = new ProductDetailImage(4, productDetailImageItems);
        }
        return productImageList;
    }

    public void setProductImageList(ProductDetailImage productImageList) {
        this.productImageList = productImageList;
    }

    public double getSavePercent() {
        return savePercent;
    }

    public void setSavePercent(double savePercent) {
        this.savePercent = savePercent;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public TheOneCardProductDetail getTheOneCardDetailList() {
        return new TheOneCardProductDetail(7000, 2500);
    }

    public void setTheOneCardDetailList(TheOneCardProductDetail theOneCardDetailList) {
        this.theOneCardDetailList = theOneCardDetailList;
    }

    public List<ProductDetailPromotion> getDetailPromotionList() {

        List<ProductDetailPromotion> productDetailPromotionList = new ArrayList<>();
        productDetailPromotionList.add(new ProductDetailPromotion("1", "http://www.uppic.org/image-4E3E_57D7C9D3.jpg"));
        productDetailPromotionList.add(new ProductDetailPromotion("2", "http://www.uppic.org/image-4391_57D7C9D3.jpg"));
        productDetailPromotionList.add(new ProductDetailPromotion("3", "http://www.uppic.org/image-4E3E_57D7C9D3.jpg"));
        return detailPromotionList = productDetailPromotionList;
    }

    public void setDetailPromotionList(List<ProductDetailPromotion> detailPromotionList) {
        this.detailPromotionList = detailPromotionList;
    }

    public ProductDetailOption getProductDetailOption() {
        return productDetailOption;
    }

    public void setProductDetailOption(ProductDetailOption productDetailOption) {
        this.productDetailOption = productDetailOption;
    }

    public SpecDao getSpecDao() {
        return mSpecDao;
    }

    public void setSpecDao(SpecDao specDao) {
        mSpecDao = specDao;
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

    public Recommend getRecommend() {
        return mRecommend;
    }

    public void setRecommend(Recommend recommend) {
        mRecommend = recommend;
    }

    public List<ProductDetailImageItem> getProductDetailImageItems() {
        return mProductDetailImageItems;
    }

    public void setProductDetailImageItems(List<ProductDetailImageItem> productDetailImageItems) {
        mProductDetailImageItems = productDetailImageItems;
    }

    public List<ProductRelatedList> getProductRelatedLists() {
        return mProductRelatedLists;
    }

    public void setProductRelatedLists(List<ProductRelatedList> productRelatedLists) {
        mProductRelatedLists = productRelatedLists;
    }

    public String getDisplayOldPrice(String unit) {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(originalPrice));
    }

    public String getDisplayNewPrice(String unit) {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(price));
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ExtensionProductDetail getExtensionProductDetail() {
        return mExtensionProductDetail;
    }

    public void setExtensionProductDetail(ExtensionProductDetail extensionProductDetail) {
        mExtensionProductDetail = extensionProductDetail;
    }

    public List<CustomAttributesProductDetail> getCustomAttributesProductDetails() {
        return mCustomAttributesProductDetails;
    }

    public void setCustomAttributesProductDetails(List<CustomAttributesProductDetail> customAttributesProductDetails) {
        mCustomAttributesProductDetails = customAttributesProductDetails;
    }
}
