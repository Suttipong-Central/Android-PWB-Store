package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.BadgeListener
import cenergy.central.com.pwb_store.fragment.ProductPromotionFragment.Companion.CREDIT_CARD_ON_TOP
import cenergy.central.com.pwb_store.fragment.ProductPromotionFragment.Companion.FREEBIE_ITEM
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class BadgeSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val badgeTv: PowerBuyTextView = itemView.findViewById(R.id.badgeTv)
    private val layout: ConstraintLayout = itemView.findViewById(R.id.badgeLayout)

    fun bind(badge: Int, selectedPosition: Int, badgeListener: BadgeListener?) {
        itemView.context?.let { context ->
            badgeTv.text = when(badge){
                FREEBIE_ITEM -> {
                    context.getString(R.string.badge_free_item)
                }
                CREDIT_CARD_ON_TOP -> {
                    context.getString(R.string.badge_credit_card_on_top)
                }
                else -> {
                    context.getString(R.string.badge_installment_plan)
                }
            }
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
}