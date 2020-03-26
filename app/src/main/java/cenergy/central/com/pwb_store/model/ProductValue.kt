package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@RealmClass
open class ProductValue (
        @SerializedName("value_index")
        var index: Int = 0,
        @SerializedName("extension_attributes")
        var valueExtension: ProductValueExtension? = null): Parcelable, RealmModel

object ProductValueRealmListParceler: RealmListParceler<ProductValue> {
        override val clazz: Class<ProductValue>
                get() = ProductValue::class.java
}