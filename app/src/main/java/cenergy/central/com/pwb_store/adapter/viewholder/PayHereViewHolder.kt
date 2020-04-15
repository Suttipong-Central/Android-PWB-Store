package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod

class PayHereViewHolder(itemView: View) : PaymentMethodAdapter.PaymentMethodViewHolder(itemView) {

    val title: TextView = itemView.findViewById(R.id.tv_title)
    val description: TextView = itemView.findViewById(R.id.tv_description)
    val button: Button = itemView.findViewById(R.id.choose_payment_method)

    override fun bindView(paymentMethod: PaymentMethod, listener: PaymentItemClickListener) {
        title.text = itemView.context.getString(R.string.pay_here)
        description.text = itemView.context.getString(R.string.pay_here_description)
        itemView.setOnClickListener { listener.onClickedItem(paymentMethod) }
        button.setOnClickListener { listener.onClickedItem(paymentMethod) }
    }
}