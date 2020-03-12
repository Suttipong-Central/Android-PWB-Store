package cenergy.central.com.pwb_store.adapter.viewholder

import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.FreeItemAdapter
import cenergy.central.com.pwb_store.adapter.QtyAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.ShoppingCartListener
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.view.PowerBuyAutoCompleteTextStroke
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

class ShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
    private val freeBieImageRecycler: RecyclerView = itemView.findViewById(R.id.freeBieImageRecycler)
    private val freeBieDropdown: ImageView = itemView.findViewById(R.id.freeBieDropdown)
    private val freeBieTitle: TextView = itemView.findViewById(R.id.freeBieTitle)
    private val tvOverQty: TextView = itemView.findViewById(R.id.tvOverQty)

    // data
    private var listener: ShoppingCartListener? = null
    private var isShowing = true

    @SuppressLint("SetTextI18n")
    fun bindProductView(item: CacheCartItem, listener: ShoppingCartListener?) {
        itemView.context?.let { context ->
            qtyTextTitle.text = context.resources.getString(R.string.qty)
            deleteItemTextView.text = context.resources.getString(R.string.shopping_delete)
            val unit = context.getString(R.string.baht)
            this.listener = listener
            productName.text = item.name
            productCode.text = "${context.resources.getString(R.string.product_code)} ${item.sku}"
            productPrice.text = "${context.resources.getString(R.string.product_price)} ${getDisplayPrice(unit, item.price ?: 0.0)}"

            // get image from cache
            Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .crossFade()
                    .fitCenter()
                    .into(productImage)

            deleteItemTextView.visibility = View.VISIBLE
            deleteItemTextView.setOnClickListener {
                confirmDelete(item.itemId, item.sku, listener)
            }

            val max = min(item.qtyInStock ?: 1, item.maxQTY ?: 1)
            productQty.setText(item.qty.toString())
            val qtyAdepter = QtyAdapter(context, R.layout.layout_text_item, arrayListOf())
            qtyAdepter.setItems(max)
            productQty.setAdapter(qtyAdepter)
            productQty.setQtyFocusable(true)
            productQty.setIsQtyEdit(true)
            qtyAdepter.setCallback(object : QtyAdapter.QtyClickListener {
                override fun onQtyClickListener(qty: Int) {
                    productQty.clearAllFocus()
                    if (item.itemId != null && item.isOfflinePrice != null){
                        listener?.onUpdateItem(item.itemId!!, qty, item.isOfflinePrice!!)
                    }
                }
            })
            productQty.setOnEnterKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        productQty.clearAllFocus()
                        if (productQty.getText() != "" && productQty.getText().toInt() > 0) {
                            if (item.itemId != null && item.isOfflinePrice != null){
                                listener?.onUpdateItem(item.itemId!!, productQty.getText().toInt(), item.isOfflinePrice!!)
                            }
                        } else {
                            productQty.setText(item.qty.toString())
                            showAlertDialog(context.getString(R.string.empty_value))
                        }
                        return true
                    }
                    return false
                }
            })
            totalPrice.text = getDisplayPrice(unit, getToTalPrice(productQty.getText().toInt(), item.price ?: 0.0))

            if (!item.freeItems.isNullOrEmpty()) {
                freeBieDropdown.visibility = View.VISIBLE
                freeBieTitle.visibility = View.VISIBLE
                freeBieImageRecycler.visibility = View.VISIBLE
                val freeItemAdapter = FreeItemAdapter(context)
                freeBieImageRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                freeBieImageRecycler.adapter = freeItemAdapter
                freeItemAdapter.freeItems = item.freeItems ?: listOf()
                freeBieDropdown.setOnClickListener {
                    if (isShowing) {
                        isShowing = false
                        freeBieDropdown.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down))
                        freeBieImageRecycler.visibility = View.GONE
                    } else {
                        isShowing = true
                        freeBieDropdown.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up))
                        freeBieImageRecycler.visibility = View.VISIBLE
                    }
                }
            } else {
                freeBieDropdown.visibility = View.GONE
                freeBieTitle.visibility = View.GONE
                freeBieImageRecycler.visibility = View.GONE
            }
        }
    }

    private fun getToTalPrice(qty: Int, price: Double): Double {
        return qty * price
    }

    private fun getDisplayPrice(unit: String, price: Double): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(Locale.getDefault()).format(price))
    }

    private fun confirmDelete(itemId: Long?, itemSKU: String?, listener: ShoppingCartListener?) {
        itemView.context?.let { context ->
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
            builder.setMessage(context.getString(R.string.title_confirm_delete_item))
            builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                if (itemId != null && itemSKU != null) {
                    listener?.onDeleteItem(itemId, itemSKU)
                } else {
                    showAlertDialog(context.getString(R.string.some_thing_wrong))
                }
                dialog?.dismiss()
            }
            builder.setNegativeButton(context.getString(R.string.no)) { dialog, _ -> dialog?.dismiss() }
            builder.create().show()
        }
    }

    fun hideDeleteItem(item: CacheCartItem) {
        itemView.context?.let { context ->
            tvOverQty.visibility = View.GONE
            deleteItemTextView.visibility = View.GONE
            qtyTextTitle.text = context.resources.getString(R.string.qty_title)
            productQty.visibility = View.GONE
            qtyText.text = item.qty.toString()
        }
    }

    private fun showAlertDialog(message: String) {
        itemView.context?.let { context ->
            val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }
}
