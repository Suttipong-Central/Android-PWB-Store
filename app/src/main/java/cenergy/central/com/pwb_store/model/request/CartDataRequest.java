package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 9/22/2017 AD.
 */

public class CartDataRequest implements Parcelable {
    @SerializedName("seq")
    @Expose
    private int seq;
    @SerializedName("productCode")
    @Expose
    private String productCode;
    @SerializedName("skuCode")
    @Expose
    private String skuCode;
    @SerializedName("quant")
    @Expose
    private int quant;
    @SerializedName("serviceType")
    @Expose
    private String serviceType;
    @SerializedName("productFromStore")
    @Expose
    private String productFromStore;
    @SerializedName("deliveryToStore")
    @Expose
    private String deliveryToStore;
    @SerializedName("deliveryByStore")
    @Expose
    private String deliveryByStore;

    protected CartDataRequest(Parcel in) {
        seq = in.readInt();
        productCode = in.readString();
        skuCode = in.readString();
        quant = in.readInt();
        serviceType = in.readString();
        productFromStore = in.readString();
        deliveryToStore = in.readString();
        deliveryByStore = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seq);
        dest.writeString(productCode);
        dest.writeString(skuCode);
        dest.writeInt(quant);
        dest.writeString(serviceType);
        dest.writeString(productFromStore);
        dest.writeString(deliveryToStore);
        dest.writeString(deliveryByStore);
    }

    public CartDataRequest(int seq, String productCode, String skuCode, int quant, String serviceType,
                           String productFromStore, String deliveryToStore, String deliveryByStore){
        this.seq = seq;
        this.productCode = productCode;
        this.skuCode = skuCode;
        this.quant = quant;
        this.serviceType = serviceType;
        this.productFromStore = productFromStore;
        this.deliveryToStore = deliveryToStore;
        this.deliveryByStore = deliveryByStore;
    }

    public static final Creator<CartDataRequest> CREATOR = new Creator<CartDataRequest>() {
        @Override
        public CartDataRequest createFromParcel(Parcel in) {
            return new CartDataRequest(in);
        }

        @Override
        public CartDataRequest[] newArray(int size) {
            return new CartDataRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProductFromStore() {
        return productFromStore;
    }

    public void setProductFromStore(String productFromStore) {
        this.productFromStore = productFromStore;
    }

    public String getDeliveryToStore() {
        return deliveryToStore;
    }

    public void setDeliveryToStore(String deliveryToStore) {
        this.deliveryToStore = deliveryToStore;
    }

    public String getDeliveryByStore() {
        return deliveryByStore;
    }

    public void setDeliveryByStore(String deliveryByStore) {
        this.deliveryByStore = deliveryByStore;
    }
}
