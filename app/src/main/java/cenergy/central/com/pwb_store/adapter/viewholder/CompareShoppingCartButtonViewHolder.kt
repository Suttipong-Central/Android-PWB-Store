package cenergy.central.com.pwb_store.adapter.viewholder

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class CompareShoppingCartButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var shoppingCartCardView: CardView = itemView.findViewById(R.id.card_view_shopping_cart)
    private var cartImg: ImageView = itemView.findViewById(R.id.img_cart)
    private var shoppingCartText: PowerBuyTextView = itemView.findViewById(R.id.shopping_cart_txt)

    fun setProductCompare(productCompare: CompareProduct) {
        if (productCompare.inStock) {
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
