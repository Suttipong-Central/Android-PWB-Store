package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.DeliveryOptionViewHolder
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.model.DeliveryOption

class DeliveryOptionsAdapter(private var listener: DeliveryOptionsListener?) : RecyclerView.Adapter<DeliveryOptionViewHolder>() {

    var deliveryOptionList: List<DeliveryOption> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryOptionViewHolder {
        return DeliveryOptionViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_delivery_options, parent, false))
    }

    override fun getItemCount(): Int {
        return deliveryOptionList.size
    }

    override fun onBindViewHolder(holder: DeliveryOptionViewHolder, position: Int) {
        holder.bindItem(deliveryOptionList[position], listener)
    }
}
