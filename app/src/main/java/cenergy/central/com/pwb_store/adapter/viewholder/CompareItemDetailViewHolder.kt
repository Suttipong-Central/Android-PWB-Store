package cenergy.central.com.pwb_store.adapter.viewholder

import android.text.Html
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import kotlinx.android.synthetic.main.list_item_text_compare_detail.view.*

class CompareItemDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDetail = itemView.tvDetail
    fun bind(item: CompareDetailAdapter.CompareItem) {
        if (item.isHTML) {
            tvDetail.gravity = Gravity.START
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvDetail.text = Html.fromHtml("<div style=\"text-align: left\">${item.detail}</div>", Html.FROM_HTML_MODE_LEGACY)
            } else {
                tvDetail.text = Html.fromHtml(item.detail)
            }
        } else {
            tvDetail.gravity = Gravity.CENTER_HORIZONTAL
            tvDetail.text = if (item.detail.toLowerCase() == "n/a") {
                "-"
            } else {
                item.detail
            }
        }
    }
}
