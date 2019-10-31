package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.response.ShoppingCartItem

/**
 * Created this because the response CartTotal have no product.sku
 * */
fun List<ShoppingCartItem>.checkItems(cartItems: List<CartItem>) : List<ShoppingCartItem> {
    // *match sku by item_id*
    this.forEach { item ->
        val cartItem = cartItems.find { it.id == item.id }
        if (cartItem != null) {
            item.sku = cartItem.sku ?: ""
        }
    }
    return this
}

fun List<ShoppingCartItem>.checkItemsBy(cartItems: List<CacheCartItem>) : List<ShoppingCartItem> {
    // *match sku by item_id*
    this.forEach { item ->
        val cartItem = cartItems.find { it.itemId == item.id }
        if (cartItem != null) {
            item.sku = cartItem.sku ?: ""
        }
    }
    return this
}