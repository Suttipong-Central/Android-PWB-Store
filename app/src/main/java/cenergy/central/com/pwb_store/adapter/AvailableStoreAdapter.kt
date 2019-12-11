package cenergy.central.com.pwb_store.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.AvailableDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.AvailableHeaderViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.EmptyViewHolder
import cenergy.central.com.pwb_store.fragment.AvailableFragment.Companion.SORT_NONE
import cenergy.central.com.pwb_store.model.StoreAvailable

class AvailableStoreAdapter() : ListAdapter<AvailableStoreAdapter.AvailableStoreItem,
        RecyclerView.ViewHolder>(StockItemDiffCallback()) {

    private var sortedBy : Int = SORT_NONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_ITEM_EMPTY -> {
                EmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_loading_result, parent, false))
            }
            VIEW_ITEM_HEADER -> {
                AvailableHeaderViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_available_header, parent, false))
            }
            else -> {
                AvailableDetailViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_avaliable_detail, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AvailableDetailViewHolder -> {
                holder.setViewHolder(getItem(position) as StoreAvailable) // first?
            }
            is AvailableHeaderViewHolder -> {
                holder.bind(sortedBy) // header
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ItemEmpty -> VIEW_ITEM_EMPTY
            is AvailableStoreHeader -> VIEW_ITEM_HEADER
            else -> VIEW_ITEM_STORE
        }
    }

    class StockItemDiffCallback : DiffUtil.ItemCallback<AvailableStoreItem>() {
        override fun areItemsTheSame(oldItem: AvailableStoreItem, newItem: AvailableStoreItem): Boolean {
            return if (oldItem is StoreAvailable && newItem is StoreAvailable) {
                oldItem.sellerCode == newItem.sellerCode
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: AvailableStoreItem, newItem: AvailableStoreItem): Boolean {
            return if (oldItem is StoreAvailable && newItem is StoreAvailable) {
                (oldItem.name == newItem.name && oldItem.contactPhone == newItem.contactPhone
                        && oldItem.qty == newItem.qty && oldItem.sellerCode == newItem.sellerCode)
            } else {
                false
            }
        }
    }

    fun setStoreStockItems(storeCode: String, storeAvailableList: List<StoreAvailable>, sortBy: Int) {
        this.sortedBy = sortBy

        val items: ArrayList<AvailableStoreItem>
        // empty items
        if (storeAvailableList.isNullOrEmpty()) {
            items = arrayListOf()
            items.add(ItemEmpty())
            submitList(items)
            return
        }

        var tempAvailableList = arrayListOf<AvailableStoreItem>()
//        tempAvailableList.add(AvailableStoreHeader()) // header
        val sameStore = storeAvailableList.firstOrNull { it.sellerCode == storeCode }
        if (sameStore != null) {
            sameStore.isHighLight = true
            tempAvailableList.add(sameStore)
        }

        storeAvailableList.forEach {
            if (tempAvailableList.indexOf(it) == -1) {
                tempAvailableList.add(it)
            }
        }

        items = tempAvailableList
        tempAvailableList = arrayListOf()

        submitList(items)
    }

    interface AvailableStoreItem
    class ItemEmpty : AvailableStoreItem
    class AvailableStoreHeader : AvailableStoreItem

    companion object {
        private const val VIEW_ITEM_EMPTY = 0
        private const val VIEW_ITEM_STORE = 1
        private const val VIEW_ITEM_HEADER = 2
    }
}
