package cenergy.central.com.pwb_store.dialogs.interfaces

import cenergy.central.com.pwb_store.model.PaymentMethod

interface PaymentItemClickListener {
    fun onClickedPayButton()
    fun onClickedPaymentItem(paymentMethod: PaymentMethod)
    fun onSelectedPromotion(paymentMethod: String, promotionId: Int)
    fun onSelectedDefaultPromotion(paymentMethod: String)
}