package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView.QuantityAction.ACTION_DECREASE
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView.QuantityAction.ACTION_INCREASE
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PowerBuyIncreaseOrDecreaseView.OnViewClickListener {

    // widget view
    private val productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_shopping_cart)
    private val productCode: PowerBuyTextView = itemView.findViewById(R.id.product_code_list_shopping_card)
    private val productPrice: PowerBuyTextView = itemView.findViewById(R.id.price_list_shopping_cart)
    private val productQty: PowerBuyIncreaseOrDecreaseView = itemView.findViewById(R.id.qty_list_shopping_cart)
    private val qtyText: PowerBuyTextView = itemView.findViewById(R.id.txt_qty_title_list_shopping_cart)
    private val qtyTextTitle: PowerBuyTextView = itemView.findViewById(R.id.qty_title_list_shopping_cart)
    private val totalPrice: PowerBuyTextView = itemView.findViewById(R.id.total_price_list_shopping_cart)
    private val deleteImageView: ImageView = itemView.findViewById(R.id.deleteItemImageView)
    private val productImage: ImageView = itemView.findViewById(R.id.product_image_list_shopping_cart)

    // data
    private val database = RealmController.getInstance()
    private var listener: ShoppingCartAdapter.ShoppingCartListener? = null
    private lateinit var cartItem: CartItem

    @SuppressLint("SetTextI18n")
    fun bindView(cartItem: CartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        val cacheCartItem = database.getCacheCartItem(cartItem.id) // get cacheCartItem
        val unit = itemView.context.getString(R.string.baht)
        this.listener = listener
        this.cartItem = cartItem
        productName.text = cartItem.name
        productCode.text = "${itemView.context.resources.getString(
                R.string.product_code)} ${cartItem.sku}"
        productPrice.text = "${itemView.context.resources.getString(
                R.string.product_price)} ${getDisplayPrice(unit, cartItem.price.toString())}"

        if (cacheCartItem != null) {
            // get image from cache
            Glide.with(itemView.context)
                    .load(cacheCartItem.imageUrl)
                    .placeholder(R.drawable.ic_pwb_logo_detail)
                    .crossFade()
                    .fitCenter()
                    .into(productImage)
            
            productQty.setOnClickQuantity(this, true)
            deleteImageView.visibility = View.VISIBLE
            deleteImageView.setOnClickListener {
                confirmDelete(cartItem, listener)
            }
        } else {
            totalPrice.setEnableStrikeThrough(true)
            productQty.setOnClickQuantity(this, false)
            deleteImageView.visibility = View.GONE
        }

        productQty.setMaximum(if (cacheCartItem == null) 1 else cacheCartItem.maxQTY ?: 1)
        productQty.setQty(cartItem.qty!!)
        totalPrice.text = getDisplayPrice(unit, getToTalPrice(productQty.getQty(), cartItem.price!!))
    }

    // region {@link implement PowerBuyIncreaseOrDecreaseView.OnViewClickListener}
    override fun onClickQuantity(action: PowerBuyIncreaseOrDecreaseView.QuantityAction, qty: Int) {
        var resultQty = 0
        when (action) {
            ACTION_INCREASE -> {
                resultQty = qty + 1
            }
            ACTION_DECREASE -> {
                resultQty = qty - 1
            }
        }
        cartItem.id?.let { itemId -> cartItem.cartId?.let { listener?.onUpdateItem(it, itemId, resultQty) } }
    }
    // endregion

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

    fun hideDeleteItem(cartItem: CartItem) {
        deleteImageView.visibility = View.GONE
        qtyTextTitle.text = itemView.resources.getString(R.string.qty_title)
        productQty.visibility = View.GONE
        qtyText.text = cartItem.qty.toString()
    }
}
