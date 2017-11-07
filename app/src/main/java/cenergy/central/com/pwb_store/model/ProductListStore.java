package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by napabhat on 11/1/2017 AD.
 */

public class ProductListStore implements IViewType,Parcelable {
    private int viewTypeId;
    @SerializedName("entity_id")
    @Expose
    private String id;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("barcode")
    @Expose
    private String barCode;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("special_price")
    @Expose
    private String specialPrice;
    @SerializedName("special_price_from")
    @Expose
    private String specialPriceFrom;
    @SerializedName("special_price_to")
    @Expose
    private String specialPriceTo;
    @SerializedName("stock_available")
    @Expose
    private String stockAvailable;
    @SerializedName("stock_on_hand")
    @Expose
    private String stockOnHand;

    protected ProductListStore(Parcel in) {
        viewTypeId = in.readInt();
        id = in.readString();
        sku = in.readString();
        storeId = in.readString();
        barCode = in.readString();
        price = in.readString();
        specialPrice = in.readString();
        specialPriceFrom = in.readString();
        specialPriceTo = in.readString();
        stockAvailable = in.readString();
        stockOnHand = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(id);
        dest.writeString(sku);
        dest.writeString(storeId);
        dest.writeString(barCode);
        dest.writeString(price);
        dest.writeString(specialPrice);
        dest.writeString(specialPriceFrom);
        dest.writeString(specialPriceTo);
        dest.writeString(stockAvailable);
        dest.writeString(stockOnHand);
    }

    public static final Creator<ProductListStore> CREATOR = new Creator<ProductListStore>() {
        @Override
        public ProductListStore createFromParcel(Parcel in) {
            return new ProductListStore(in);
        }

        @Override
        public ProductListStore[] newArray(int size) {
            return new ProductListStore[size];
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(String specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getSpecialPriceFrom() {
        return specialPriceFrom;
    }

    public void setSpecialPriceFrom(String specialPriceFrom) {
        this.specialPriceFrom = specialPriceFrom;
    }

    public String getSpecialPriceTo() {
        return specialPriceTo;
    }

    public void setSpecialPriceTo(String specialPriceTo) {
        this.specialPriceTo = specialPriceTo;
    }

    public String getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(String stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public String getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(String stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public String getDisplayOldPrice(String unit) {
        //return String.format(Locale.getDefault(), "%s %s", NumberFormat.getInstance(Locale.getDefault()).format(oldPrice), unit);
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(Double.parseDouble(price)));
    }

    public String getDisplayNewPrice(String unit) {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(Double.parseDouble(specialPrice)));
    }
}
