package cenergy.central.com.pwb_store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.NumberFormat
import java.util.*

/**
 * Created by Anuphap Suwannamas on 10/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class CompareProduct(@PrimaryKey var sku: String = "",
                          var name: String? = "",
                          var price: Double? = 0.0,
                          var specialPrice: Double? = 0.0,
                          var imageUrl: String? = "",
                          var brand: String? = "") : RealmObject(), IViewType {

    // for set view type in adapter
    var viewTypeID: Int = 0

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(id: Int) {
        this.viewTypeID = id
    }

    fun normalPrice(unit: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(price))
    }

    companion object {
        const val FIELD_SKU = "sku"
        @JvmStatic
        fun asCompareProduct(product: Product): CompareProduct {
            return CompareProduct(sku = product.sku, name = product.name,
                    price = product.price, specialPrice = product.specialPrice, imageUrl = product.imageUrl,
                    brand = product.brand)
        }
    }
}