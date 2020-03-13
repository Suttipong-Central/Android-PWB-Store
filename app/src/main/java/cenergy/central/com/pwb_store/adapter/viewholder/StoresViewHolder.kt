package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class StoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val layout: ConstraintLayout = itemView.findViewById(R.id.layout_item)
    val storeName: PowerBuyTextView = itemView.findViewById(R.id.store_name)

    fun bindView(branch: Branch, selectedIndex: Int?) {
        storeName.text = branch.storeName

        if (selectedIndex == adapterPosition) {
            storeName.setTextColor(ContextCompat.getColor(itemView.context, R.color.textSelectedColor))
            layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.extraGray))
        } else {
            storeName.setTextColor(ContextCompat.getColor(itemView.context, R.color.grayTextColor))
            layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
        }
    }
}