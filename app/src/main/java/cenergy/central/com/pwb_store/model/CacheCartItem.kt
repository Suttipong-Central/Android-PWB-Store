package cenergy.central.com.pwb_store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 24/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */
open class CacheCartItem(
        @PrimaryKey
        var itemId: Long? = 0,
        var sku: String? = "",
        var qty: Int? = 0,
        var name: String? = "",
        var price: Double? = 0.0,
        var type: String? = "",
        var cartId: String? = "",
        var imageUrl: String = "",
        var maxQTY: Int? = 0) : RealmObject() {

    companion object {
        const val FIELD_ID = "itemId"

        @JvmStatic
        fun asCartItem(cartItem: CartItem, product: Product): CacheCartItem {
            return CacheCartItem(itemId = cartItem.id, sku = cartItem.sku, qty = cartItem.qty,
                    name = cartItem.name, price = cartItem.price, type = cartItem.type, cartId = cartItem.cartId,
                    maxQTY = product.extension?.stokeItem?.maxQTY ?: 1, imageUrl= product.getImageUrl())
        }
    }

    fun updateItem(cartItem: CartItem) {
        this.itemId = cartItem.id
        this.sku = cartItem.sku
        this.qty = cartItem.qty
        this.name = cartItem.name
        this.price = cartItem.price
        this.type = cartItem.type
        this.cartId = cartItem.cartId
        this.imageUrl = imageUrl
    }
}