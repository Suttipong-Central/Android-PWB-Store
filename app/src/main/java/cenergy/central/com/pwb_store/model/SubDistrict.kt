package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AddressAdapter
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SubDistrict(
        @PrimaryKey
        @SerializedName("subdistrict_id")
        var subDistrictId: String = "",
        @SerializedName("country_id")
        var countryId: String = "",
        @SerializedName("district_id")
        var districtId: String = "",
        @SerializedName("district_code")
        var districtCode: String = "",
        @SerializedName("default_name")
        var defaultName: String = "",
        var name: String = "",
        @SerializedName("entity_id")
        var postcodeId: String = "",
        @SerializedName("zip_code")
        var postcode: String = "") : RealmObject(), AddressAdapter.AddressItem {

    companion object {
        const val FIELD_ID = "subDistrictId"
        const val FIELD_DISTRICT_ID = "districtId"
        const val FIELD_NAME = "name"
        const val FIELD_DEFAULT_NAME = "defaultName"
    }
}