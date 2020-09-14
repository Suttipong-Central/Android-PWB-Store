package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.CreditCardPromotionsAdapter
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethod
import cenergy.central.com.pwb_store.model.PaymentMethodView
import cenergy.central.com.pwb_store.model.response.PaymentCreditCardPromotion
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.list_item_pay_by_credite_card.view.*

class FullPaymentViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {
    private val radioPayment: RadioButton = itemView.radioPayment
    private val expandLayout: ConstraintLayout = itemView.expandLayout
    private val tvSelectPromotions: PowerBuyTextView = itemView.tvSelectPromotions
    private val layoutSpinner: FrameLayout = itemView.layoutSpinner
    private val promotionOptions: AppCompatSpinner = itemView.promotionSpinner
    private val onPromotionItemSelectedCallback = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(adapter: AdapterView<*>?) {

        }

        override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val item = adapter?.getItemAtPosition(position)
            if (item != null && item is PaymentPromotionView.PromotionView) {
                listener.onSelectedPromotion(PaymentMethod.FULL_PAYMENT, item.promotionId, item.promotionCode)
            } else if (item != null && item is PaymentPromotionView.PromotionDefaultView) {
                listener.onSelectedDefaultPromotion(PaymentMethod.FULL_PAYMENT)
            }
        }
    }

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        radioPayment.text = itemView.context.getString(R.string.fullpayment)
        radioPayment.isChecked = item.selected

        // setup credit card promotions
        if (!item.promotions.isNullOrEmpty()) {
            layoutSpinner.visibility = View.VISIBLE
            tvSelectPromotions.visibility = View.VISIBLE
            promotionOptions.visibility = View.VISIBLE
            val items = arrayListOf<PaymentPromotionView>()
            items.add(PaymentPromotionView.HeaderView(
                    itemView.context.getString(R.string.select_credit_card_promotion)))

            items.addAll(item.promotions.map {
                val title = when (it.simpleAction) {
                    PaymentCreditCardPromotion.DISCOUNT_BY_PERCENT -> {
                        itemView.context.getString(R.string.format_credit_card_promotion_percent, it.discountAmount)
                    }
                    else -> {
                        itemView.context.getString(R.string.format_credit_card_promotion, it.discountAmount)
                    }
                }
                PaymentPromotionView.PromotionView(
                        promotionId = it.promotionId,
                        promotionCode = it.promotionCode,
                        title = title,
                        bankImageUrl = it.getBankImageUrl(),
                        bankColor = it.bankColor
                )
            })

            items.add(PaymentPromotionView.PromotionDefaultView(
                    title = itemView.context.getString(R.string.default_credit_card_promotion)))
            promotionOptions.adapter = CreditCardPromotionsAdapter(itemView.context, items)
            val selectPromotion = items.firstOrNull { pp -> pp is PaymentPromotionView.PromotionView && pp.promotionId == item.promotionId }
            if (selectPromotion != null) {
                val selectedPromotion = selectPromotion as PaymentPromotionView.PromotionView
                val index = items.indexOf(selectedPromotion)
                promotionOptions.setSelection(index)
            }
            promotionOptions.onItemSelectedListener = onPromotionItemSelectedCallback
        } else {
            layoutSpinner.visibility = View.GONE
            tvSelectPromotions.visibility = View.GONE
            promotionOptions.visibility = View.GONE
        }

        expandLayout.visibility = if (item.selected) View.VISIBLE else View.GONE

        // setup onClick
        itemView.setOnClickListener {
            if (!item.selected) {
                listener.onClickedPaymentItem(item.paymentMethod)
            }
        }
    }
}

sealed class PaymentPromotionView {
    data class HeaderView(val title: String) : PaymentPromotionView()

    data class PromotionView(val promotionId: Int, val promotionCode: String, val title: String, val bankImageUrl: String,
                             val bankColor: String) : PaymentPromotionView()

    data class PromotionDefaultView(val promotionId: Int = -1, val title: String) : PaymentPromotionView()
}