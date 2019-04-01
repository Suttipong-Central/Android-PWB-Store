package cenergy.central.com.pwb_store.dialogs.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypesClickListener
import cenergy.central.com.pwb_store.model.response.PaymentMethod

class PaymentTypesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val description: TextView = itemView.findViewById(R.id.txt_description)

    fun onByView(paymentMethod: PaymentMethod, paymentTypesClickListener: PaymentTypesClickListener?) {
        description.text = paymentMethod.title
        itemView.setOnClickListener {
            paymentTypesClickListener?.onPaymentTypesClickListener(paymentMethod)
        }
    }
}
