package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.PaymentPromotionView
import cenergy.central.com.pwb_store.extensions.setImageUrl
import kotlinx.android.synthetic.main.item_creditcard_promotion.view.*
import kotlinx.android.synthetic.main.item_select_promotion.view.*

class CreditCardPromotionsAdapter(context: Context, items: MutableList<PaymentPromotionView>) :
        ArrayAdapter<PaymentPromotionView>(context, 0, items) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        return when (val item = getItem(position)) {
            is PaymentPromotionView.HeaderView -> {
                val view = layoutInflater.inflate(R.layout.item_select_promotion,
                        parent, false)
                setItemHeaderSelect(view, item)
                view
            }
            is PaymentPromotionView.PromotionDefaultView -> {
                val view = layoutInflater.inflate(R.layout.item_creditcard_promotion,
                        parent, false)
                setItemPromotionDefault(view, item)
                view
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_creditcard_promotion,
                        parent, false)
                setItemPromotion(view, item as PaymentPromotionView.PromotionView)
                view
            }
        }
    }

    private fun setItemPromotion(view: View, item: PaymentPromotionView.PromotionView) {
        val color: Int = Color.parseColor(item.bankColor)
        // set border color
        view.groupLayout.setBackgroundColor(color)
        view.ivBankImage.setImageUrl(item.bankImageUrl)
        view.ivBankImage.setBackgroundColor(color)
        view.tvPromotion.text = item.title
        view.tvPromotion.setTextColor(color)
    }

    private fun setItemPromotionDefault(view: View, item: PaymentPromotionView.PromotionDefaultView) {
        // set border color
        view.groupLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        view.ivBankImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_credit_card_white))
        view.ivBankImage.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        view.tvPromotion.text = item.title
        view.tvPromotion.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    private fun setItemHeaderSelect(view: View, item: PaymentPromotionView.HeaderView) {
        view.tvPromotionHeader.text = item.title
    }

    override fun isEnabled(position: Int) = position != 0
}