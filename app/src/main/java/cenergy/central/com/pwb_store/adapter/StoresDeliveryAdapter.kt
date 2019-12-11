package cenergy.central.com.pwb_store.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.CheckoutType
import cenergy.central.com.pwb_store.adapter.interfaces.StoreClickListener
import cenergy.central.com.pwb_store.adapter.viewholder.Stores2hViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.StoresViewHolder
import cenergy.central.com.pwb_store.model.response.BranchResponse

class StoresDeliveryAdapter(val listener: StoreClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedIndex: Int? = null
    var checkoutType: CheckoutType = CheckoutType.NORMAL
    var items = listOf<BranchResponse>()

    fun updateItems(checkoutType: CheckoutType, items: List<BranchResponse>) {
        this.checkoutType = checkoutType
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_STORE_ITEM -> {
                StoresViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_stores, parent, false))
            }

            VIEW_STORE_2H_ITEM -> {
                Stores2hViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_stores_2h, parent, false))
            }

            else -> throw IllegalStateException("View type $viewType not found on StoresDeliveryAdapter.")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (checkoutType == CheckoutType.NORMAL) VIEW_STORE_ITEM else VIEW_STORE_2H_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[holder.adapterPosition]

        when (holder) {
            is StoresViewHolder -> {
                holder.bindView(item.branch, selectedIndex)
            }

            is Stores2hViewHolder -> {
                holder.bind(item, selectedIndex)
            }
        }

        // onclick
        holder.itemView.setOnClickListener {
            selectedIndex = holder.adapterPosition
            notifyDataSetChanged()
            listener.onItemClicked(item)
        }
    }

    companion object {
        const val VIEW_STORE_ITEM = 1
        const val VIEW_STORE_2H_ITEM = 2
    }
}
