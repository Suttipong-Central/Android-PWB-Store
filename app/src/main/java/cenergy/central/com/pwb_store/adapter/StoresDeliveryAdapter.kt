package cenergy.central.com.pwb_store.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.adapter.viewholder.StoresViewHolder
import cenergy.central.com.pwb_store.model.Branch

class StoresDeliveryAdapter(val listener: StoreClickListener) : RecyclerView.Adapter<StoresViewHolder>() {

    var selectedIndex: Int? = null
    var branches = listOf<Branch>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresViewHolder {
        return StoresViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_stores, parent, false))
    }

    override fun getItemCount(): Int {
        return branches.size
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {
        val branch = branches[position]
        holder.bindView(branch)
        holder.itemView.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
            listener.onItemClicked(branch)
        }
        if (selectedIndex == position){
            holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.powerBuyPurple))
        } else {
            holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grayTextColor))
        }
    }
}
