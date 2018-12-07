package cenergy.central.com.pwb_store.model
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class ShippingOrder(@SerializedName("address")
                         var shippingAddress: AddressInformation? = null, var method: String? = ""): RealmObject()