package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class SubscribeCheckOut(
        @SerializedName("subscribecheckout")
        var checkout: String = "",
        var shippingDate: String? = null,
        var shippingSlotInDay: String? = null,
        var shippingSlotDescription: String? = null,
        @SerializedName("pickup_store")
        var storePickup: StorePickup? = null
)

class StorePickup(
        @SerializedName("store_id")
        var storeId: Int
)
