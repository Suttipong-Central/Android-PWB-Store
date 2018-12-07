package cenergy.central.com.pwb_store.model

import io.realm.RealmList
import io.realm.RealmObject

open class ShippingAssignment(var shipping: ShippingOrder? = null, var items: RealmList<Item>? = RealmList()): RealmObject()
