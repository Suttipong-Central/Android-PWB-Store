package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.AddressAdapter
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Province(
        @PrimaryKey
        @SerializedName("region_id")
        var provinceId: String = "",
        @SerializedName("country_id")
        var countryId: String = "",
        var code: String = "",
        @SerializedName("default_name")
        var defaultName: String = "",
        var name: String = "") : RealmObject(), AddressAdapter.AddressItem {

    companion object {
        const val FIELD_ID = "provinceId"
        const val FIELD_NAME = "name"
        const val FIELD_DEFAULT_NAME = "defaultName"
    }
}