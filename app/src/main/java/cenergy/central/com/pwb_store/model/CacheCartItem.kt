package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.Constants.Companion.DEFAULT_SOLD_BY
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.model.response.OfflinePriceItem
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

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
        var maxQTY: Int? = 0,
        var qtyInStock: Int? = null,
        var paymentMethod: String = "",
        var branch: Branch? = null,
        var soldBy: String? = null,
        var isOfflinePrice: Boolean = false,
        var freeItems: RealmList<CacheFreeItem>? = RealmList()) : RealmObject()
{
    fun updateItem(cartItem: CartItem) {
        this.itemId = cartItem.id
        this.sku = cartItem.sku
        this.qty = cartItem.qty
        this.name = cartItem.name
        this.price = cartItem.price
        this.type = cartItem.type
        this.cartId = cartItem.cartId
    }

    companion object {
        const val FIELD_ID = "itemId"
        const val FIELD_SKU = "sku"

        @JvmStatic
        fun asCartItem(cartItem: CartItem, product: Product): CacheCartItem {
            return CacheCartItem(
                    itemId = cartItem.id,
                    sku = cartItem.sku,
                    qty = cartItem.qty,
                    name = product.name,
                    price = cartItem.price,
                    type = cartItem.type,
                    cartId = cartItem.cartId,
                    maxQTY = product.extension?.stokeItem?.maxQTY ?: 1,
                    qtyInStock = product.extension?.stokeItem?.qty,
                    imageUrl = product.getImageUrl(),
                    paymentMethod = product.paymentMethod,
                    isOfflinePrice = product.isOfflinePrice,
                    soldBy = product.soldBy ?: DEFAULT_SOLD_BY) // cache payment_method
        }

        @JvmStatic
        fun asCartItem(cartItem: CartItem, compareProduct: CompareProduct): CacheCartItem {
            return CacheCartItem(
                    itemId = cartItem.id,
                    sku = cartItem.sku,
                    qty = cartItem.qty,
                    name = cartItem.name,
                    price = cartItem.price,
                    type = cartItem.type,
                    cartId = cartItem.cartId,
                    maxQTY = compareProduct.maxQty ?: 1,
                    qtyInStock = compareProduct.qtyInStock,
                    imageUrl = compareProduct.imageUrl,
                    soldBy = compareProduct.soldBy ?: DEFAULT_SOLD_BY,
                    isOfflinePrice = compareProduct.isOfflinePrice)
        }

        @JvmStatic
        fun asCartItem(cartItem: CartItem, product: Product, branchResponse: BranchResponse): CacheCartItem {
            return CacheCartItem(
                    itemId = cartItem.id,
                    sku = cartItem.sku,
                    qty = cartItem.qty,
                    name = cartItem.name,
                    price = cartItem.price,
                    type = cartItem.type,
                    cartId = cartItem.cartId,
                    maxQTY = product.extension?.stokeItem?.maxQTY ?: 1,
                    qtyInStock = branchResponse.sourceItem?.quantity ?: 0,
                    imageUrl = product.getImageUrl(),
                    branch = branchResponse.branch,
                    soldBy = product.soldBy ?: DEFAULT_SOLD_BY,
                    isOfflinePrice = product.isOfflinePrice)
        }
    }
}

open class CacheFreeItem(
        var sku: String = "",
        var imageUrl: String = "",
        var forItemId: Long = 0,
        var qty: Int = 0
) : RealmObject()