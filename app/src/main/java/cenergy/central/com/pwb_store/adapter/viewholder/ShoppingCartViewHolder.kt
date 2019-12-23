package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.QtyAdapter
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.response.ShoppingCartItem
import cenergy.central.com.pwb_store.view.PowerBuyAutoCompleteTextStroke
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    // widget view
    private val productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_shopping_cart)
    private val productCode: PowerBuyTextView = itemView.findViewById(R.id.product_code_list_shopping_card)
    private val productPrice: PowerBuyTextView = itemView.findViewById(R.id.price_list_shopping_cart)
    private val productQty: PowerBuyAutoCompleteTextStroke = itemView.findViewById(R.id.qty_list_shopping_cart)
    private val qtyText: PowerBuyTextView = itemView.findViewById(R.id.txt_qty_title_list_shopping_cart)
    private val qtyTextTitle: PowerBuyTextView = itemView.findViewById(R.id.qty_title_list_shopping_cart)
    private val totalPrice: PowerBuyTextView = itemView.findViewById(R.id.total_price_list_shopping_cart)
    private val deleteItemTextView: TextView = itemView.findViewById(R.id.deleteItemTextView)
    private val productImage: ImageView = itemView.findViewById(R.id.product_image_list_shopping_cart)
    private val tvOverQty: TextView = itemView.findViewById(R.id.tvOverQty)
    private val tvTitleFreebie: TextView = itemView.findViewById(R.id.tvTitleFreebie)

    // data
    private var listener: ShoppingCartAdapter.ShoppingCartListener? = null
    private lateinit var item: ShoppingCartItem
    private var cacheCartItem: CacheCartItem? = null

    @SuppressLint("SetTextI18n")
    fun bindProductView(item: ShoppingCartItem, listener: ShoppingCartAdapter.ShoppingCartListener?, cacheCartItem: CacheCartItem) {
        qtyTextTitle.text = itemView.resources.getString(R.string.qty)
        deleteItemTextView.text = itemView.resources.getString(R.string.shopping_delete)

        tvTitleFreebie.visibility = View.GONE // hide title free item
        val unit = itemView.context.getString(R.string.baht)
        this.listener = listener
        this.item = item
        this.cacheCartItem = cacheCartItem
        productName.text = cacheCartItem.name
        productCode.text = "${itemView.context.resources.getString(
                R.string.product_code)} ${item.sku}"
        productPrice.text = "${itemView.context.resources.getString(
                R.string.product_price)} ${getDisplayPrice(unit, item.price?: 0.0)}"

        // get image from cache
        Glide.with(itemView.context)
                .load(cacheCartItem.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(productImage)

        deleteItemTextView.visibility = View.VISIBLE
        deleteItemTextView.setOnClickListener {
            confirmDelete(item, listener)
        }

        val max = min(cacheCartItem.qtyInStock ?: 1, cacheCartItem.maxQTY ?: 1)
        productQty.setText(item.qty.toString())
        val qtyAdepter = QtyAdapter(itemView.context, R.layout.layout_text_item, arrayListOf())
        qtyAdepter.setItems(max)
        productQty.setAdapter(qtyAdepter)
        productQty.setQtyFocusable(true)
        productQty.setIsQtyEdit(true)
        qtyAdepter.setCallback(object : QtyAdapter.QtyClickListener{
            override fun onQtyClickListener(qty: Int) {
                productQty.clearAllFocus()
                item.id?.let { itemId -> listener?.onUpdateItem(itemId, qty) }
            }
        })
        productQty.setOnEnterKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    productQty.clearAllFocus()
                    if (productQty.getText() != "" && productQty.getText().toInt() > 0) {
                        item.id?.let { itemId -> listener?.onUpdateItem(itemId, productQty.getText().toInt()) }
                    } else {
                        productQty.setText(item.qty.toString())
                        showAlertDialog(itemView.context.getString(R.string.empty_value))
                    }
                    return true
                }
                return false
            }
        })
        totalPrice.text = getDisplayPrice(unit, getToTalPrice(productQty.getText().toInt(), item.price?: 0.0))
    }


    // region freebie item
    @SuppressLint("SetTextI18n")
    fun bindFreebieView(item: ShoppingCartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        qtyTextTitle.text = itemView.resources.getString(R.string.qty)
        deleteItemTextView.text = itemView.resources.getString(R.string.shopping_delete)
        tvTitleFreebie.visibility = View.VISIBLE // visible title free item

        val unit = itemView.context.getString(R.string.baht)
        this.listener = listener
        this.item = item
        productName.text = item.name
        productCode.text = "${itemView.context.resources.getString(R.string.product_code)} ${item.sku}"
        productPrice.text = "${itemView.context.resources.getString(R.string.product_price)} ${getDisplayPrice(unit, item.price?: 0.0)}"

        // hide for freebie
        deleteItemTextView.visibility = View.GONE
        productQty.setText(item.qty.toString())
        totalPrice.text = "FREE"
        totalPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.freeColor))
    }
    // endregion

    private fun getToTalPrice(qty: Int, price: Double): Double {
        return qty * price
    }

    private fun getDisplayPrice(unit: String, price: Double): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(price))
    }

    private fun confirmDelete(shoppingCartItem: ShoppingCartItem, listener: ShoppingCartAdapter.ShoppingCartListener?) {
        val context = itemView.context
        val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialogTheme)
        builder.setMessage(context.getString(R.string.title_confirm_delete_item))
        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
            if (shoppingCartItem.id != null && shoppingCartItem.sku != null){
                listener?.onDeleteItem(shoppingCartItem.id!!, shoppingCartItem.sku!!)
            } else {
                showAlertDialog(context.getString(R.string.some_thing_wrong))
            }
            dialog?.dismiss()
        }

        builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ -> dialog?.dismiss() }
        builder.create().show()
    }

    fun hideDeleteItem(shoppingCartItem: ShoppingCartItem) {
        tvOverQty.visibility = View.GONE
        deleteItemTextView.visibility = View.GONE
        qtyTextTitle.text = itemView.resources.getString(R.string.qty_title)
        productQty.visibility = View.GONE
        qtyText.text = shoppingCartItem.qty.toString()
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(itemView.context, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
        builder.show()
    }
}
