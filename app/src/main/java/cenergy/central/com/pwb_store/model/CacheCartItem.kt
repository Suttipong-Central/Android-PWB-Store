package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

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
        var qtyInStock: Int? = 0, // qty of product
        var paymentMethod: String = "") : RealmObject(), Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString())

    fun updateItem(cartItem: CartItem) {
        this.itemId = cartItem.id
        this.sku = cartItem.sku
        this.qty = cartItem.qty
        this.name = cartItem.name
        this.price = cartItem.price
        this.type = cartItem.type
        this.cartId = cartItem.cartId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(itemId)
        parcel.writeString(sku)
        parcel.writeValue(qty)
        parcel.writeString(name)
        parcel.writeValue(price)
        parcel.writeString(type)
        parcel.writeString(cartId)
        parcel.writeString(imageUrl)
        parcel.writeValue(maxQTY)
        parcel.writeValue(qtyInStock)
        parcel.writeValue(paymentMethod)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CacheCartItem> {
        const val FIELD_ID = "itemId"
        const val FIELD_SKU = "sku"

        @JvmStatic
        fun asCartItem(cartItem: CartItem, product: Product): CacheCartItem {
            return CacheCartItem(
                    itemId = cartItem.id,
                    sku = cartItem.sku,
                    qty = cartItem.qty,
                    name = cartItem.name,
                    price = cartItem.price,
                    type = cartItem.type,
                    cartId = cartItem.cartId,
                    maxQTY = product.extension?.stokeItem?.maxQTY ?: 1,
                    qtyInStock = product.extension?.stokeItem?.qty ?: 0,
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
                    qtyInStock = compareProduct.qtyInStock ?: 1,
                    imageUrl = compareProduct.imageUrl)
        }

        override fun createFromParcel(parcel: Parcel): CacheCartItem {
            return CacheCartItem(parcel)
        }

        override fun newArray(size: Int): Array<CacheCartItem?> {
            return arrayOfNulls(size)
        }
    }
}