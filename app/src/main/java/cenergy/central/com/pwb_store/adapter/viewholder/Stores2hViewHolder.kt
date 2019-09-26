package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.item_stores_2h.view.*

class Stores2hViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvStoreName: PowerBuyTextView = itemView.storeNameTextView
    private val tvDeliveryDescription: PowerBuyTextView = itemView.deliveryTextView
    fun bind(item: BranchResponse, selectedIndex: Int?) {
        if (selectedIndex == adapterPosition) {
            tvStoreName.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.powerBuyPurple))
        } else {
            tvStoreName.setTextColor(ContextCompat.getColor(itemView.context,
                    R.color.grayTextColor))
        }

        tvStoreName.text = item.branch.storeName
        tvDeliveryDescription.text = itemView.context.getString(
                R.string.format_store_ispu_delivery, item.branch.ispuDelivery)

    }
}