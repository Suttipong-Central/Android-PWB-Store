package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.Specification
import kotlinx.android.synthetic.main.adapter_product_spec.view.*

class SpecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvLabel = itemView.tvLabel
    private val tvValue = itemView.tvValue
    private val rootSpecView = itemView.rootSpecView

    fun bind(spec: Specification, isDarkBg: Boolean) {
        tvLabel.text = spec.label
        tvValue.text = spec.value
        rootSpecView.setBackgroundColor(ContextCompat.getColor(itemView.context,
                if (isDarkBg) R.color.highlightRow else R.color.white))
    }
}