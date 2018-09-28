package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import java.util.ArrayList

import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.AvaliableTopicViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.EmptyViewHolder
import cenergy.central.com.pwb_store.model.*

/**
 * Created by napabhat on 8/16/2017 AD.
 */

class AvailableStoreAdapter(val mContext: Context, private val mStoreDao: StoreDao) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val emptyResult = ViewType(VIEW_TYPE_ID_EMPTY_RESULT)
    private val mListViewType = ArrayList<IViewType>()

    val spanSize: GridLayoutManager.SpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (getItemViewType(position)) {
                VIEW_TYPE_ID_STORE_TOPIC -> 4
                VIEW_TYPE_ID_STORE_DETAIL -> 4
                VIEW_TYPE_ID_EMPTY_RESULT -> 4
                else -> 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when (viewType) {
            VIEW_TYPE_ID_EMPTY_RESULT -> return EmptyViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_loading_result, parent, false)
            )
            VIEW_TYPE_ID_STORE_TOPIC -> return AvaliableTopicViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_avaliable_topic, parent, false)
            )
            VIEW_TYPE_ID_STORE_DETAIL -> return AvaliableDetailViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_avaliable_detail, parent, false)
            )
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewTypeId = getItemViewType(position)
        val viewType = mListViewType[position]
        when (viewTypeId) {
            VIEW_TYPE_ID_EMPTY_RESULT -> if (holder is EmptyViewHolder) {
                holder.setViewHolder()
            }

            VIEW_TYPE_ID_STORE_TOPIC -> if (holder is AvaliableTopicViewHolder) {
                holder.setViewHolder()
            }

            VIEW_TYPE_ID_STORE_DETAIL -> if (viewType is AvaliableStoreItem && holder is AvaliableDetailViewHolder) {
                holder.setViewHolder(viewType, mStoreDao)
            }
        }
    }

    override fun getItemCount(): Int {
        return mListViewType.size
    }

    override fun getItemViewType(position: Int): Int {
        return mListViewType[position].viewTypeId
    }

    fun setCompareAvailable(availableStoreDao: AvailableStoreDao, storeId: String) {

        val makeAvailableStoreItems = arrayListOf<AvaliableStoreItem>()

        for (availableStoreItem in availableStoreDao.avaliableProducts[0].avaliableStoreItems) {
            for (storeList in mStoreDao.storeLists) {
                if (availableStoreItem.storeName == storeList.storeId && availableStoreItem.stock > 0) {
                    makeAvailableStoreItems.add(AvaliableStoreItem(
                            availableStoreItem.storeName,
                            storeList.storeAddrNo,
                            availableStoreItem.stock,
                            storeList.telephone,
                            storeList.storeName))
                }
            }
        }

        if(makeAvailableStoreItems.size > 0){
            availableStoreDao.viewTypeId = VIEW_TYPE_ID_STORE_TOPIC
            mListViewType.add(availableStoreDao)

            val storeStaff = makeAvailableStoreItems.firstOrNull{it.storeName == storeId}
            if(storeStaff != null){
                storeStaff.viewTypeId = VIEW_TYPE_ID_STORE_DETAIL
                mListViewType.add(storeStaff)
                makeAvailableStoreItems.sortedBy { it.storeName }.forEach { if (it.storeName != storeId){
                    it.viewTypeId = VIEW_TYPE_ID_STORE_DETAIL
                    mListViewType.add(it)
                }}
            } else {
                makeAvailableStoreItems.sortedBy { it.storeName }.forEach {
                    it.viewTypeId = VIEW_TYPE_ID_STORE_DETAIL
                    mListViewType.add(it)
                }
            }
        } else {
            mListViewType.add(emptyResult)
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ID_EMPTY_RESULT = 0
        private const val VIEW_TYPE_ID_STORE_TOPIC = 1
        private const val VIEW_TYPE_ID_STORE_DETAIL = 2
    }
}
