package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.OptionBody

interface ProductDetailListener {
    fun getProduct(): Product?

    fun addProductToCompare(product: Product?)
    fun addProductToCart(product: Product?)
    fun addProduct1HrsToCart(product: Product?)
    fun addProductConfigToCart(product: Product?, listOptionsBody: ArrayList<OptionBody>)

    fun onDisplayAvailableStore(product: Product?)
    fun onDisplayOverview(overview: String)
    fun onDisplaySpecification(spec: String)
}