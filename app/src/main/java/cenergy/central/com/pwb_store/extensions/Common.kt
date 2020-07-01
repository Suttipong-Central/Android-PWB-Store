package cenergy.central.com.pwb_store.extensions

import android.app.Activity
import android.content.Context
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.realm.RealmController

fun Context?.isProductInStock(product: Product) : Boolean {
    val database = RealmController.getInstance()
    val productInCart = database.getCacheCartItemBySKU(product.sku)
    val productQty = product.extension?.stokeItem?.qty ?: 0

    return if (productInCart == null) {
        productQty > 0
    } else {
        val productInCartQty = productInCart.qty ?: 0
        productQty > productInCartQty
    }
}

fun Product.isSalable(): Boolean{
    return extension?.stokeItem?.isSalable != null && extension!!.stokeItem!!.isSalable
}

fun Product.is1HourProduct(): Boolean{
    return if (extension?.stokeItem?.is2HProduct != null && extension!!.stokeItem!!.is2HProduct &&
            shippingMethods.isNotEmpty()){
        shippingMethods.contains(Product.PRODUCT_ONE_HOUR)
    } else {
        false
    }
}

fun Activity?.getCompareProducts(): Int{
    val database = RealmController.getInstance()
    return database.compareProducts.size
}