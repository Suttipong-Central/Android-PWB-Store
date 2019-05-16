package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImage
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class DrawerSubHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mIcon = itemView.findViewById<ImageView>(R.id.icon)
    private var mTitle = itemView.findViewById<PowerBuyTextView>(R.id.title)

    fun bindItem(title: String) {
        mTitle.text = title
        val context = itemView.context

        when (title) {
            context.getString(R.string.drawer_compare) -> {
                mIcon.setImage(R.drawable.ic_compare)
            }
            context.getString(R.string.drawer_cart) -> {
                mIcon.setImage(R.drawable.ic_cart_white)
            }
            context.getString(R.string.drawer_history) -> {
                mIcon.setImage(R.drawable.ic_list_history)
            }
            context.getString(R.string.drawer_logout) -> {
                mIcon.setImage(R.drawable.ic_logout)
            }
        }
    }
}
