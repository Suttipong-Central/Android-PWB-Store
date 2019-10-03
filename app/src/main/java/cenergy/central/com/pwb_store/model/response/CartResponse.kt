package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.CartItem
import com.google.gson.annotations.SerializedName

class CartResponse(
        var items: List<CartItem>,
        @SerializedName("extension_attributes")
        var extension: CartExtension
)

class CartExtension(
        var retailer: Retailer? = null
)

class Retailer(
        @SerializedName("extension_attributes")
        var extension: RetailerExtension? = null,
        var id: Long,
        var name: String,
        @SerializedName("is_active")
        var active: Boolean,
        @SerializedName("seller_code")
        var sellerCode: String,
        @SerializedName("created_at")
        var createAt: String,
        @SerializedName("updated_at")
        var updateAt: String
)

class RetailerExtension(
        @SerializedName("ispu_promise_delivery")
        var deliverlyPromiseIspu: String? = ""
)