package cenergy.central.com.pwb_store.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class HDLMemberResponse (
        var customerFound: Int = 0,
        var customerInfos: ArrayList<HDLCustomerInfos>? = arrayListOf()
)

@Parcelize
data class HDLCustomerInfos(
        var customerId: Long = 0,
        var firstname: String = "",
        var lastname: String = "",
        var houseNo: String = "",
        var buildingName: String = "",
        var moo: String = "",
        var soi: String = "",
        var road: String = "",
        var mobile: String = "",
        var telephone: String = "",
        var postcode: String = "",
        var district: String = "",
        var subdistrict: String = "",
        var province: String = ""
): Parcelable{
    fun getDisplayName(): String{
        return "$firstname $lastname"
    }
}