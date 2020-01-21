package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CacheFreeItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.response.ShoppingCartItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created this because the response CartTotal have no product.sku
 * */
fun List<CacheCartItem>.mergeItems(cartItems: List<CartItem>, shoppingCartItems: List<ShoppingCartItem>): List<CacheCartItem> {
    // add amastyPromo to cartItems
    cartItems.forEach { item ->
        val cartItem = shoppingCartItems.find { item.id == it.id }
        if (cartItem?.extension?.amastyPromo != null) {
            item.extension = cartItem.extension
        }
    }

    // add free item to arrayList
    val freeItems = arrayListOf<CacheFreeItem>()
    cartItems.filter { !it.extension?.freebies.isNullOrEmpty() }.forEach {
        it.extension!!.freebies.forEach { freeItem ->
            freeItems.add(CacheFreeItem(freeItem.sku, "", freeItem.forItemId, freeItem.qty))
        }
    }

    // add feebie image to cacheItem
    freeItems.forEach { freebie ->
        freebie.imageUrl = cartItems.find { it.sku == freebie.sku }?.extension?.amastyPromo?.src ?: ""
        find { cacheCartItem -> cacheCartItem.itemId == freebie.forItemId && cacheCartItem.freeItems?.firstOrNull { it.sku == freebie.sku } == null }?.freeItems?.add(freebie)
    }

    // add price include tax
    shoppingCartItems.forEach { shoppingCartItem ->
        find { it.itemId == shoppingCartItem.id }?.price = shoppingCartItem.price
    }
    return this
}

fun List<CacheCartItem>.getCartItem(): ArrayList<Any> {
    val cartItems: ArrayList<Any> = arrayListOf()
    val header: ArrayList<String> = arrayListOf()
    distinctBy { it.soldBy }.forEach {
        if (it.soldBy != null){
            header.add(it.soldBy!!)
        }
    }
    val centralIndex = header.indexOf(Product.DEFAULT_SOLD_BY)
    if (centralIndex > 0){
        Collections.swap(header, centralIndex, 0) // swap "central" to fist index
    }
    header.forEach { headerItem ->
        cartItems.add(headerItem)
        val items = filter { it.soldBy == headerItem }
        items.forEach { item ->
            cartItems.add(item)
        }
    }
    return cartItems
}

fun List<CacheCartItem>.hasProduct2h(): Boolean {
    val item = this.find { it.branch != null }
    return item != null
}