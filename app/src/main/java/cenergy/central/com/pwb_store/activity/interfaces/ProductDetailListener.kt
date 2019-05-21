package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.Product

interface ProductDetailListener {
    fun getProduct(): Product?

    fun addProductToCompare(product: Product?)
    fun addProductToCart(product: Product?)

    fun onDisplayAvailableStore(product: Product?)
    fun onDisplayOverview(overview: String)
    fun onDisplaySpecification(spec: String)
}