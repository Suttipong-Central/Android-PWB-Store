package cenergy.central.com.pwb_store.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/1/2018 AD.
 */

public class SkuDataRequest implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("itemNo")
    @Expose
    private int itemNo;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("storeCode")
    @Expose
    private String storeCode;
    @SerializedName("fromStore")
    @Expose
    private String fromStore;

    public SkuDataRequest(String id, int itemNo, String sku, int quantity, String storeCode, String fromStore){
       this.id = id;
       this.itemNo = itemNo;
       this.sku = sku;
       this.quantity = quantity;
       this.storeCode = storeCode;
       this.fromStore = fromStore;
    }
    protected SkuDataRequest(Parcel in) {
        id = in.readString();
        itemNo = in.readInt();
        sku = in.readString();
        quantity = in.readInt();
        storeCode = in.readString();
        fromStore = in.readString();
    }

    public static final Creator<SkuDataRequest> CREATOR = new Creator<SkuDataRequest>() {
        @Override
        public SkuDataRequest createFromParcel(Parcel in) {
            return new SkuDataRequest(in);
        }

        @Override
        public SkuDataRequest[] newArray(int size) {
            return new SkuDataRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(itemNo);
        parcel.writeString(sku);
        parcel.writeInt(quantity);
        parcel.writeString(storeCode);
        parcel.writeString(fromStore);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getFromStore() {
        return fromStore;
    }

    public void setFromStore(String fromStore) {
        this.fromStore = fromStore;
    }
}
