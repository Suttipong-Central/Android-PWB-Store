package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.Branch
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */
data class UpdateItemBody(var cartItem: ItemBody? = null) {
    companion object {
        fun create(cartId: String, itemId: Long, qty: Int, retailerId: Int? = null): UpdateItemBody {
            val body = if (retailerId != null){
                val extensionAttr = CartExtensionAttr.create(retailerId)
                ItemBody(cartId = cartId, itemId = itemId, qty = qty, extensionAttr = extensionAttr)
            } else {
                ItemBody(cartId, itemId, qty)
            }
            return UpdateItemBody(body)
        }

        fun create(cartId: String, itemId: Long, qty: Int, branch: Branch, retailerId: Int? = null): UpdateItemBody {
            val shippingAssignment = ShippingAssignment(shippingMethod = "storepickup_ispu")
            val pickupStore = PickupStore(branch.storeId)
            val extensionAttr = CartExtensionAttr(shippingAssignment, pickupStore)
            val body = ItemBody(cartId = cartId, itemId = itemId, qty = qty, extensionAttr = extensionAttr)
            return UpdateItemBody(body)
        }
    }
}

data class ItemBody(
        @SerializedName("quote_id")
        var cartId: String,
        @SerializedName("item_id")
        var itemId: Long,
        var qty: Int = 1,
        @SerializedName("extension_attributes")
        val extensionAttr: CartExtensionAttr? = null
)