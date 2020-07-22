package cenergy.central.com.pwb_store.model

data class PaymentMethod(var code: String = "", var title: String = ""){
    companion object {
        // payment methods code
        const val CASH_ON_DELIVERY = "cashondelivery"
        const val FULL_PAYMENT = "fullpaymentredirect"
        const val PAY_AT_STORE = "payatstore"
        const val INSTALLMENT = "p2c2p_ipp"
        const val E_ORDERING = "e_ordering"
        const val BANK_AND_COUNTER_SERVICE = "p2c2p_123"
    }
}