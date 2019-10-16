package cenergy.central.com.pwb_store.model

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
) {
    companion object {
       fun getStorePickupIspu() : DeliveryOption {
           return DeliveryOption(carrierCode = "storepickup",
                   methodCode = "ispu",
                   carrierTitle = "Store Pickup",
                   methodTitle = "1 Hours Pickup",
                   extension = DeliveryExtension())
       }
    }
}

data class DeliveryExtension(@SerializedName("pickup_locations")
                             var pickupLocations: List<PickupLocation> = arrayListOf(),
                             @SerializedName("shipping_slot_list")
                             var shippingSlots: ArrayList<ShippingSlot> = arrayListOf())

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