package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.Order
import cenergy.central.com.pwb_store.view.PowerBuyTextView

@SuppressLint("SetTextI18n")
class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var cardView: CardView = itemView.findViewById(R.id.card_view)
    private var orderNumber: PowerBuyTextView = itemView.findViewById(R.id.order_number_history)
    private var orderDate: PowerBuyTextView = itemView.findViewById(R.id.date_history)
    private var orderMember: PowerBuyTextView = itemView.findViewById(R.id.order_name_history)

    fun bindView(order: Order) {
        orderNumber.text = "${itemView.context.resources.getString(R.string.order_number)} ${order.orderId}"
        orderDate.text = "${itemView.context.getString(R.string.order_date_history)} ${order.getDisplayTimeCreated(itemView.context)}"
        orderMember.text = "${itemView.context.resources.getString(R.string.name_user_order)} ${order.memberName}"
    }
}
