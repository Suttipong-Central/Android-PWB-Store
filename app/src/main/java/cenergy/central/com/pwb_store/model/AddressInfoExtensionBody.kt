package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

data class AddressInfoExtensionBody(
        @SerializedName("subscribecheckout")
        val checkout: String = "", // example@email.com
        val shippingDate: String? = null, // "2019-08-07"
        val shippingSlotInDay: String? = null, // "1"
        val shippingSlotDescription: String? = null, // "09:00-09:30"
        @SerializedName("pickup_store")
        val storePickup: StorePickup? = null,
        @SerializedName("pickup_location_id")
        val pickupLocationId: String? = null,
        @SerializedName("picker_info")
        val pickerInfo: PickerInfo? = null
)

data class StorePickup(
        @SerializedName("store_id")
        val storeId: String
)

data class PickerInfo(
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("last_name")
        val lastName: String,
        val email: String,
        val telephone: String
)
