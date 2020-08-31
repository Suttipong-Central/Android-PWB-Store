package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class Store(
        @SerializedName("id")
        var storeId: Long? = 0,
        @SerializedName("store_code")
        var storeCode: String? = "",
        @SerializedName("store_name")
        var storeName: String? = "",
        @SerializedName("number")
        var number: String? = "",
        var building: String? = "",
        var moo: String? = "",
        var soi: String? = "",
        var road: String? = "",
        @SerializedName("sub_district")
        var subDistrict: String? = "",
        var district: String? = "",
        var province: String? = "",
        @SerializedName("postal_code")
        var postalCode: String? = "",
        @SerializedName("created_at")
        var createdAt: String? = "",
        @SerializedName("updated_at")
        var updateAt: String? = "",
        @SerializedName("retailer_id")
        var retailerId: String = ""):RealmObject(){
        companion object {
                const val SELLER_CODE = "seller_code"
        }
}