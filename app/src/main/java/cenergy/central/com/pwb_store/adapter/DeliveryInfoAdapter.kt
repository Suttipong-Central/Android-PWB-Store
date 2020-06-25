package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.DeliveryInfo

class DeliveryInfoAdapter : RecyclerView.Adapter<DeliveryInfoAdapter.DeliveryInfoViewHolder>() {
    var items = arrayListOf<DeliveryInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delivery_info, parent, false)
        return DeliveryInfoViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DeliveryInfoViewHolder, position: Int) {

    }

    inner class DeliveryInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}