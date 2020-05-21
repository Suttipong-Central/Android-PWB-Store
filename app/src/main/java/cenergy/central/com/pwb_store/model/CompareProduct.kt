package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.extensions.isSpecialPrice
import cenergy.central.com.pwb_store.extensions.toPriceDisplay
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CompareProduct(@PrimaryKey var sku: String = "",
                          var name: String? = "",
                          var price: Double? = 0.0,
                          var specialPrice: Double? = 0.0,
                          var imageUrl: String = "",
                          var inStock: Boolean = false,
                          var maxQty: Int? = 0,
                          var qtyInStock: Int? = 0,
                          var rating: Int = 0,
                          var brand: String? = "",
                          var minQty: Int = 0,
                          var isSalable: Boolean = false) : RealmObject(), IViewType {

    // for set view type in adapter
    var viewTypeID: Int = 0

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(id: Int) {
        this.viewTypeID = id
    }

    fun getProductPrice(): String {
        return if (specialPrice == null) {
            price?.toPriceDisplay() ?: ""
        } else {
            specialPrice?.toPriceDisplay() ?: ""
        }
    }

    companion object {
        const val FIELD_SKU = "sku"
        @JvmStatic
        fun asCompareProduct(product: Product): CompareProduct {
            // TODO: Improve set price
           val specialPrice = if (product.isSpecialPrice()) product.specialPrice else null

            return CompareProduct(sku = product.sku, name = product.name,
                    price = product.price,
                    specialPrice = specialPrice,
                    rating = product.rating ?: 0,
                    imageUrl = product.getImageUrl(),
                    inStock = product.extension?.stokeItem?.isInStock ?: false,
                    brand = product.brand,
                    maxQty = product.extension?.stokeItem?.maxQTY ?: 1,
                    qtyInStock = product.extension?.stokeItem?.qty ?: 0,
                    minQty = product.extension?.stokeItem?.minQTY ?: 0,
                    isSalable = product.extension?.stokeItem?.isSalable ?: false)
        }
    }
}