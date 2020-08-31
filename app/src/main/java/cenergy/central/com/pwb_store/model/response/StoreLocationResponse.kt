package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName


class StoreLocationResponse(
        var items: List<StoreLocation>? = null
)

class StoreLocation(
        @SerializedName("extension_attributes")
        var extension: StoreLocationExtension? = null,
        var id: Int = 223, // Default ID form K.Lee
        var name: String? = null,
        @SerializedName("is_active")
        var isActive: Boolean = false,
        @SerializedName("seller_code")
        var sellerCode: String? = null,
        @SerializedName("created_at")
        var createdAt: String? = null,
        @SerializedName("updated_at")
        var updatedAt: String? = null,
        @SerializedName("attribute_set_name")
        var attributeSetName: String? = null
)

class StoreLocationExtension(
        var address: AddressInfo? = null,
        @SerializedName("has_define_specific_sku")
        var hasSpecificSku: Boolean
)

class AddressInfo(
        var id: String? = null,
        @SerializedName("retailer_id")
        var retailerId: String? = null,
        var coordinates: Coordinates? = null,
        var region: String? = null,
        @SerializedName("region_id")
        var regionId: Int = 0,
        @SerializedName("country_id")
        var countryId: String? = null,
        var postcode: String? = null,
        var city: String? = null,
        var street: List<String>? = null
)

class Coordinates(
        var latitude: Double = 0.toDouble(),
        var longitude: Double = 0.toDouble()
)