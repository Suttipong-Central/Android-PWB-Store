package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class HeaderCartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val tvSoldBy: PowerBuyTextView = itemView.findViewById(R.id.tvSoldBy)

    fun bindView(soldBy: String) {
        tvSoldBy.text = soldBy
    }
}