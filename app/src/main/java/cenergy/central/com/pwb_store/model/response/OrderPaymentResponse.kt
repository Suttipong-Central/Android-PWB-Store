package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

data class OrderPaymentResponse(
        @SerializedName("parent_id")
        var parentId: Int = 0,
        var method: String = "",
        @SerializedName("shipping_amount")
        var shippingAmount: Double = 0.0
)
