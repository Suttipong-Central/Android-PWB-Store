package cenergy.central.com.pwb_store.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.response.PaymentAgent
import kotlinx.android.synthetic.main.item_payment_transfer.view.*

class PaymentTransferAdapter : RecyclerView.Adapter<PaymentTransferAdapter.PaymentTransferViewHolder>() {
    var items = listOf<PaymentAgent>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentTransferViewHolder {
        return PaymentTransferViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_payment_transfer, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PaymentTransferViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class PaymentTransferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val agentImageView = itemView.agentImageView
        private val ageTextView = itemView.agentTextView

        fun bind(agent: PaymentAgent) {
            ageTextView.text = agent.name
            Log.d("PaymentTransferAd", agent.getImageUrl())
            agentImageView.setImageUrl(agent.getImageUrl(), R.drawable.ic_power_buy)
        }
    }

}