package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class Store(
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
        @SerializedName("create_at")
        var userCreate: String = "",
        @SerializedName("update_at")
        var userUpdate: String = ""
)