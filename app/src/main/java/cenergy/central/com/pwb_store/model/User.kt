package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class User(
        @SerializedName("id")
        var userId: Long = 0,
        var name: String = "",
        @SerializedName("staff_id")
        var staffId: String = "",
        @SerializedName("store_id")
        var storeId: Long = 0,
        var email: String = "",
        var username: String = "",
        @SerializedName("create_at")
        var userCreate: String = "",
        @SerializedName("update_at")
        var userUpdate: String = ""
)
