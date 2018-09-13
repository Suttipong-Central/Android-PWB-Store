package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class StoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val storeName: PowerBuyTextView = itemView.findViewById(R.id.store_name)

    fun bindView(store: String) {
        storeName.text = store
    }
}