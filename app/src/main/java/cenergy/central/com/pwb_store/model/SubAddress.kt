package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class SubAddress(
        @SerializedName("tel_mobile")
        var mobile: String = "",
        @SerializedName("house_no")
        var houseNumber: String = "",
        var building: String = "",
        var soi: String = "",
        var t1cNo: String = "",
        @SerializedName("district_id")
        var districtId: String = "",
        var district: String = "",
        @SerializedName("subdistrict_id")
        var subDistrictId: String = "",
        var subDistrict: String = "",
        @SerializedName("postcode_id")
        var postcodeId: String = "",
        var postcode: String = "") : RealmObject()