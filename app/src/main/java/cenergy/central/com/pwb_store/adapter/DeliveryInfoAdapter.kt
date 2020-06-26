package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.DeliveryInfo
import kotlinx.android.synthetic.main.item_delivery_info.view.*

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
        holder.bind(items[position])
    }

    inner class DeliveryInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDeliveryTitle = itemView.tvDeliveryTitle
        private val ivDeliveryIcon = itemView.ivDeliveryIcon
        private val tvDescription = itemView.tvDeliveryDescription
        private val tvFee = itemView.tvDeliveryCost

        fun bind(deliveryInfo: DeliveryInfo) {
            val icon = when (deliveryInfo.shippingMethod) {
                "pwb_express" -> R.drawable.ic_delivery_express
                "pwb_standard", "pwb_hdl" -> R.drawable.ic_delivery_standard
                else -> R.drawable.ic_claim
            }
            ivDeliveryIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, icon))
            tvDeliveryTitle.text = deliveryInfo.shippingMethodLabel
            tvDescription.text = deliveryInfo.deliveryTimeMessage
            tvFee.text = deliveryInfo.shippingFee
        }
    }
}