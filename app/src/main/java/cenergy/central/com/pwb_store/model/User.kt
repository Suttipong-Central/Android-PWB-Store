package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class User(
        @SerializedName("id")
        var userId: Long = 0,
        var name: String = "",
        @SerializedName("staff_id")
        var staffId: String? = "",
        @SerializedName("store_id")
        var storeId: Long? = 0,
        var email: String? = "",
        var username: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("is_chat_shop_user")
        var isChatAndShopUser: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        var userLevel: Long = 0) : RealmObject()