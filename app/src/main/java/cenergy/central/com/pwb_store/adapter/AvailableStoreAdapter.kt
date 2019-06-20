package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.AvailableDetailViewHolder
import cenergy.central.com.pwb_store.model.*

class AvailableStoreAdapter(var storeAvailableList: List<StoreAvailable>) : RecyclerView.Adapter<AvailableDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableDetailViewHolder {
        return AvailableDetailViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_avaliable_detail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AvailableDetailViewHolder, position: Int) {
        holder.setViewHolder(storeAvailableList[position])
    }

    override fun getItemCount(): Int {
        return storeAvailableList.size
    }

    fun setCompareAvailable(storeCode: String) {
        val sameStore = storeAvailableList.firstOrNull { it.sellerCode == storeCode }
        if(sameStore != null){
            var tempAvailableList = arrayListOf<StoreAvailable>()
            tempAvailableList.add(sameStore)
            storeAvailableList.forEach {
                if(tempAvailableList.indexOf(it) == -1){
                    tempAvailableList.add(it)
                }
            }
            storeAvailableList = tempAvailableList
            tempAvailableList = arrayListOf()
        }
        notifyDataSetChanged()
    }
}
