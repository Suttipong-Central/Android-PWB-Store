package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import kotlinx.android.synthetic.main.list_item_text_header_compare_detail.view.*

class CompareHeaderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvLabel = itemView.labelTextView
    fun bind(item: CompareDetailAdapter.CompareTitleItem) {
        tvLabel.text = item.title
    }
}
