package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.extensions.formatterUTC
import cenergy.central.com.pwb_store.extensions.toDate
import cenergy.central.com.pwb_store.manager.bus.event.ProductDetailDescriptionBus
import cenergy.central.com.pwb_store.model.response.OrderResponse
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 31/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class Order(
        @PrimaryKey
        var orderId: String = "",
        var createdAt: String = "",
        var memberName: String = "",
        var shippingType: String = "",
        var items: RealmList<Item> = RealmList(),
        var shippingAddress: AddressInformation? = null,
        var billingAddress: AddressInformation? = null,
        var branchShipping: Branch? = null,
        var shippingDescription: String = "",
        var baseTotal: Double = 0.0,
        var shippingAmount: Double = 0.0
) : RealmObject() {
    companion object {
        const val FIELD_ORDER_ID = "orderId"

        fun asOrder(orderResponse: OrderResponse, branchShipping: Branch?) : Order {
            return Order(orderId = orderResponse.orderId!!,
                    createdAt = orderResponse.createdAt.toDate().formatterUTC(),
                    memberName = orderResponse.billingAddress!!.getDisplayName(),
                    shippingType = orderResponse.shippingType!!,
                    items = Order.asItems(orderResponse.items),
                    shippingAddress = orderResponse.orderExtension!!.shippingAssignments!![0]!!.shipping!!.shippingAddress!!,
                    billingAddress = orderResponse.billingAddress!!,
                    branchShipping = branchShipping,
                    shippingDescription = orderResponse.shippingDescription,
                    baseTotal = orderResponse.baseTotal,
                    shippingAmount = orderResponse.shippingAmount)
        }

        private fun asItems(items: RealmList<Item>?): RealmList<Item>{
            return items ?: RealmList()
        }
    }
}