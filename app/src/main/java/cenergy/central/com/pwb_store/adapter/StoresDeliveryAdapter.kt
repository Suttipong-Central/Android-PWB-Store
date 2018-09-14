package cenergy.central.com.pwb_store.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.adapter.viewholder.StoresViewHolder

class StoresDeliveryAdapter(val listener: StoreClickListener) : RecyclerView.Adapter<StoresViewHolder>() {

    var selectedIndex: Int? = null
    var stores = arrayListOf<String>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresViewHolder {
        return StoresViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_stores, parent, false))
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {
        val store = stores[position]
        holder.bindView(store)
        holder.itemView.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
            listener.onItemClicked(store)
        }
        if (selectedIndex == position){
            holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.powerBuyPurple))
        } else {
            holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grayTextColor))
        }
    }
}
