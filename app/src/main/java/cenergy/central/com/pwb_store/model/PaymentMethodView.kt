package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter.Companion.EMPTY_VIEW
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter.Companion.TITLE_VIEW
import cenergy.central.com.pwb_store.model.response.PaymentCreditCardPromotion

sealed class PaymentMethodView {
    abstract val viewType: Int

    data class HeaderItemView(val title: String,
                              override val viewType: Int = TITLE_VIEW) : PaymentMethodView()

    data class PaymentItemView(val paymentMethod: PaymentMethod,
                               val promotions: List<PaymentCreditCardPromotion>? = null,
                               var selected: Boolean = false,
                               override val viewType: Int) : PaymentMethodView()

    data class EmptyItemView(override val viewType: Int = EMPTY_VIEW) : PaymentMethodView()
}