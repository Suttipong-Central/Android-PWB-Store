package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class DeliveryOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var layout: LinearLayout = itemView.findViewById(R.id.layout_delivery_option)
    private var carrierTitle: PowerBuyTextView = itemView.findViewById(R.id.carrier_title)
    private var methodTitle: PowerBuyTextView = itemView.findViewById(R.id.method_title)

    fun bindView(deliveryOption: DeliveryOption) {
        carrierTitle.text = deliveryOption.carrierTitle
        methodTitle.text = deliveryOption.methodTitle
        if(deliveryOption.available){
            layout.setOnClickListener {
                Toast.makeText(itemView.context, "TEST CLICK", Toast.LENGTH_SHORT).show()
            }
        } else {
            layout.isEnabled = false
        }
    }
}
