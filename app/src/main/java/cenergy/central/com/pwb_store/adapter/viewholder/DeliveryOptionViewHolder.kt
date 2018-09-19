package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class DeliveryOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var carrierTitle: TextView = itemView.findViewById(R.id.tv_carrier_title)
    private var methodTitle: TextView = itemView.findViewById(R.id.tv_method_title)

    fun bindItem(deliveryOption: DeliveryOption, listener: DeliveryOptionsListener?) {
        carrierTitle.text = deliveryOption.carrierTitle
        methodTitle.text = deliveryOption.methodTitle
        if(deliveryOption.available){
            itemView.setOnClickListener {
                listener?.onSelectedOptionListener(deliveryOption)
            }
        } else {
            itemView.isEnabled = false
        }
    }
}
