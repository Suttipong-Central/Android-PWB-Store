package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

data class DeliveryInfo(
        @SerializedName("shipping_method")
        var shippingMethod: String = "",
        @SerializedName("delivery_lead_time_message")
        var deliveryTimeMessage: String = "",
        @SerializedName("delivery_free_message")
        var deliveryFreeMessage: String = "",
        @SerializedName("shipping_fee")
        var shippingFee: String = "",
        @SerializedName("shipping_method_label")
        var shippingMethodLabel: String = ""
)