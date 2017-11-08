package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napabhat on 11/1/2017 AD.
 */

public class ExtensionProductDetail implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("image")
    @Expose
    private String imageUrl;

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("by_store")
    @Expose
    private List<ProductDetailStore> mProductDetailStores = new ArrayList<>();
    private ProductDetailImage productImageList;
    @SerializedName("promotion")
    @Expose
    private List<PromotionItem> mPromotionItems = new ArrayList<>();
    private List<PromotionPaymentItem> mPromotionPaymentItems = new ArrayList<>();

    protected ExtensionProductDetail(Parcel in) {
        viewTypeId = in.readInt();
        mProductDetailStores = in.createTypedArrayList(ProductDetailStore.CREATOR);
        imageUrl = in.readString();
        description = in.readString();
        productImageList = in.readParcelable(ProductDetailImage.class.getClassLoader());
        mPromotionItems = in.createTypedArrayList(PromotionItem.CREATOR);
        mPromotionPaymentItems = in.createTypedArrayList(PromotionPaymentItem.CREATOR);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mProductDetailStores);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeParcelable(productImageList, flags);
        dest.writeTypedList(mPromotionItems);
        dest.writeTypedList(mPromotionPaymentItems);
    }


    public static final Creator<ExtensionProductDetail> CREATOR = new Creator<ExtensionProductDetail>() {
        @Override
        public ExtensionProductDetail createFromParcel(Parcel in) {
            return new ExtensionProductDetail(in);
        }

        @Override
        public ExtensionProductDetail[] newArray(int size) {
            return new ExtensionProductDetail[size];
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

    public List<ProductDetailStore> getProductDetailStores() {
        return mProductDetailStores;
    }

    public void setProductDetailStores(List<ProductDetailStore> productListStores) {
        mProductDetailStores = productListStores;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductDetailImage getProductImageList() {
        if (productImageList == null) {
            List<ProductDetailImageItem> productDetailImageItems = new ArrayList<>();
            productDetailImageItems.add(new ProductDetailImageItem("1", imageUrl));
            productDetailImageItems.add(new ProductDetailImageItem("2", ""));
            productDetailImageItems.add(new ProductDetailImageItem("3", ""));
            productDetailImageItems.add(new ProductDetailImageItem("4", ""));
            productDetailImageItems.add(new ProductDetailImageItem("5", ""));
            productDetailImageItems.add(new ProductDetailImageItem("6", ""));

            productImageList = new ProductDetailImage(4, productDetailImageItems);
        }
        return productImageList;
    }

    public List<PromotionItem> getPromotionItems() {
        return mPromotionItems;
    }

    public void setPromotionItems(List<PromotionItem> promotionItems) {
        mPromotionItems = promotionItems;
    }

    public List<PromotionPaymentItem> getPromotionPaymentItems() {
        return mPromotionPaymentItems;
    }

    public void setPromotionPaymentItems(List<PromotionPaymentItem> promotionPaymentItems) {
        mPromotionPaymentItems = promotionPaymentItems;
    }
}
