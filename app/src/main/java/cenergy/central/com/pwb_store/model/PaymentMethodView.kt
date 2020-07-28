package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.model.response.PaymentCreditCardPromotion

sealed class PaymentMethodView {
    abstract val viewType: Int

    data class HeaderItemView(val title: String,
                              override val viewType: Int = PaymentMethodAdapter.TITLE_VIEW) : PaymentMethodView()

    data class PaymentItemView(val paymentMethod: PaymentMethod,
                               val promotions: List<PaymentCreditCardPromotion>? = null,
                               var selected: Boolean = false,
                               override val viewType: Int) : PaymentMethodView()

    data class PayButtonItemView(var enable: Boolean = false,
                                 override val viewType: Int = PaymentMethodAdapter.PAY_BUTTON_VIEW) : PaymentMethodView()

    data class EmptyItemView(override val viewType: Int = PaymentMethodAdapter.EMPTY_VIEW) : PaymentMethodView()
}