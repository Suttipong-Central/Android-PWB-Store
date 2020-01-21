package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class HeaderCartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val header: PowerBuyTextView = itemView.findViewById(R.id.txtHeader)

    fun bindView(soldBy: String) {
        header.text = soldBy
    }
}