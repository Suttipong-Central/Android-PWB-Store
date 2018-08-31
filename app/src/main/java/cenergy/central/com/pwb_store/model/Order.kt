package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.model.response.OrderResponse
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 31/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class Order(
        @PrimaryKey
        var orderId: String = "",
        var orderResponse: OrderResponse? = null,
        var userInformation: UserInformation? = null):RealmObject() {
        companion object {
                const val FIELD_ORDER_ID = "orderId"
        }
}