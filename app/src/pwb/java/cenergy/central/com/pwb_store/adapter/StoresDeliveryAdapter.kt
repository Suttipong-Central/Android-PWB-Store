package cenergy.central.com.pwb_store.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.adapter.viewholder.StoresViewHolder
import cenergy.central.com.pwb_store.model.Branch

class StoresDeliveryAdapter(val listener: StoreClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedIndex: Int? = 0
    var branches = listOf<Branch?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            StoresViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_stores, parent, false))
        } else {
            ProgressViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_progress, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return branches.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (branches[position] != null) VIEW_ITEM else VIEW_LOADING
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StoresViewHolder) {
            val branch = branches[position]
            holder.bindView(branch!!)
            holder.itemView.setOnClickListener {
                selectedIndex = position
                notifyDataSetChanged()
                listener.onItemClicked(branch)
            }
            if (selectedIndex == position) {
                holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context,
                        R.color.powerBuyPurple))
            } else {
                holder.storeName.setTextColor(ContextCompat.getColor(holder.itemView.context,
                        R.color.grayTextColor))
            }
        }
    }

    companion object {
        const val VIEW_ITEM = 1
        const val VIEW_LOADING = 2
    }

    inner class ProgressViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
}
