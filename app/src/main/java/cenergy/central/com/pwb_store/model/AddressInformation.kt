package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class AddressInformation(
        var city: String = "",
        var region: String = "",
        @SerializedName("regionId")
        var regionId: String = "",
        @SerializedName("regionCode")
        var regionCode: String = "",
        @SerializedName("countryId")
        var countryId: String? = "",
        var street: RealmList<String>? = null,
        var postcode: String? = "",
        var firstname: String = "",
        var lastname: String = "",
        var email: String = "",
        var telephone: String = "",
        @SerializedName("customAttributes")
        var subAddress: SubAddress? = null,
        @SerializedName("same_as_billing")
        var sameBilling: Int? = null,
        @SerializedName("save_in_address_book")
        var saveInAddress: Int = 0,
        var saveInAddressBook: String? = null,
        var company: String = "",
        @SerializedName("vat_id")
        var vatId: String = ""
) : RealmObject() {

    companion object {
        fun createAddress(firstName: String, lastName: String, email: String, contactNo: String,
                          homeNo: String, homeBuilding: String, homeSoi: String, homeDistrict: String,
                          homeSubDistrict: String, homeCity: String, homeRoad: String, homePostalCode: String,
                          homePhone: String, provinceId: String, provinceCode: String, countryId: String,
                          districtId: String, subDistrictId: String, postcodeId: String, sameBilling: Int? = null,
                          company: String = "", vatId: String = ""): AddressInformation {

            val subAddress = SubAddress(
                    mobile = homePhone,
                    houseNumber = homeNo,
                    building = homeBuilding,
                    soi = homeSoi,
                    t1cNo = null,
                    district = homeDistrict,
                    subDistrict = homeSubDistrict,
                    postcode = homePostalCode,
                    districtId = districtId,
                    subDistrictId = subDistrictId,
                    postcodeId = postcodeId,
                    addressLine = homeRoad)
            return AddressInformation(
                    city = homeCity,
                    region = homeCity,
                    regionId = provinceId,
                    regionCode = provinceCode,
                    countryId = countryId,
                    street = RealmList("n/a"),
                    postcode = homePostalCode,
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    telephone = contactNo,
                    subAddress = subAddress,
                    company = company,
                    vatId = vatId,
                    sameBilling = sameBilling)
        }

        fun createBranchAddress(branch: Branch): AddressInformation{
            val subAddress = SubAddress(
                    mobile = null,
                    houseNumber = null,
                    building = null,
                    soi = null,
                    t1cNo = null,
                    district = null,
                    subDistrict = null,
                    postcode = null,
                    districtId = null,
                    subDistrictId = null,
                    postcodeId = null,
                    addressLine = null)
            return AddressInformation(
                    city = branch.city,
                    region = "",
                    regionId = branch.regionId.toString(),
                    regionCode = branch.regionCode,
                    countryId = branch.countryId,
                    street = RealmList("n/a"),
                    postcode = branch.postcode,
                    firstname = branch.storeName,
                    lastname = branch.sellerCode,
                    email = branch.email,
                    telephone = branch.phone,
                    subAddress = subAddress,
                    company = "",
                    vatId = "",
                    sameBilling = null)
        }
    }

    fun getDisplayName(): String {
        return if (firstname.isNotEmpty() && lastname.isNotEmpty()) {
            "$firstname $lastname"
        } else {
            ""
        }
    }
}