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
        fun createTestAddress(firstName: String, lastName: String, email: String, contactNo: String): AddressInformation {
            val subAddress = SubAddress(mobile = "",
                    houseNumber = "99",
                    building = "Apptitude Park",
                    soi = "-",
                    t1cNo = "",
                    district = "คลองสาน",
                    subDistrict = "คลองต้นไทร",
                    postcode = "10600",
                    districtId = "36", subDistrictId = "199", postcodeId = "213")
            return AddressInformation(countryId = "TH",
                    regionId = "668",
                    regionCode = "BKK",
                    region = "กรุงเทพมหานคร",
                    street = RealmList("ถนนหนึ่ง", "ถนนสอง"),
                    company = "",
                    telephone = contactNo,
                    postcode = "10600",
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