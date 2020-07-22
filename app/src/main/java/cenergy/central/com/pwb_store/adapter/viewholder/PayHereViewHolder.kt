package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView

class PayHereViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {

    val title: TextView = itemView.findViewById(R.id.tv_title)
    val description: TextView = itemView.findViewById(R.id.tv_description)
    val button: Button = itemView.findViewById(R.id.choose_payment_method)

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        title.text = itemView.context.getString(R.string.pay_here)
        description.text = itemView.context.getString(R.string.pay_here_description)
        itemView.setOnClickListener { listener.onClickedItem(item.paymentMethod) }
        button.setOnClickListener { listener.onClickedItem(item.paymentMethod) }
    }
}