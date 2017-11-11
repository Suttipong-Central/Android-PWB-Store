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

public class Extension implements IViewType,Parcelable {
    private int viewTypeId;

    @SerializedName(value = "MediumFullUrl", alternate = "image")
    @Expose
    private String imageUrl;

    @SerializedName(value = "Description" , alternate = "description")
    @Expose
    private String description;
    @SerializedName("by_store")
    @Expose
    private List<ProductListStore> mProductListStores = new ArrayList<>();
//    @SerializedName("brand")
//    @Expose
    private String brand;

    protected Extension(Parcel in) {
        viewTypeId = in.readInt();
        mProductListStores = in.createTypedArrayList(ProductListStore.CREATOR);
        imageUrl = in.readString();
        description = in.readString();
        brand = in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeTypedList(mProductListStores);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(brand);
    }


    public static final Creator<Extension> CREATOR = new Creator<Extension>() {
        @Override
        public Extension createFromParcel(Parcel in) {
            return new Extension(in);
        }

        @Override
        public Extension[] newArray(int size) {
            return new Extension[size];
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

    public List<ProductListStore> getProductListStores() {
        return mProductListStores;
    }

    public void setProductListStores(List<ProductListStore> productListStores) {
        mProductListStores = productListStores;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
