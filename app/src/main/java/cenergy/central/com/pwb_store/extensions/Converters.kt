package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.CacheCartItem

fun List<CacheCartItem>.getPaymentType() : List<String> {
    val paymentType = arrayListOf<String>()
    forEach { product ->
        if (product.paymentMethod.isNotEmpty() && product.paymentMethod != "")  {
            paymentType.addAll(product.paymentMethod.split(","))
        }
    }
    return paymentType.distinct()
}