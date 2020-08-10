package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.Product

fun Product.isSalable(): Boolean {
    return extension?.stokeItem?.isSalable != null && extension!!.stokeItem!!.isSalable
}

fun Product.is1HourProduct(): Boolean {
    return if (extension?.stokeItem?.is2HProduct != null && extension!!.stokeItem!!.is2HProduct &&
            shippingMethods.isNotEmpty()) {
        shippingMethods.contains(Product.PRODUCT_TWO_HOUR)
    } else {
        false
    }
}