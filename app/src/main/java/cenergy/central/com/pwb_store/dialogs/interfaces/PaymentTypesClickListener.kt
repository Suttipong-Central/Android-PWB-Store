package cenergy.central.com.pwb_store.dialogs.interfaces

import cenergy.central.com.pwb_store.model.response.PaymentMethod

interface PaymentTypesClickListener{
    fun onPaymentTypesClickListener(paymentMethods: PaymentMethod)
}