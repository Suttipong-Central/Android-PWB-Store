package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class DeliveryOption(
        @SerializedName("carrier_code")
        var carrierCode: String = "",
        @SerializedName("method_code")
        var methodCode: String = "",
        @SerializedName("carrier_title")
        var carrierTitle: String = "",
        @SerializedName("method_title")
        var methodTitle: String = "",
        var amount: Int = 0,
        @SerializedName("base_amount")
        var baseAmount: Int = 0,
        var available: Boolean = false,
        @SerializedName("extension_attributes")
        var extension: DeliveryExtension,
        @SerializedName("error_message")
        var errorMessage: String = "",
        @SerializedName("price_excl_tax")
        var priceExcludeTax: String = "",
        @SerializedName("price_incl_tax")
        var priceIncludeTax: String = ""
)

data class DeliveryExtension(@SerializedName("pickup_locations")
                             var pickupLocations: List<PickupLocation> = arrayListOf(),
                             @SerializedName("shipping_slot_list")
                             var shippingSlots: ArrayList<ShippingSlot> = arrayListOf())

data class ShippingSlot(
        val id: String? = null,
        @SerializedName("date_time_from")
        val dateTimeFrom: String? = "",
        @SerializedName("date_time_to")
        val dateTimeTo: String? = "",
        @SerializedName("extension_attributes")
        val slotExtension: SlotExtension? = null):Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(SlotExtension::class.java.classLoader))
    fun getTimeDescription(): String {
        return if (dateTimeFrom != null && dateTimeTo != null &&
                dateTimeFrom.isNotEmpty() && dateTimeTo.isNotEmpty()) {
            val timeFrom = dateTimeFrom.split(" ")[1]
            val timeTo = dateTimeTo.split(" ")[1]
            "$timeFrom - $timeTo" // "9:00 - 9:30"
        } else {
            ""
        }
    }

    fun getDate(): String {
        return if (dateTimeFrom != null && dateTimeFrom.isNotEmpty()) {
            dateTimeFrom.split(" ").first()
        } else {
            ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(dateTimeFrom)
        parcel.writeString(dateTimeTo)
        parcel.writeParcelable(slotExtension, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShippingSlot> {
        override fun createFromParcel(parcel: Parcel): ShippingSlot {
            return ShippingSlot(parcel)
        }

        override fun newArray(size: Int): Array<ShippingSlot?> {
            return arrayOfNulls(size)
        }
    }
}

data class SlotExtension(val daySlotId: Int):Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(daySlotId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SlotExtension> {
        override fun createFromParcel(parcel: Parcel): SlotExtension {
            return SlotExtension(parcel)
        }

        override fun newArray(size: Int): Array<SlotExtension?> {
            return arrayOfNulls(size)
        }
    }

}

data class PickupLocation(var id: String = "",
                          var code: String = "",
                          var name: String = "",
                          @SerializedName("display_order")
                          var displayOrder: String = "",
                          @SerializedName("address_line1")
                          var address: String = "",
                          var district: String = "",
                          var province: String = "",
                          @SerializedName("region_id")
                          var regionId: String = "",
                          @SerializedName("postal_code")
                          var postcode: String = "",
                          var telephone: String? = "",
                          @SerializedName("lat")
                          var latitude: String = "",
                          @SerializedName("long")
                          var longitude: String = "",
                          @SerializedName("pickup_fee")
                          var pickupFee: String = "",
                          @SerializedName("pos_handling_fee")
                          var posHandlingFee: String = "",
                          @SerializedName("extension_attributes")
                          var extension: PickupExtension
) {
    fun asBranch(): Branch {
        return Branch(storeId = id, street = address, city = extension.pickupAddressInfo.region,
                phone = telephone
                        ?: "", postcode = postcode, storeName = name, centralStoreCode = code,
                latitude = latitude, longitude = longitude)
    }
}

data class PickupExtension(@SerializedName("additional_address_info")
                           var pickupAddressInfo: PickupAddressInfo)

data class PickupAddressInfo(@SerializedName("subdistrict")
                             var subDistrict: String = "",
                             @SerializedName("subdistrict_id")
                             var subDistrictId: String,
                             var district: String = "",
                             @SerializedName("districtId")
                             var districtId: String,
                             @SerializedName("region_name")
                             var region: String = "",
                             @SerializedName("region_id")
                             var regionId: String)