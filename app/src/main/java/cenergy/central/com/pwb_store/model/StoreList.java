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
    @SerializedName("StoreId")
    @Expose
    private String storeId;
    @SerializedName("StoreName")
    @Expose
    private String storeName;
    @SerializedName("StoreNameEN")
    @Expose
    private String storeNameEN;
    @SerializedName("OpenTime")
    @Expose
    private String openTime;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("StoreAddrNo")
    @Expose
    private String storeAddrNo;
    @SerializedName("StoreAddrNoEN")
    @Expose
    private String storeAddrNoEN;
    @SerializedName("StoreAddrVillages")
    @Expose
    private String storeAddrVillages;
    @SerializedName("StoreAddrVillagesEN")
    @Expose
    private String storeAddrVillagesEN;
    @SerializedName("StoreAddrStreet")
    @Expose
    private String storeAddrStreet;
    @SerializedName("StoreAddrStreetEN")
    @Expose
    private String storeAddrStreetEN;
    @SerializedName("StoreAddrSoi")
    @Expose
    private String storeAddrSoi;
    @SerializedName("StoreAddrSoiEN")
    @Expose
    private String storeAddrSoiEN;
    @SerializedName("SubdistrictName")
    @Expose
    private String subDistrictName;
    @SerializedName("SubdistrictNameEN")
    @Expose
    private String subDistrictNameEN;
    @SerializedName("DistrictName")
    @Expose
    private String districtName;
    @SerializedName("DistrictNameEN")
    @Expose
    private String districtNameEN;
    @SerializedName("ProvinceName")
    @Expose
    private String provinceName;
    @SerializedName("ProvinceNameEN")
    @Expose
    private String provinceNameEN;
    @SerializedName("Postcode")
    @Expose
    private String postCode;
    @SerializedName("Fax")
    @Expose
    private String fax;
    @SerializedName("Telephone")
    @Expose
    private String telephone;
    @SerializedName("EMail")
    @Expose
    private String email;
    @SerializedName("Latitude")
    @Expose
    private double latitude;
    @SerializedName("Longitude")
    @Expose
    private double longitude;
    @SerializedName("StockOnHand")
    @Expose
    private int stockOnHand;
    private boolean isSelected;

    protected StoreList(Parcel in) {
        viewTypeId = in.readInt();
        storeId = in.readString();
        storeName = in.readString();
        storeNameEN = in.readString();
        openTime = in.readString();
        description = in.readString();
        storeAddrNo = in.readString();
        storeAddrNoEN = in.readString();
        storeAddrVillages = in.readString();
        storeAddrVillagesEN = in.readString();
        storeAddrStreet = in.readString();
        storeAddrStreetEN = in.readString();
        storeAddrSoi = in.readString();
        storeAddrSoiEN = in.readString();
        subDistrictName = in.readString();
        subDistrictNameEN = in.readString();
        districtName = in.readString();
        districtNameEN = in.readString();
        provinceName = in.readString();
        provinceNameEN = in.readString();
        postCode = in.readString();
        fax = in.readString();
        telephone = in.readString();
        email = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        stockOnHand = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(storeId);
        dest.writeString(storeName);
        dest.writeString(storeNameEN);
        dest.writeString(openTime);
        dest.writeString(description);
        dest.writeString(storeAddrNo);
        dest.writeString(storeAddrNoEN);
        dest.writeString(storeAddrVillages);
        dest.writeString(storeAddrVillagesEN);
        dest.writeString(storeAddrStreet);
        dest.writeString(storeAddrStreetEN);
        dest.writeString(storeAddrSoi);
        dest.writeString(storeAddrSoiEN);
        dest.writeString(subDistrictName);
        dest.writeString(subDistrictNameEN);
        dest.writeString(districtName);
        dest.writeString(districtNameEN);
        dest.writeString(provinceName);
        dest.writeString(provinceNameEN);
        dest.writeString(postCode);
        dest.writeString(fax);
        dest.writeString(telephone);
        dest.writeString(email);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(stockOnHand);
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

    public String getStoreNameEN() {
        return storeNameEN;
    }

    public void setStoreNameEN(String storeNameEN) {
        this.storeNameEN = storeNameEN;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreAddrNo() {
        return storeAddrNo;
    }

    public void setStoreAddrNo(String storeAddrNo) {
        this.storeAddrNo = storeAddrNo;
    }

    public String getStoreAddrNoEN() {
        return storeAddrNoEN;
    }

    public void setStoreAddrNoEN(String storeAddrNoEN) {
        this.storeAddrNoEN = storeAddrNoEN;
    }

    public String getStoreAddrVillages() {
        return storeAddrVillages;
    }

    public void setStoreAddrVillages(String storeAddrVillages) {
        this.storeAddrVillages = storeAddrVillages;
    }

    public String getStoreAddrVillagesEN() {
        return storeAddrVillagesEN;
    }

    public void setStoreAddrVillagesEN(String storeAddrVillagesEN) {
        this.storeAddrVillagesEN = storeAddrVillagesEN;
    }

    public String getStoreAddrStreet() {
        return storeAddrStreet;
    }

    public void setStoreAddrStreet(String storeAddrStreet) {
        this.storeAddrStreet = storeAddrStreet;
    }

    public String getStoreAddrStreetEN() {
        return storeAddrStreetEN;
    }

    public void setStoreAddrStreetEN(String storeAddrStreetEN) {
        this.storeAddrStreetEN = storeAddrStreetEN;
    }

    public String getStoreAddrSoi() {
        return storeAddrSoi;
    }

    public void setStoreAddrSoi(String storeAddrSoi) {
        this.storeAddrSoi = storeAddrSoi;
    }

    public String getStoreAddrSoiEN() {
        return storeAddrSoiEN;
    }

    public void setStoreAddrSoiEN(String storeAddrSoiEN) {
        this.storeAddrSoiEN = storeAddrSoiEN;
    }

    public String getSubDistrictName() {
        return subDistrictName;
    }

    public void setSubDistrictName(String subDistrictName) {
        this.subDistrictName = subDistrictName;
    }

    public String getSubDistrictNameEN() {
        return subDistrictNameEN;
    }

    public void setSubDistrictNameEN(String subDistrictNameEN) {
        this.subDistrictNameEN = subDistrictNameEN;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictNameEN() {
        return districtNameEN;
    }

    public void setDistrictNameEN(String districtNameEN) {
        this.districtNameEN = districtNameEN;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceNameEN() {
        return provinceNameEN;
    }

    public void setProvinceNameEN(String provinceNameEN) {
        this.provinceNameEN = provinceNameEN;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(int stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
