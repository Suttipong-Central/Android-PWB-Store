package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AddressAdapter
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class District(
        @PrimaryKey
        @SerializedName("district_id")
        var districtId: String = "",
        @SerializedName("country_id")
        var countryId: String = "",
        @SerializedName("region_id")
        var provinceId: String = "",
        @SerializedName("region_code")
        var provinceCode: String = "",
        var code: String = "",
        @SerializedName("default_name")
        var defaultName: String = "",
        var name: String = ""): RealmObject(), AddressAdapter.AddressItem {

    companion object {
        const val FIELD_ID = "districtId"
        const val FIELD_PROVINCE_ID = "provinceId"
        const val FIELD_NAME = "name"
        const val FIELD_DEFAULT_NAME = "defaultName"
    }
}