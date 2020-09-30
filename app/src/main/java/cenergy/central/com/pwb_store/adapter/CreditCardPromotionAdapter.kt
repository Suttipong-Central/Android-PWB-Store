package cenergy.central.com.pwb_store.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.response.CreditCardPromotion
import cenergy.central.com.pwb_store.model.response.PaymentCreditCardPromotion.Companion.DISCOUNT_BY_PERCENT
import cenergy.central.com.pwb_store.model.response.PaymentInformationResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class CreditCardPromotionAdapter : RecyclerView.Adapter<CreditCardPromotionViewHolder>() {
    var items: ArrayList<CreditCardPromotion> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardPromotionViewHolder {
        return CreditCardPromotionViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_credit_card_on_top, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CreditCardPromotionViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class CreditCardPromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var border: FrameLayout = itemView.findViewById(R.id.layoutBorder)
    private var badgeIcon: ImageView = itemView.findViewById(R.id.badgeIcon)
    private var badgeTv: PowerBuyTextView = itemView.findViewById(R.id.badgeTv)
    private var tvPercent: PowerBuyTextView = itemView.findViewById(R.id.tvDiscount)

    @SuppressLint("Range")
    fun bind(creditCardOnTop: CreditCardPromotion) {
        val color = Color.parseColor(creditCardOnTop.bankColor)
        badgeIcon.setImageUrl(creditCardOnTop.getBankImageUrl())
        badgeIcon.setBackgroundColor(color)
        badgeTv.setTextColor(color)
        border.setBackgroundColor(color)
        if (creditCardOnTop.simpleAction == DISCOUNT_BY_PERCENT) {
            badgeTv.text = itemView.context.getString(R.string.format_percent, creditCardOnTop.discountAmount)
            tvPercent.text = itemView.context.getString(R.string.format_get_more_percent, creditCardOnTop.discountAmount)
        } else {
            badgeTv.text = itemView.context.getString(R.string.format_bath, creditCardOnTop.discountAmount)
            tvPercent.text = itemView.context.getString(R.string.format_get_more_bath, creditCardOnTop.discountAmount)
        }
    }
}