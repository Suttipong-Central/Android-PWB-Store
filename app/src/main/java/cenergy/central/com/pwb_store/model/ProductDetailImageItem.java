package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by napabhat on 10/26/2016 AD.
 */

public class ProductDetailImageItem implements IViewType, Parcelable {

    public static final Creator<ProductDetailImageItem> CREATOR = new Creator<ProductDetailImageItem>() {
        @Override
        public ProductDetailImageItem createFromParcel(Parcel in) {
            return new ProductDetailImageItem(in);
        }

        @Override
        public ProductDetailImageItem[] newArray(int size) {
            return new ProductDetailImageItem[size];
        }
    };
    private int viewTypeId;
    @SerializedName("LargeFullUrl")
    @Expose
    private String imgUrl;
    private String pId;
    private String slug;
    private boolean isSelected;

    public ProductDetailImageItem(){

    }

    public ProductDetailImageItem(String id, String imgUrl) {
        this.pId = id;
        this.imgUrl = imgUrl;
    }

    protected ProductDetailImageItem(Parcel in) {
        viewTypeId = in.readInt();
        imgUrl = in.readString();
        slug = in.readString();
        pId = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(imgUrl);
        dest.writeString(slug);
        dest.writeString(pId);
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
