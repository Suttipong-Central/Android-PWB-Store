package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 22/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class CartItem(
        @PrimaryKey
        @SerializedName("item_id")
        var id: Int? = 0,
        var sku: String? = "",
        var qty: Int? = 0,
        var name: String? = "",
        var price: Double? = 0.0,
        @SerializedName("product_type")
        var type: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "") : RealmObject() {

    companion object {
        const val FIELD_ID = "id"
    }
}