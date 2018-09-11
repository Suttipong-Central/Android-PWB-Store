package me.a3cha.android.thaiaddress.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 8/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class Postcode(
        @PrimaryKey
        @SerializedName("entity_id")
        var id: Long = 0,
        @SerializedName("subdistrict_id")
        var subDistrictId: Long = 0,
        @SerializedName("zip_code")
        var postcode: Long = 0):RealmObject() {
        companion object {
            const val FIELD_ID = "id"
            const val FIELD_SUB_DISTRICT_ID = "subDistrictId"
        }
}