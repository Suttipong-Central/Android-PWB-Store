package cenergy.central.com.pwb_store.model

import android.content.Context
import cenergy.central.com.pwb_store.extensions.toOrderDateTime
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
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
        var shippingAmount: Double = 0.0,
        var paymentRedirect: String = "",
        var discountPrice: Double = 0.0,
        var total: Double = 0.0) : RealmObject() {

    fun getDisplayTimeCreated(context: Context): String {
        val language = PreferenceManager(context).getDefaultLanguage()
        return createdAt.toOrderDateTime(language)
    }

    companion object {
        const val FIELD_ORDER_ID = "orderId"

        fun asOrder(orderResponse: OrderResponse, branchShipping: Branch?,
                    paymentRedirect: String = ""): Order {
            return Order(orderId = orderResponse.orderId!!,
                    createdAt = orderResponse.createdAt,
                    memberName = orderResponse.billingAddress!!.getDisplayName(),
                    shippingType = orderResponse.shippingType!!,
                    items = asItems(orderResponse.items),
                    shippingAddress = orderResponse.orderExtension!!.shippingAssignments!![0]!!.shipping!!.shippingAddress!!,
                    billingAddress = orderResponse.billingAddress!!,
                    branchShipping = branchShipping,
                    shippingDescription = orderResponse.shippingDescription,
                    baseTotal = orderResponse.baseTotal,
                    shippingAmount = orderResponse.shippingAmount,
                    paymentRedirect = paymentRedirect,
                    discountPrice = orderResponse.discount,
                    total =  orderResponse.total)
        }

        private fun asItems(items: RealmList<Item>?): RealmList<Item> {
            return items ?: RealmList()
        }
    }
}