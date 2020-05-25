package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodAdapter
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod

class PaymentEmptyViewHolder(itemView: View) : PaymentMethodAdapter.PaymentMethodViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tv_title)
    val button: Button = itemView.findViewById(R.id.choose_payment_method)

    override fun bindView(paymentMethod: PaymentMethod, listener: PaymentItemClickListener) {
        title.text = paymentMethod.title
        itemView.setOnClickListener { listener.onClickedItem(paymentMethod) }
        button.setOnClickListener { listener.onClickedItem(paymentMethod) }
    }
}