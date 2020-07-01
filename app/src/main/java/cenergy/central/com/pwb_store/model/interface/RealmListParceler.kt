package cenergy.central.com.pwb_store.model.`interface`

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CacheFreeItem
import cenergy.central.com.pwb_store.utils.readRealmList
import cenergy.central.com.pwb_store.utils.writeRealmList
import io.realm.RealmList
import io.realm.RealmModel
import kotlinx.android.parcel.Parceler

interface RealmListParceler<T>: Parceler<RealmList<T>?> where T: RealmModel, T: Parcelable {
    override fun create(parcel: Parcel): RealmList<T>? = parcel.readRealmList(clazz)

    override fun RealmList<T>?.write(parcel: Parcel, flags: Int) {
        parcel.writeRealmList(this, clazz)
    }

    val clazz : Class<T>
}

object CacheFreeItemRealmListParceler: RealmListParceler<CacheFreeItem> {
    override val clazz: Class<CacheFreeItem>
        get() = CacheFreeItem::class.java
}