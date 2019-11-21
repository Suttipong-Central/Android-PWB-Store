package cenergy.central.com.pwb_store.adapter.viewholder

import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.ProductValue

class ShadeSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val shadeLayout: ConstraintLayout = itemView.findViewById(R.id.layoutShade)
    private val shadeView: View = itemView.findViewById(R.id.shadeView)

    fun bind(productValue: ProductValue, selectedIndex: Int?) {
        if (productValue.valueExtension?.value != null) {
            shadeView.setBackgroundColor(Color.parseColor(productValue.valueExtension?.value))
        }
        if (selectedIndex == adapterPosition) {
            shadeLayout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blackText))
        } else {
            shadeLayout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.lightGray2))
        }
    }
}