package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.BadgeListener
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.list_item_badge_select.view.*

class BadgeSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val badgeTv: PowerBuyTextView = itemView.findViewById(R.id.badgeTv)
    private val layout: ConstraintLayout = itemView.findViewById(R.id.badgeLayout)
    fun bind(badge: String, selectedPosition: Int, badgeListener: BadgeListener?) {
        badgeTv.text = badge
        layout.setOnClickListener { badgeListener?.onBadgeSelectedListener(adapterPosition) }
        if (adapterPosition == selectedPosition){
            badgeTv.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            badgeTv.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_badge_selected)
        } else {
            badgeTv.setTextColor(ContextCompat.getColor(itemView.context, R.color.badge))
            badgeTv.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_badge_unselected)
        }
    }
}