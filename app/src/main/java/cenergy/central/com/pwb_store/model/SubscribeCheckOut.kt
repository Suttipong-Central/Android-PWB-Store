package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class SubscribeCheckOut(
        @SerializedName("subscribecheckout")
        var checkout: String = "",
        var shippingDate: String = "",
        var shippingSlotInDay: String = "",
        var shippingSlotDescription: String = ""
) {
    companion object {
        fun createSubscribe(checkout: String, shippingDate: String, shippingSlotInDay: String, shippingSlotDescription: String): SubscribeCheckOut {
            return SubscribeCheckOut(checkout = checkout,
                    shippingDate = shippingDate,
                    shippingSlotInDay = shippingSlotInDay,
                    shippingSlotDescription = shippingSlotDescription)

        }
    }
}
