package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class LoginUserResponse(
        var id: Long = 0,
        @SerializedName("user_id")
        var userId: Long = 0,
        @SerializedName("staff_id")
        var staffId: String = "",
        @SerializedName("level_id")
        var levelId: Long = 0
): RealmObject()

open class UserBranch(
        var items: RealmList<Seller> = RealmList(),
        @SerializedName("search_criteria")
        var criteria: String? = "",
        @SerializedName("total_count")
        var total: Int = 0
): RealmObject()

open class Seller(
        @SerializedName("seller_code")
        var code: String = ""
): RealmObject()