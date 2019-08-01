package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class Branch(
        @SerializedName("storepickup_id")
        var storeId: String = "",
        var street: String = "",
        var state: String = "",
        var city: String = "",
        @SerializedName("country_id")
        var countryId: String = "",
        var description: String = "",
        var email: String = "",
        var phone: String = "",
        @SerializedName("zipcode")
        var postcode: String = "",
        @SerializedName("store_name")
        var storeName: String = "",
        @SerializedName("central_store_code")
        var centralStoreCode: String = "",
        var status: String = "",
        var latitude: String = "",
        var longitude: String = "",
        @SerializedName("lead_time")
        var leadTime: String = "",
        var isActive: Int = 0,
        var sellerCode: String = "",
        var attrSetName: String = "",
        var createdAt: String = "",
        var updatedAt: String = "",
        var fax: String = "",
        var region: String = "",
        @SerializedName("region_id")
        var regionId: Int = 0,
        @SerializedName("region_code")
        var regionCode: String = ""): RealmObject()
{
        fun getBranchAddress(): String {
                return "$street, $city, $postcode"
        }
}