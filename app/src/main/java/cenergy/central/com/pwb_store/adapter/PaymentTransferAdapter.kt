package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.response.PaymentAgent
import kotlinx.android.synthetic.main.item_payment_transfer.view.*

class PaymentTransferAdapter(val clickListener: (PaymentAgent) -> Unit) : ListAdapter<PaymentTransferAdapter.AgentItem,
        PaymentTransferAdapter.PaymentTransferViewHolder>(AgentItemDiffUtil()) {

    var source = listOf<AgentItem>()
        set(value) {
            field = value
            this.submitList(ArrayList(value))
        }

    fun getItemSelected(): AgentItem {
        return source.first { it.isSelect }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentTransferViewHolder {
        return PaymentTransferViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.item_payment_transfer, parent, false))
    }

    override fun onBindViewHolder(holder: PaymentTransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AgentItemDiffUtil : DiffUtil.ItemCallback<AgentItem>() {
        override fun areItemsTheSame(oldItem: AgentItem, newItem: AgentItem): Boolean {
            return oldItem.agent.agentId == newItem.agent.agentId
        }

        override fun areContentsTheSame(oldItem: AgentItem, newItem: AgentItem): Boolean {
            return false // TODO: improve it only force for now
        }
    }

    inner class PaymentTransferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val agentImageView = itemView.agentImageView
        private val ageTextView = itemView.agentTextView
        private val groupLayout = itemView.groupLayout

        fun bind(item: AgentItem) {
            val agent = item.agent
            ageTextView.visibility = if (item.isSelect) View.VISIBLE else View.INVISIBLE
            ageTextView.text = agent.name
            groupLayout.background = if (item.isSelect) {
                ContextCompat.getDrawable(itemView.context, R.drawable.agent_background_selected)
            } else {
                null
            }
            agentImageView.setImageUrl(agent.getImageUrl(), R.drawable.ic_power_buy)
            itemView.setOnClickListener {
                clickListener(agent)
            }
        }
    }

    data class AgentItem(val agent: PaymentAgent, var isSelect: Boolean = false)
}