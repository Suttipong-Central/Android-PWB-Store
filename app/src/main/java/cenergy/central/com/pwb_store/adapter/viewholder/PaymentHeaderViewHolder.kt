package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.PaymentMethodViewHolder
import cenergy.central.com.pwb_store.model.PaymentMethodView
import kotlinx.android.synthetic.main.list_item_payment_header.view.*

class PaymentHeaderViewHolder(itemView: View) : PaymentMethodViewHolder<PaymentMethodView.HeaderItemView>(itemView) {
    private val titleTextView = itemView.titleTextView
    override fun bindView(item: PaymentMethodView.HeaderItemView) {
        titleTextView.text = itemView.context.getString(R.string.select_payment_types)
    }
}