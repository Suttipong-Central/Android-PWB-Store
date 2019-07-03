package cenergy.central.com.pwb_store.adapter.viewholder

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.bus.event.ProductFilterItemBus
import kotlinx.android.synthetic.main.list_item_product_filter_header.view.*
import org.greenrobot.eventbus.EventBus

class CancelFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title = itemView.txt_header_filter

    init {
        title.typeface = Typeface.DEFAULT_BOLD
        title.text = itemView.context.getString(R.string.filter_all)
        title.setTextColor(ContextCompat.getColor(itemView.context, R.color.textDefaultColor))
    }

    fun bind() {
        itemView.setOnClickListener {
            EventBus.getDefault().post(ProductFilterItemBus(null, adapterPosition))
        }
    }
}