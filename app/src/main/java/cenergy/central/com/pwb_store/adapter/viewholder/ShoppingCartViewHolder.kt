package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView.QuantityAction.ACTION_DECREASE
import cenergy.central.com.pwb_store.view.PowerBuyIncreaseOrDecreaseView.QuantityAction.ACTION_INCREASE
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PowerBuyIncreaseOrDecreaseView.OnViewClickListener {

    // widget view
    private val productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_shopping_cart)
    private val productCode: PowerBuyTextView = itemView.findViewById(R.id.product_code_list_shopping_card)
    private val productPrice: PowerBuyTextView = itemView.findViewById(R.id.price_list_shopping_cart)
    private val productQty: PowerBuyIncreaseOrDecreaseView = itemView.findViewById(R.id.qty_list_shopping_cart)
    private val qtyText: PowerBuyTextView = itemView.findViewById(R.id.txt_qty_title_list_shopping_cart)
    private val qtyTextTitle: PowerBuyTextView = itemView.findViewById(R.id.qty_title_list_shopping_cart)
    private val totalPrice: PowerBuyTextView = itemView.findViewById(R.id.total_price_list_shopping_cart)
    private val deleteItemTextView: TextView = itemView.findViewById(R.id.deleteItemTextView)
    private val productImage: ImageView = itemView.findViewById(R.id.product_image_list_shopping_cart)
    private val tvOverQty: TextView = itemView.findViewById(R.id.tvOverQty)
    private val tvTitleFreebie: TextView = itemView.findViewById(R.id.tvTitleFreebie)

    // data
    private var listener: ShoppingCartAdapter.ShoppingCartListener? = null
    private lateinit var item: CartItem
    private var cacheCartItem: CacheCartItem? = null

    @SuppressLint("SetTextI18n")
    fun bindProductView(item: CartItem, listener: ShoppingCartAdapter.ShoppingCartListener?, cacheCartItem: CacheCartItem) {
        qtyTextTitle.text = itemView.resources.getString(R.string.qty)
        deleteItemTextView.text = itemView.resources.getString(R.string.shopping_delete)

        tvTitleFreebie.visibility = View.GONE // hide title free item
        val unit = itemView.context.getString(R.string.baht)
        this.listener = listener
        this.item = item
        this.cacheCartItem = cacheCartItem
        productName.text = item.name
        productCode.text = "${itemView.context.resources.getString(
                R.string.product_code)} ${item.sku}"
        productPrice.text = "${itemView.context.resources.getString(
                R.string.product_price)} ${getDisplayPrice(unit, item.price!!)}"

        // get image from cache
        Glide.with(itemView.context)
                .load(cacheCartItem.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(productImage)

        productQty.setOnClickQuantity(this, true)
        deleteItemTextView.visibility = View.VISIBLE
        deleteItemTextView.setOnClickListener {
            confirmDelete(item, listener)
        }

        val max = Math.min(cacheCartItem.qtyInStock ?: 1, cacheCartItem.maxQTY ?: 1)
        productQty.setMaximum(max)
        productQty.setQty(item.qty!!)
        checkOverAddQty(item.qty!!)
        totalPrice.text = getDisplayPrice(unit, getToTalPrice(productQty.getQty(), item.price!!))
    }


    // region freebie item
    @SuppressLint("SetTextI18n")
    fun bindFreebieView(item: CartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        qtyTextTitle.text = itemView.resources.getString(R.string.qty)
        deleteItemTextView.text = itemView.resources.getString(R.string.shopping_delete)
        tvTitleFreebie.visibility = View.VISIBLE // visible title free item

        val unit = itemView.context.getString(R.string.baht)
        this.listener = listener
        this.item = item
        productName.text = item.name
        productCode.text = "${itemView.context.resources.getString(R.string.product_code)} ${item.sku}"
        productPrice.text = "${itemView.context.resources.getString(
                R.string.product_price)} ${getDisplayPrice(unit, item.price!!)}"

        // hide for freebie
        productQty.setOnClickQuantity(this, false)
        deleteItemTextView.visibility = View.GONE
        productQty.setQty(item.qty!!)
        totalPrice.text = "FREE"
        totalPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.freeColor))
    }
    // endregion

    // region {@link implement PowerBuyIncreaseOrDecreaseView.OnViewClickListener}
    override fun onClickQuantity(action: PowerBuyIncreaseOrDecreaseView.QuantityAction, qty: Int) {
        val resultQty = when (action) {
            ACTION_INCREASE -> {
                qty + 1
            }
            ACTION_DECREASE -> {
                qty - 1
            }
        }

        checkOverAddQty(qty)

        item.id?.let { itemId -> item.cartId?.let { listener?.onUpdateItem(it, itemId, resultQty) } }
    }

    private fun checkOverAddQty(qty: Int) {
        // over qty?
        if (cacheCartItem != null) {
            val max = Math.min(cacheCartItem!!.qtyInStock ?: 1, cacheCartItem!!.maxQTY ?: 1)
            tvOverQty.visibility = if (qty >= max) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
    // endregion

    private fun getToTalPrice(qty: Int, price: Double): Double {
        return qty * price
    }

    private fun getDisplayPrice(unit: String, price: Double): String {
        val vat = price * 0.07
        val total = (price + vat).roundToInt()
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(total))
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
        tvOverQty.visibility = View.GONE
        deleteItemTextView.visibility = View.GONE
        qtyTextTitle.text = itemView.resources.getString(R.string.qty_title)
        productQty.visibility = View.GONE
        qtyText.text = cartItem.qty.toString()
    }

}
