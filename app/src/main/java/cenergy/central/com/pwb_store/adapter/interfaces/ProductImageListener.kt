package cenergy.central.com.pwb_store.adapter.interfaces

import cenergy.central.com.pwb_store.model.ProductDetailImageItem

/**
 * Created by Anuphap Suwannamas on 2/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface ProductImageListener{
    fun onProductImageClickListener(index: Int, productImage: ProductDetailImageItem)
}