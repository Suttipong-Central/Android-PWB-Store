package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by Anuphap Suwannamas on 22/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class CartItem(
        @SerializedName("item_id")
        var id : Int? = 0,
        var sku: String? = "",
        var qty: Int? = 0,
        var name: String? = "",
        var price: Double? = 0.0,
        @SerializedName("product_type")
        var type: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "") : RealmObject()