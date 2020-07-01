package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.model.response.ItemExtension
import com.google.gson.annotations.SerializedName

data class CartItem(
        @SerializedName("item_id")
        var id: Long? = 0,
        var sku: String? = "",
        var qty: Int? = 0,
        var name: String? = "",
        var price: Double? = 0.0,
        @SerializedName("product_type")
        var type: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "",
        @SerializedName("extension_attributes")
        var extension: ItemExtension? = null)