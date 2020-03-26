package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Specification(var code: String = "",
                         var label: String = "",
                         var value: String? = "") : Parcelable, RealmObject()


object SpecificationRealmListParceler: RealmListParceler<Specification> {
    override val clazz: Class<Specification>
        get() = Specification::class.java
}