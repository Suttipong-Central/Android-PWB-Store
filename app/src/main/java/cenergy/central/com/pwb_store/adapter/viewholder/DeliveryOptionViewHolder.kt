package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType

class DeliveryOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var carrierTitle: TextView = itemView.findViewById(R.id.tv_carrier_title)
    private var methodTitle: TextView = itemView.findViewById(R.id.tv_method_title)
    private var amount: TextView = itemView.findViewById(R.id.tv_amount)

    @SuppressLint("SetTextI18n")
    fun bindItem(deliveryOption: DeliveryOption, listener: DeliveryOptionsListener?) {
        val unit = itemView.context.getString(R.string.baht)
        carrierTitle.text = DeliveryType.fromString(deliveryOption.methodCode).toString()
        methodTitle.text = deliveryOption.methodTitle
        amount.text = "$unit${deliveryOption.amount}"
        amount.visibility = if (deliveryOption.amount > 0) View.VISIBLE else View.GONE
        if (deliveryOption.available) {
            itemView.setOnClickListener {
                listener?.onSelectedOptionListener(deliveryOption)
            }
        } else {
            itemView.isEnabled = false
        }
    }
}
