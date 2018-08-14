package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.util.*

class CompareShoppingCartButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var shoppingCartCardView: CardView = itemView.findViewById(R.id.card_view_shopping_cart)
    var cartImg: ImageView = itemView.findViewById(R.id.img_cart)
    var shoppingCartText: PowerBuyTextView = itemView.findViewById(R.id.shopping_cart_txt)

    fun setProductCompare(productCompare: CompareProduct) {
        val randStock = Random().nextInt(2)
        if (randStock > 0) {
            shoppingCartCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.powerBuyPurple))
            cartImg.visibility = View.VISIBLE
            shoppingCartText.text = itemView.resources.getString(R.string.add_to_cart)
        } else {
            shoppingCartCardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.hintColor))
            cartImg.visibility = View.GONE
            shoppingCartText.text = itemView.resources.getString(R.string.product_out_stock)
        }
    }
}