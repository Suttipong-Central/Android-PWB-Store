package cenergy.central.com.pwb_store.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.ShoppingCartViewHolder
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.response.PromotionResponse
import cenergy.central.com.pwb_store.model.response.ShoppingCartItem
import cenergy.central.com.pwb_store.realm.RealmController
import java.util.ArrayList

class ShoppingCartAdapter(val listener: ShoppingCartListener?, private val isDescription: Boolean) : RecyclerView.Adapter<ShoppingCartViewHolder>() {
    private val database = RealmController.getInstance()

    var promotions: ArrayList<PromotionResponse> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var shoppingCartItem = listOf<ShoppingCartItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        return ShoppingCartViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_shopping_cart, parent, false))
    }

    override fun getItemCount(): Int {
        return shoppingCartItem.size
    }

    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        val item = shoppingCartItem[position]
        val cacheCartItem = database.getCacheCartItem(item.id) // get cacheCartItem

        if (cacheCartItem != null) {
            val promotion = promotions.first { it.sku == cacheCartItem.sku }.extension
            holder.bindProductView(item, listener, cacheCartItem, promotion)
        } else {
            holder.bindFreebieView(item, listener)
        }
        if (isDescription) {
            holder.hideDeleteItem(item)
        }
    }

    interface ShoppingCartListener {
        fun onDeleteItem(itemId: Long, sku: String)
        fun onUpdateItem(itemId: Long, qty: Int, isChatAndShop: Boolean)
    }
}
