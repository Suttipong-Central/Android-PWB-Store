package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.Product

/**
 * Created by Anuphap Suwannamas on 21/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface ProductDetailListener {
    fun getProduct(): Product?

    fun addProductToCompare(product: Product?)
    fun addProductToCart(product: Product?)
    fun onDisplayAvailableStore(product: Product?)
}