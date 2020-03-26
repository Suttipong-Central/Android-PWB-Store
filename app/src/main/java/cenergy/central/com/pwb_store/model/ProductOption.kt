package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith

@Parcelize
@RealmClass
open class ProductOption(
        var id: Int = 0,
        @SerializedName("product_id")
        var productId: Long = 0,
        @SerializedName("attribute_id")
        var attrId: String = "",
        var label: String = "",
        var position: Int = 0,
        var values: @WriteWith<ProductValueRealmListParceler> RealmList<ProductValue> = RealmList()
) : Parcelable, RealmModel

object ProductOptionRealmListParceler: RealmListParceler<ProductOption> {
        override val clazz: Class<ProductOption>
                get() = ProductOption::class.java
}