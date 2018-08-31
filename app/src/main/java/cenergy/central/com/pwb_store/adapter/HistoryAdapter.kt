package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.HistoryViewHolder
import cenergy.central.com.pwb_store.manager.listeners.HistoryClickListener
import cenergy.central.com.pwb_store.model.Order

class HistoryAdapter(var listener: HistoryClickListener, private val orders: MutableList<Order>) : RecyclerView.Adapter<HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_history, parent, false))
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orders[position]
        holder.cardView.setOnClickListener {
            listener.onClickHistory(order.orderId)
        }
        holder.bindView(order)
    }

}
