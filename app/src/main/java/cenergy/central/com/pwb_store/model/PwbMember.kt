package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class PwbMember(
        var id: Long? = 0,
        @SerializedName("customer_id")
        var customerId: Long? = 0,
        @SerializedName("region_id")
        var regionId: Int? = 0,
        var countryId: String? = "",
        var street: ArrayList<String>? = arrayListOf(),
        var telephone: String? = "",
        var postcode: String? = "",
        var city: String? = "",
        var firstname: String? = "",
        var lastname: String? = "",
        @SerializedName("default_shipping")
        var defaultShipping: Boolean? = false,
        @SerializedName("default_billing")
        var defaultBilling: Boolean? = false,
        var subAddress: MemberSubAddress? = null,
        var email: String? = ""
) {
    fun getDisplayName(): String {
        return "$firstname $lastname"
    }
}

class MemberSubAddress(
        var houseNo: String = "",
        var building: String = "",
        var soi: String = "",
        var street: String = "",
        var district: String = "",
        var districtId: String = "",
        var subDistrict: String = "",
        var subDistrictId: String = "",
        var postcodeId: String = ""
)
