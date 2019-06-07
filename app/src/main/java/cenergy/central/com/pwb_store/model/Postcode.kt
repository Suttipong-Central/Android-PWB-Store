package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AddressAdapter
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Postcode(
        @PrimaryKey
        @SerializedName("entity_id")
        var postcodeId: String = "",
        @SerializedName("subdistrict_id")
        var subDistrictId: String = "",
        @SerializedName("zip_code")
        var postcode: String = "") : RealmObject(), AddressAdapter.AddressItem {

    companion object {
        const val FIELD_ID = "postcodeId"
        const val FIELD_SUB_DISTRICT_ID = "subDistrictId"
        const val FIELD_POST_CODE = "postcode"
    }
}