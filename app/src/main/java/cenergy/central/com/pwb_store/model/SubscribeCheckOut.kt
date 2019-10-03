package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class SubscribeCheckOut(
        @SerializedName("subscribecheckout")
        var checkout: String = "", // example@email.com
        var shippingDate: String? = null, // "2019-08-07"
        var shippingSlotInDay: String? = null, // "1"
        var shippingSlotDescription: String? = null, // "09:00-09:30"
        @SerializedName("pickup_store")
        var storePickup: StorePickup? = null
)

class StorePickup(
        @SerializedName("store_id")
        var storeId: String
)
