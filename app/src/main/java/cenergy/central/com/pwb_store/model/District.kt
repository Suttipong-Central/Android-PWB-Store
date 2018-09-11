package me.a3cha.android.thaiaddress.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 7/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class District(
        @PrimaryKey
        @SerializedName("district_id")
        var districtId: Long = 0,
        @SerializedName("country_id")
        var countryId: String = "",
        @SerializedName("region_id")
        var provinceId: Long = 0,
        @SerializedName("name_en")
        var nameEn: String = "",
        @SerializedName("name_th")
        var nameTh: String = ""): RealmObject() {

    companion object {
        const val FIELD_ID = "districtId"
        const val FIELD_PROVINCE_ID = "provinceId"
    }
}