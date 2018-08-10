package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class CompareShoppingCartButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var shoppingCartCardView: CardView = itemView.findViewById(R.id.card_view_shopping_cart)
    var cartImg: ImageView = itemView.findViewById(R.id.img_cart)
    var shoppingCartText: PowerBuyTextView = itemView.findViewById(R.id.shopping_cart_txt)

    fun setProductCompare(productCompare: String) {

    }
}
