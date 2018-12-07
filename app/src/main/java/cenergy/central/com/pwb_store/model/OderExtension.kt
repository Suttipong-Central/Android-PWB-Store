package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class OderExtension(@SerializedName("shipping_assignments")
                         var shippingAssignments: RealmList<ShippingAssignment>? = RealmList()): RealmObject()