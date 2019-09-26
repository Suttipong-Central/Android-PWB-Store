package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class StoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val storeName: PowerBuyTextView = itemView.findViewById(R.id.store_name)

    fun bindView(branch: Branch, selectedIndex: Int?) {
        storeName.text = branch.storeName

        if (selectedIndex == adapterPosition) {
            storeName.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.powerBuyPurple))
        } else {
            storeName.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.grayTextColor))
        }
    }
}