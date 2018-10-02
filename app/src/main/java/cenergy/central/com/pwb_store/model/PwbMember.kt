package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Anuphap Suwannamas on 1/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class PwbMember(
        var id: Long? = 0,
        var firstname: String? = "",
        var lastname: String? = "",
        var email: String? = "",
        @SerializedName("the_one_card_no")
        var t1cCardNo: String? = "",
        var addresses: List<MemberAddress>? = arrayListOf()
) {
    fun getDisplayName(): String {
        return "$firstname $lastname"
    }

    fun getShipping(): MemberAddress {
        var pwbMemberAddress: MemberAddress? = null
        if (addresses != null && addresses!!.isNotEmpty()){
            addresses?.forEach {
                if(it.defaultShipping!!){
                    pwbMemberAddress = it
                }
            }
        }
        return pwbMemberAddress?:addresses!![0]
    }

    fun getDefualtBillingAddress(): Int {
        var index = 0
        if(addresses != null && addresses!!.isNotEmpty()){
            for (i in addresses!!.indices){
                if (addresses!![i].defaultShipping!!){
                    index = i
                }
            }
        }
        return index
    }
}

class MemberAddress(
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
        var subAddress: MemberSubAddress? = null)

class MemberSubAddress(
        var houseNo: String? = "",
        var district: String? = "",
        var districtId: String? = "",
        var subDistrict: String? = "",
        var subDistrictId: String? = "",
        var postcodeId: String? = ""
)
//
//class PwbMemberDeserializer : JsonDeserializer<MemberSubAddress> {
//    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): MemberSubAddress {
//        var houseNo: String? = ""
//        var district: String? = ""
//        var districtId: String? = ""
//        var subDistrict: String? = ""
//        var subDistrictId: String? = ""
//        var postcodeId: String? = ""
//
//        val jsonObject = json.asJsonObject
//        when (jsonObject.get("attribute_code").asString) {
//            "house_no" -> {
//                houseNo = jsonObject.get("value").asString
//            }
//        }
//        return MemberSubAddress(houseNo, district, districtId, subDistrict, subDistrictId, postcodeId)
//    }
//}
//
//class PwbMemberSerializer : JsonSerializer<MemberSubAddress> {
//    override fun serialize(src: MemberSubAddress, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        val jsonObject = JsonObject()
//        when (jsonObject.get("attribute_code").asString) {
//            "house_no" -> {
//                jsonObject.addProperty("house_no", src.houseNo)
//            }
//        }
//
//        return jsonObject
//    }
//}