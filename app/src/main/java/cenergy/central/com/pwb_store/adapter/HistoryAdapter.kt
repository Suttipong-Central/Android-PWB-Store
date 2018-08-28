package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.HistoryViewHolder
import cenergy.central.com.pwb_store.model.response.OrderResponse

class HistoryAdapter(private val orderResponses: MutableList<OrderResponse>) : RecyclerView.Adapter<HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_history, parent, false))
    }

    override fun getItemCount(): Int {
        return orderResponses.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bindView(orderResponses[position])
    }

}
