package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import kotlinx.android.synthetic.main.list_item_text_compare_detail.view.*

class CompareItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDetail = itemView.tvDetail

    fun setViewHolder(item: CompareDetailAdapter.CompareItem) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvDetail.text = Html.fromHtml(item.detail, Html.FROM_HTML_MODE_LEGACY)
        } else {
            tvDetail.text = Html.fromHtml(item.detail)
        }
    }
}
