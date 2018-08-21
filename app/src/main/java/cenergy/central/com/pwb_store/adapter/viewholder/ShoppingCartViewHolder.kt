package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_shopping_cart)

    fun bindView() {
        productName.text = "Product Name"
    }
}
