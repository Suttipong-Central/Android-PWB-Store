package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class AddressInformation(
        var region: String = "",
        @SerializedName("region_id")
        var regionId: String = "",
        @SerializedName("region_code")
        var regionCode: String = "",
        @SerializedName("country_id")
        var countryId: String = "",
        var street: RealmList<String>? = null,
        var postcode: String = "",
        var firstname: String = "",
        var lastname: String = "",
        var email: String = "",
        var telephone: String = "",
        @SerializedName("customAttributes")
        var subAddress: SubAddress? = null,
        @SerializedName("same_as_billing")
        var sameBuilling: Int = 0
) : RealmObject() {

    companion object {
        fun createTestAddress(firstName: String, lastName: String, email: String, contactNo: String,
                              homeNo: String, homeBuilding: String, homeSoi: String, homeDistrict: String,
                              homeSubDistrict: String, homeCity: String, homeRoad: String,
                              homePostalCode: String, homePhone: String): AddressInformation {

            val subAddress = SubAddress(
                    mobile = homePhone,
                    houseNumber = homeNo,
                    building = homeBuilding,
                    soi = homeSoi,
                    t1cNo = "",
                    district = homeDistrict,
                    subDistrict = homeSubDistrict,
                    postcode = homePostalCode,
                    districtId = "50",
                    subDistrictId = "256",
                    postcodeId = "213")
            return AddressInformation(
                    region = homeCity,
                    regionId = "668",
                    regionCode = "BKK",
                    countryId = "TH",
                    street = RealmList(homeRoad),
                    postcode = homePostalCode,
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    telephone = contactNo,
                    subAddress = subAddress)
        }

        fun createAddress(firstName: String, lastName: String, email: String, contactNo: String,
                              homeNo: String, homeBuilding: String, homeSoi: String, homeDistrict: String,
                              homeSubDistrict: String, homeCity: String, homeRoad: String,
                              homePostalCode: String, homePhone: String): AddressInformation {

            val subAddress = SubAddress(
                    mobile = homePhone,
                    houseNumber = homeNo,
                    building = homeBuilding,
                    soi = homeSoi,
                    t1cNo = "",
                    district = homeDistrict,
                    subDistrict = homeSubDistrict,
                    postcode = homePostalCode,
                    districtId = "50",
                    subDistrictId = "256",
                    postcodeId = "213")
            return AddressInformation(
                    region = homeCity,
                    regionId = "668",
                    regionCode = "BKK",
                    countryId = "TH",
                    street = RealmList(homeRoad),
                    postcode = homePostalCode,
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    telephone = contactNo,
                    subAddress = subAddress,
                    sameBuilling = 1)
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