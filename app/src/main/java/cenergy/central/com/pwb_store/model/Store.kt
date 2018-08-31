package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class Store(
        @SerializedName("id")
        var storeId: Long = 0,
        var storeCode: String = "",
        var storeName: String = "",
        @SerializedName("number")
        var houseNo: String = "",
        var building: String = "",
        var moo: String = "",
        var soi: String = "",
        var road: String = "",
        var subDistricrt: String = "",
        var district: String = "",
        var province: String = "",
        var postalCode: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("updated_at")
        var updateAt: String = ""):RealmObject()