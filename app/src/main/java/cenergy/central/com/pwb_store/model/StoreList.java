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
    @SerializedName("store_code")
    @Expose
    private String storeId;
    @SerializedName("name")
    @Expose
    private String storeName;
    @SerializedName("open_time")
    @Expose
    private String openTime;
    @SerializedName("address")
    @Expose
    private String storeAddrNo;
    @SerializedName("street")
    @Expose
    private String storeAddrStreet;
    @SerializedName("sub_district")
    @Expose
    private String subDistrictName;
    @SerializedName("district")
    @Expose
    private String districtName;
    @SerializedName("province")
    @Expose
    private String provinceName;
    @SerializedName("postcode")
    @Expose
    private String postCode;
    @SerializedName("telephone")
    @Expose
    private String telephone;
    @SerializedName("email")
    @Expose
    private String email;
    private boolean isSelected;

    protected StoreList(Parcel in) {
        viewTypeId = in.readInt();
        storeId = in.readString();
        storeName = in.readString();
        openTime = in.readString();
        storeAddrNo = in.readString();
        subDistrictName = in.readString();
        districtName = in.readString();
        provinceName = in.readString();
        postCode = in.readString();
        telephone = in.readString();
        email = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewTypeId);
        dest.writeString(storeId);
        dest.writeString(storeName);
        dest.writeString(openTime);
        dest.writeString(storeAddrNo);
        dest.writeString(subDistrictName);
        dest.writeString(districtName);
        dest.writeString(provinceName);
        dest.writeString(postCode);
        dest.writeString(telephone);
        dest.writeString(email);
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
