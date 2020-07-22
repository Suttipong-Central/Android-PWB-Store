package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView

class FullPaymentViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
    private val button: Button = itemView.findViewById(R.id.choose_payment_method)

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        titleTextView.text = itemView.context.getString(R.string.fullpayment)
        itemView.setOnClickListener { listener.onClickedItem(item.paymentMethod) }
        button.setOnClickListener { listener.onClickedItem(item.paymentMethod) }
    }
}