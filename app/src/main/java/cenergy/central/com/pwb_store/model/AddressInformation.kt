package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class AddressInformation(
        var countryId: String = "",
        var regionId: String = "",
        var regionCode: String = "",
        var region: String = "",
        var street: RealmList<String>? = null,
        var company: String = "",
        var telephone: String = "",
        var postcode: String = "",
        var firstname: String = "",
        var lastname: String = "",
        var email: String = "",
        var vatId: String = "",
        @SerializedName("customAttributes")
        var subAddress: SubAddress? = null) : RealmObject() {

    companion object {
        fun createTestAddress(firstName: String, lastName: String, email: String, contactNo: String,
                              homeNo: String, homeBuilding: String, homeSoi: String, homeRoad: String,
                              homeCity: String, homeDistrict: String, homeSubDistrict: String,
                              homePostalCode: String, homePhone: String): AddressInformation {
            val subAddress = SubAddress(mobile = homePhone,
                    houseNumber = homeNo,
                    building = homeBuilding,
                    soi = homeSoi,
                    t1cNo = "",
                    district = homeDistrict,
                    subDistrict = homeSubDistrict,
                    postcode = homePostalCode,
                    districtId = "36", subDistrictId = "199", postcodeId = "213")
            return AddressInformation(countryId = "TH",
                    regionId = "668",
                    regionCode = "BKK",
                    region = homeCity,
                    street = RealmList(homeRoad),
                    company = "",
                    telephone = contactNo,
                    postcode = homePostalCode,
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    vatId = "",
                    subAddress = subAddress)
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