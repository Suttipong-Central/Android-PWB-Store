package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class Brand(
        @PrimaryKey
        @SerializedName("id") var brandId: Long? = 0,
        var name: String = "") : RealmObject() {
    companion object {
        const val FIELD_BRAN_ID = "brandId"
    }
}