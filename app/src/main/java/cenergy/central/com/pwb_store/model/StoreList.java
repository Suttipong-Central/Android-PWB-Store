package cenergy.central.com.pwb_store.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by napabhat on 8/29/2017 AD.
 */

public class StoreList implements IViewType,Parcelable{
    private int viewTypeId;
    @SerializedName(value = "StoreId", alternate = "store_code")
    @Expose
    private String storeId;
    @SerializedName(value = "StoreName", alternate = "name")
    @Expose
    private String storeName;
//    @SerializedName("StoreNameEN")
//    @Expose
//    private String storeNameEN;
    @SerializedName(value = "OpenTime", alternate = "open_time")
    @Expose
    private String openTime;
//    @SerializedName("Description")
//    @Expose
//    private String description;
    @SerializedName(value = "StoreAddrNo", alternate = "address")
    @Expose
    private String storeAddrNo;
//    @SerializedName("StoreAddrNoEN")
//    @Expose
//    private String storeAddrNoEN;
//    @SerializedName("StoreAddrVillages")
//    @Expose
//    private String storeAddrVillages;
//    @SerializedName("StoreAddrVillagesEN")
//    @Expose
//    private String storeAddrVillagesEN;
    @SerializedName(value = "StoreAddrStreet", alternate = "street")
    @Expose
    private String storeAddrStreet;
//    @SerializedName("StoreAddrStreetEN")
//    @Expose
//    private String storeAddrStreetEN;
//    @SerializedName("StoreAddrSoi")
//    @Expose
//    private String storeAddrSoi;
//    @SerializedName("StoreAddrSoiEN")
//    @Expose
//    private String storeAddrSoiEN;
    @SerializedName(value = "SubdistrictName", alternate = "sub_district")
    @Expose
    private String subDistrictName;
//    @SerializedName("SubdistrictNameEN")
//    @Expose
//    private String subDistrictNameEN;
    @SerializedName(value = "DistrictName", alternate = "district")
    @Expose
    private String districtName;
//    @SerializedName("DistrictNameEN")
//    @Expose
//    private String districtNameEN;
    @SerializedName(value = "ProvinceName", alternate = "province")
    @Expose
    private String provinceName;
//    @SerializedName("ProvinceNameEN")
//    @Expose
//    private String provinceNameEN;
    @SerializedName(value = "Postcode", alternate = "postcode")
    @Expose
    private String postCode;
//    @SerializedName("Fax")
//    @Expose
//    private String fax;
    @SerializedName(value = "Telephone", alternate = "telephone")
    @Expose
    private String telephone;
    @SerializedName(value = "EMail", alternate = "email")
    @Expose
    private String email;
//    @SerializedName("Latitude")
//    @Expose
//    private double latitude;
//    @SerializedName("Longitude")
//    @Expose
//    private double longitude;
//    @SerializedName("StockOnHand")
//    @Expose
//    private int stockOnHand;
    private boolean isSelected;

    protected StoreList(Parcel in) {
        viewTypeId = in.readInt();
        storeId = in.readString();
        storeName = in.readString();
        //storeNameEN = in.readString();
        openTime = in.readString();
        //description = in.readString();
        storeAddrNo = in.readString();
//        storeAddrNoEN = in.readString();
//        storeAddrVillages = in.readString();
//        storeAddrVillagesEN = in.readString();
//        storeAddrStreet = in.readString();
//        storeAddrStreetEN = in.readString();
//        storeAddrSoi = in.readString();
//        storeAddrSoiEN = in.readString();
        subDistrictName = in.readString();
//        subDistrictNameEN = in.readString();
        districtName = in.readString();
//        districtNameEN = in.readString();
        provinceName = in.readString();
//        provinceNameEN = in.readString();
        postCode = in.readString();
//        fax = in.readString();
        telephone = in.readString();
        email = in.readString();
//        latitude = in.readDouble();
//        longitude = in.readDouble();
//        stockOnHand = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(storeId);
        dest.writeString(storeName);
        //dest.writeString(storeNameEN);
        dest.writeString(openTime);
        //dest.writeString(description);
        dest.writeString(storeAddrNo);
//        dest.writeString(storeAddrNoEN);
//        dest.writeString(storeAddrVillages);
//        dest.writeString(storeAddrVillagesEN);
//        dest.writeString(storeAddrStreet);
//        dest.writeString(storeAddrStreetEN);
//        dest.writeString(storeAddrSoi);
//        dest.writeString(storeAddrSoiEN);
        dest.writeString(subDistrictName);
//        dest.writeString(subDistrictNameEN);
        dest.writeString(districtName);
//        dest.writeString(districtNameEN);
        dest.writeString(provinceName);
//        dest.writeString(provinceNameEN);
        dest.writeString(postCode);
//        dest.writeString(fax);
        dest.writeString(telephone);
        dest.writeString(email);
//        dest.writeDouble(latitude);
//        dest.writeDouble(longitude);
//        dest.writeInt(stockOnHand);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    public static final Creator<StoreList> CREATOR = new Creator<StoreList>() {
        @Override
        public StoreList createFromParcel(Parcel in) {
            return new StoreList(in);
        }

        @Override
        public StoreList[] newArray(int size) {
            return new StoreList[size];
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
        this.viewTypeId =id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getStoreAddrNo() {
        return storeAddrNo;
    }

    public void setStoreAddrNo(String storeAddrNo) {
        this.storeAddrNo = storeAddrNo;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubDistrictName() {
        return subDistrictName;
    }

    public void setSubDistrictName(String subDistrictName) {
        this.subDistrictName = subDistrictName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStoreAddrStreet() {
        return storeAddrStreet;
    }

    public void setStoreAddrStreet(String storeAddrStreet) {
        this.storeAddrStreet = storeAddrStreet;
    }
}
