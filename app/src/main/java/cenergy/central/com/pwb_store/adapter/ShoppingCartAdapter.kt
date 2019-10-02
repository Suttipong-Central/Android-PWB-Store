package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.ShoppingCartViewHolder
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.realm.RealmController

class ShoppingCartAdapter(val listener: ShoppingCartListener?, private val isDescription: Boolean) : RecyclerView.Adapter<ShoppingCartViewHolder>() {
    private val database = RealmController.getInstance()

    var cartItemList = listOf<CartItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        return ShoppingCartViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_shopping_cart, parent, false))
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        val item = cartItemList[position]
        val cacheCartItem = database.getCacheCartItem(item.id) // get cacheCartItem

        if (cacheCartItem != null) {
            holder.bindProductView(item, listener, cacheCartItem)
        } else {
            holder.bindFreebieView(item, listener)
        }
        if (isDescription) {
            holder.hideDeleteItem(item)
        }
    }

    interface ShoppingCartListener {
        fun onDeleteItem(itemId: Long)
        fun onUpdateItem(itemId: Long, qty: Int)
    }
}
