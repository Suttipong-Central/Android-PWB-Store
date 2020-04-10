package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.PaymentMethod

fun PaymentMethod.isBankAndCounterServiceType(): Boolean {
    return this.code == PaymentMethod.BANK_AND_COUNTER_SERVICE
}