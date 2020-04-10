package cenergy.central.com.pwb_store.dialogs.interfaces

import cenergy.central.com.pwb_store.model.PaymentMethod

interface PaymentItemClickListener {
    fun onClickedItem(paymentMethod: PaymentMethod)
}