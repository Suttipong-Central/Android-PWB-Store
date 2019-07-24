package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.response.PaymentMethod

fun PaymentMethod.isPayWithCreditCard(): Boolean {
    val creditCardTypes = arrayListOf("fullpaymentredirect", "fullpayment", "redirectp2c2p",
            "installment", "redirect_installment", "installmentredirect", "p2c2p_ipp")
    return creditCardTypes.contains(this.code)
}