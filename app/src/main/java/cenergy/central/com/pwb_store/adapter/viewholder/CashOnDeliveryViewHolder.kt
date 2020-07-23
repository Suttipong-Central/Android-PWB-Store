package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView

class CashOnDeliveryViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {

    private val radioPayment: RadioButton = itemView.findViewById(R.id.radioPayment)
    private val description: TextView = itemView.findViewById(R.id.tv_description)
    private val context = itemView.context

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        radioPayment.text = context.getString(R.string.cash_on_delivery)
        description.text = context.getString(R.string.cod_description)
        itemView.setOnClickListener { listener.onClickedItem(item.paymentMethod) }
    }
}