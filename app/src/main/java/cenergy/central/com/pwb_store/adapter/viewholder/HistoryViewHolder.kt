package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.view.PowerBuyTextView

@SuppressLint("SetTextI18n")
class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var orderNumber: PowerBuyTextView = itemView.findViewById(R.id.order_number_history)
    var orderDate: PowerBuyTextView = itemView.findViewById(R.id.date_history)
    var orderMember: PowerBuyTextView = itemView.findViewById(R.id.order_name_history)

    fun bindView(orderResponse: OrderResponse) {
        orderNumber.text = "${itemView.context.resources.getString(R.string.order_number)} ${orderResponse.orderId}"
        orderDate.text = "${itemView.context.resources.getString(R.string.order_date)} ${orderResponse.updatedAt}"
        orderMember.text = "${itemView.context.resources.getString(R.string.order_number)} ${orderResponse.billingAddress?.getDisplayName()}"
    }
}
