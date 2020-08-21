package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.Installment
import cenergy.central.com.pwb_store.model.PaymentMethod
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

fun Product.getInstallments(): ArrayList<Installment> {
    return if (this.paymentMethod.contains(PaymentMethod.INSTALLMENT, true) && this.extension!!.installmentPlans.isNotEmpty()) {
        val installments: ArrayList<Installment> = arrayListOf()
        this.extension!!.installmentPlans.groupBy { it.bankId}.forEach {
            val installment = Installment(it.key, it.value.sortedBy { installment -> installment.period })
            installments.add(installment)
        }
        installments
    } else {
        arrayListOf()
    }
}