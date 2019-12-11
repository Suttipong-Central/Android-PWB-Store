package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.item_stores_2h.view.*

class Stores2hViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val layout: ConstraintLayout = itemView.layout_item
    private val tvStoreName: PowerBuyTextView = itemView.storeNameTextView
    private val tvDeliveryDescription: PowerBuyTextView = itemView.deliveryTextView
    fun bind(item: BranchResponse, selectedIndex: Int?) {
        if (selectedIndex == adapterPosition) {
            tvStoreName.setTextColor(ContextCompat.getColor(itemView.context, R.color.textSelectedColor))
            layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.extraGray))
        } else {
            tvStoreName.setTextColor(ContextCompat.getColor(itemView.context, R.color.grayTextColor))
            layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
        }
        tvStoreName.text = item.branch.storeName
        tvDeliveryDescription.text = itemView.context.getString(
                R.string.format_store_ispu_delivery, item.branch.ispuDelivery)
    }
}