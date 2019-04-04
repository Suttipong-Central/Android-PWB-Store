package cenergy.central.com.pwb_store.extensions

import android.content.Context
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.response.PaymentMethod

fun List<CacheCartItem>.getPaymentType(context: Context) : List<PaymentMethod> {
    val paymentType = arrayListOf<String>()
    forEach { product ->
        if (product.paymentMethod.isNotEmpty() && product.paymentMethod != "")  {
            paymentType.addAll(product.paymentMethod.split(","))
        }
    }
    val paymentMethodFilter = arrayListOf<PaymentMethod>()
    paymentType.distinct().forEach {
        if (it == "fullpayment" || it == "redirectp2c2p"){
            paymentMethodFilter.add(PaymentMethod(title = context.getString(R.string.fullpayment), code = "redirectp2c2p"))
        }
        if (it == "installment" || it == "redirect_installment"){
            paymentMethodFilter.add(PaymentMethod(title = context.getString(R.string.installment), code = "redirect_installment"))
        }
    }
    return paymentMethodFilter
}