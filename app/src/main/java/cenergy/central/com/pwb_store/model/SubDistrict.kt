package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 7/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class SubDistrict(
        @PrimaryKey
        @SerializedName("subdistrict_id")
        var subDistrictId: Long = 0,
        @SerializedName("country_id")
        var countryId: String = "",
        @SerializedName("district_id")
        var districtId: Long = 0,
        @SerializedName("name_en")
        var nameEn: String = "",
        @SerializedName("name_th")
        var nameTh: String = ""): RealmObject() {

    fun getSubDistrictName(lang: String): String {
        return when (lang) {
            AppLanguage.EN.key -> nameEn
            else -> nameTh
        }
    }

    companion object {
        const val FIELD_ID = "subDistrictId"
        const val FIELD_DISTRICT_ID = "districtId"
        const val FIELD_NAME_TH = "nameTh"
        const val FIELD_NAME_EN = "nameEn"
    }
}