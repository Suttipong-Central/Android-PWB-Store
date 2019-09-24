package cenergy.central.com.pwb_store.extensions

import android.content.Context
import android.os.Parcel
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.response.PaymentMethod

fun List<CacheCartItem>.getPaymentType(context: Context): List<PaymentMethod> {
    val paymentType = arrayListOf<String>()
    forEach { product ->
        if (product.paymentMethod.isNotEmpty() && product.paymentMethod != "") {
            paymentType.addAll(product.paymentMethod.split(","))
        }
    }
    val paymentMethodFilter = arrayListOf<PaymentMethod>()
    val fullPayment = PaymentMethod(title = context.getString(R.string.fullpayment), code = "redirectp2c2p")
    val installment = PaymentMethod(title = context.getString(R.string.installment), code = "redirect_installment")
    paymentType.distinct().forEach {
        if (it == "fullpayment" || it == "redirectp2c2p") {
            if (!paymentMethodFilter.contains(fullPayment)) {
                paymentMethodFilter.add(fullPayment)
            }
        }
        if (it == "installment" || it == "redirect_installment") {
            if (!paymentMethodFilter.contains(installment)) {
                paymentMethodFilter.add(installment)
            }
        }
    }
    return paymentMethodFilter
}

fun List<PaymentMethod>.getMethodTitle(): List<String> {
    val methods = arrayListOf<String>()
    forEach { paymentMethod ->
        paymentMethod.title.let { methods.add(it) }
    }
    return methods
}

fun Parcel.writeLongList(input:List<Long>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeLong) // Save each element.
}

fun Parcel.createLongList() : List<Long> {
    val size = readLong()
    val output = ArrayList<Long>()
    for (i in 0 until size) {
        output.add(readLong())
    }
    return output
}