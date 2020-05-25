package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.model.response.BranchResponse
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CacheCartItem(
        @PrimaryKey
        var itemId: Long? = 0,
        var sku: String? = "",
        var qty: Int? = 0, // qty of items in cart
        var name: String? = "",
        var price: Double? = 0.0,
        var type: String? = "",
        var cartId: String? = "",
        var imageUrl: String = "",
        var maxQTY: Int? = 0,
        var qtyInStock: Int? = null,
        var paymentMethod: String = "",
        var branch: Branch? = null) : RealmObject(), Parcelable {

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
                    paymentMethod = product.paymentMethod) // cache payment_method
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
                    imageUrl = compareProduct.imageUrl)
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
                    qtyInStock = branchResponse.sourceItem?.quantity?: 0,
                    imageUrl = product.getImageUrl(),
                    branch = branchResponse.branch)
        }
    }
}