package cenergy.central.com.pwb_store.adapter.viewholder

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class CompareHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val clearAllButton: PowerBuyTextView = itemView.findViewById(R.id.clear_all_button)

    fun setViewHolder(disableButton: Boolean, listener: CompareItemListener) {
        if (disableButton) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                clearAllButton.setTextColor(itemView.context.resources.getColor(R.color.grayTextColor))
            } else {
                clearAllButton.setTextColor(ContextCompat.getColor(itemView.context, R.color.grayTextColor))
            }
            clearAllButton.setOnClickListener(null)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                clearAllButton.setTextColor(itemView.context.resources.getColor(R.color.primaryButtonColor))
            } else {
                clearAllButton.setTextColor(ContextCompat.getColor(itemView.context, R.color.primaryButtonColor))
            }
            clearAllButton.setOnClickListener { listener.onClearAllProductCompare() }
        }
    }
}