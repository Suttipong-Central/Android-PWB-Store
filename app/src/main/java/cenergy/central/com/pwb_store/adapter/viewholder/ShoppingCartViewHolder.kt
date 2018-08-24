package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_shopping_cart)
    private val productCode: PowerBuyTextView = itemView.findViewById(R.id.product_code_list_shopping_card)
    private val productPrice: PowerBuyTextView = itemView.findViewById(R.id.price_list_shopping_cart)
    private val productQty: PowerBuyIncreaseOrDecreaseView = itemView.findViewById(R.id.qty__list_shopping_cart)
    private val totalPrice: PowerBuyTextView = itemView.findViewById(R.id.total_price_list_shopping_cart)
    private val deleteImageView: ImageView = itemView.findViewById(R.id.deleteItemImageView)

    @SuppressLint("SetTextI18n")
    fun bindView(cartItem: CartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        val unit = Contextor.getInstance().context.getString(R.string.baht)
        productName.text = cartItem.name
        productCode.text = "${itemView.context.resources.getString(
                R.string.product_code)} ${cartItem.sku}"
        productPrice.text = "${itemView.context.resources.getString(
                R.string.product_price)} ${getDisplayPrice(unit, cartItem.price.toString())}"
        productQty.setQty(cartItem.qty!!)
        totalPrice.text = getDisplayPrice(unit, getToTalPrice(productQty.getQty(), cartItem.price!!))

        deleteImageView.setOnClickListener {
            confirmDelete(cartItem, listener)
        }
    }

    private fun getToTalPrice(qty: Int, price: Double): String {
        return (qty * price).toString()
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun confirmDelete(cartItem: CartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        val context = itemView.context
        val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialogTheme)
        builder.setMessage(context.getString(R.string.title_confirm_delete_item))
        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
            cartItem.cartId?.let { cartId ->
                cartItem.id?.let { itemId -> listener?.onDeleteItem(cartId, itemId) }
                dialog?.dismiss()
            }
        }

        builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ -> dialog?.dismiss() }
        builder.create().show()
    }
}
