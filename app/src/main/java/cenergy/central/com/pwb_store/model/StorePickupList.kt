package cenergy.central.com.pwb_store.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class StorePickupList(
        @PrimaryKey
        var sku: String = "",
        var stores: RealmList<Branch> = RealmList()
): RealmObject() {
    companion object {
        const val FIELD_SKU: String = "sku"
    }
}