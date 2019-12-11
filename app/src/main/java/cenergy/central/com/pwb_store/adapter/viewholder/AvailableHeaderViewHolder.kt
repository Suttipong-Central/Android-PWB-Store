package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_available_header.view.*

class AvailableHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvStoreCode = itemView.tvStoreCode
    private val tvStoreName = itemView.tvStoreName
    private val tvStoreContact = itemView.tvStoreContact
    private val tvStoreStock = itemView.tvStoreStock

    fun bind(sortedBy: Int) {

    }
}
