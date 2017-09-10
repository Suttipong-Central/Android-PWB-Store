package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by napabhat on 10/25/2016 AD.
 */

public class ProductDetailPromotion implements IViewType, Parcelable {

    public static final Creator<ProductDetailPromotion> CREATOR = new Creator<ProductDetailPromotion>() {
        @Override
        public ProductDetailPromotion createFromParcel(Parcel in) {
            return new ProductDetailPromotion(in);
        }

        @Override
        public ProductDetailPromotion[] newArray(int size) {
            return new ProductDetailPromotion[size];
        }
    };
    private int viewTypeId;
    private String slug;
    private String imgUrl;

    public ProductDetailPromotion(String slug, String imgUrl) {
        this.slug = slug;
        this.imgUrl = imgUrl;
    }

    protected ProductDetailPromotion(Parcel in) {
        viewTypeId = in.readInt();
        slug = in.readString();
        imgUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(slug);
        dest.writeString(imgUrl);
    }

    @Override
    public int getViewTypeId() {
        return viewTypeId;
    }

    @Override
    public void setViewTypeId(int id) {
        this.viewTypeId = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
