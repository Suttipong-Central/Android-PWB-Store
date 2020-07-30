package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.model.PaymentMethodView
import kotlinx.android.synthetic.main.list_item_pay_common.view.*

class BankAndCounterServiceViewHolder(itemView: View, private val listener: PaymentItemClickListener)
    : PaymentMethodViewHolder<PaymentMethodView.PaymentItemView>(itemView) {
    private val radioPayment: RadioButton = itemView.radioPayment
    private val description: TextView = itemView.tvDescription
    private val expandLayout: ConstraintLayout = itemView.expandLayout

    override fun bindView(item: PaymentMethodView.PaymentItemView) {
        radioPayment.text = itemView.context.getString(R.string.pay_by_bank_transfer)
        radioPayment.isChecked = item.selected
        expandLayout.visibility = if (item.selected) View.VISIBLE else View.GONE
        description.visibility = View.GONE
        itemView.setOnClickListener {
            if (!item.selected) {
                listener.onClickedPaymentItem(item.paymentMethod)
            }
        }
    }
}