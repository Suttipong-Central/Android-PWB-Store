package cenergy.central.com.pwb_store.extensions

import android.content.Context
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.model.Category
import cenergy.central.com.pwb_store.model.CompareProduct
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

fun Product.isTwoHourProduct(): Boolean{
    return if (extension?.stokeItem?.is2HProduct != null && extension!!.stokeItem!!.is2HProduct &&
            shippingMethods.isNotEmpty()){
        val shippingList = arrayListOf<String>()
        shippingList.addAll(this.shippingMethods.split(","))
        shippingList.indexOf(Product.PRODUCT_TWO_HOUR) != -1
    } else {
        false
    }
}

fun Category.isSpecial(): Boolean {
    val specialIDs = Constants.SPECIAL_CATEGORIES
    return specialIDs.contains(this.id)
}

fun Context?.isProductInStock(compareProduct: CompareProduct) : Boolean {
    val database = RealmController.getInstance()
    val productInCart = database.getCacheCartItemBySKU(compareProduct.sku)
    val productQty = compareProduct.qtyInStock ?: 0

    return if (productInCart == null) {
        productQty > 0
    } else {
        val productInCartQty = productInCart.qty ?: 0
        productQty > productInCartQty
    }
}
