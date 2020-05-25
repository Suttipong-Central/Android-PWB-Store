package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod

class CashOnDeliveryViewHolder(itemView: View) : PaymentMethodAdapter.PaymentMethodViewHolder(itemView) {

    private val title: TextView = itemView.findViewById(R.id.tv_title)
    private val description: TextView = itemView.findViewById(R.id.tv_description)
    private val button: Button = itemView.findViewById(R.id.choose_payment_method)
    private val context = itemView.context

    override fun bindView(paymentMethod: PaymentMethod, listener: PaymentItemClickListener) {
        title.text = context.getString(R.string.cash_on_delivery)
        description.text = context.getString(R.string.cod_description)
        itemView.setOnClickListener { listener.onClickedItem(paymentMethod) }
        button.setOnClickListener { listener.onClickedItem(paymentMethod) }
    }
}