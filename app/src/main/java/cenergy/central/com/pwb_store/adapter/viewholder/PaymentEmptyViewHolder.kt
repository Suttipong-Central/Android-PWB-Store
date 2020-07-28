package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.RadioButton
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView

class PaymentEmptyViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {
    private val radioPayment: RadioButton = itemView.findViewById(R.id.radioPayment)

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        radioPayment.text = item.paymentMethod.title
        radioPayment.isChecked = item.selected
        itemView.setOnClickListener { listener.onClickedPaymentItem(item.paymentMethod) }
    }
}