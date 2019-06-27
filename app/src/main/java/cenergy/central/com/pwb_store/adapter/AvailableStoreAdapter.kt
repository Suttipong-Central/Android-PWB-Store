package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.AvailableDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableHeaderViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.EmptyViewHolder
import cenergy.central.com.pwb_store.model.StoreAvailable

class AvailableStoreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var storeAvailableList: ArrayList<AvailableStoreItem> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_ITEM_EMPTY -> {
                EmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_loading_result, parent, false))
            }
            VIEW_ITEM_HEADER -> {
                AvaliableHeaderViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_avaliable_header, parent, false))
            }
            else -> {
                 AvailableDetailViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_avaliable_detail, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AvailableDetailViewHolder) {
            holder.setViewHolder(storeAvailableList[position] as StoreAvailable)
        }
    }

    override fun getItemCount(): Int {
        return storeAvailableList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (storeAvailableList[position]) {
            is ItemEmpty -> VIEW_ITEM_EMPTY
            is AvailableStoreHeader -> VIEW_ITEM_HEADER
            else -> VIEW_ITEM_STORE
        }
    }

    fun setCompareAvailable(storeCode: String, storeAvailableList: List<StoreAvailable>) {

        // empty items
        if (storeAvailableList.isNullOrEmpty()) {
            this.storeAvailableList = arrayListOf()
            this.storeAvailableList.add(ItemEmpty())
            notifyDataSetChanged()
            return
        }

        val sameStore = storeAvailableList.firstOrNull { it.sellerCode == storeCode }
        if (sameStore != null) {
            var tempAvailableList = arrayListOf<AvailableStoreItem>()
            tempAvailableList.add(AvailableStoreHeader())
            tempAvailableList.add(sameStore)
            storeAvailableList.forEach {
                if (tempAvailableList.indexOf(it) == -1) {
                    tempAvailableList.add(it)
                }
            }
            this.storeAvailableList = tempAvailableList
            tempAvailableList = arrayListOf()
        }
        notifyDataSetChanged()
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
