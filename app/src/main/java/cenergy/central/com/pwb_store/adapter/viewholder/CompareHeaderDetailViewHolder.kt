package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import kotlinx.android.synthetic.main.list_item_text_header_compare_detail.view.*

class CompareHeaderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvLabel = itemView.labelTextView
    fun bind(item: CompareDetailAdapter.CompareTitleItem) {
        if (item.title == "Short Description"){
            tvLabel.text = itemView.context.getString(R.string.product_overview)
        } else {
            tvLabel.text = item.title
        }
    }
}
