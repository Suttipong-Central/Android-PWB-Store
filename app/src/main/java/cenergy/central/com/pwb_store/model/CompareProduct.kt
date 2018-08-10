package cenergy.central.com.pwb_store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 10/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class CompareProduct(@PrimaryKey var sku: String = "",
                          var name: String? = "",
                          var attributeID: Int? = null,
                          var price: Int? = 0,
                          var status: Int? = 0) : RealmObject() {
    companion object {
        const val FIELD_SKU = "sku"
        @JvmStatic
        fun asCompareProduct(product: Product): CompareProduct {
            return CompareProduct(sku = product.sku, name = product.name,
                    attributeID = product.attributeID, price = product.price, status = product.status)
        }
    }
}