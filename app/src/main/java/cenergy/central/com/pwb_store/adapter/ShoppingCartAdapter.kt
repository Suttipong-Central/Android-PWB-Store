package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.ShoppingCartViewHolder
import cenergy.central.com.pwb_store.model.CartItem

class ShoppingCartAdapter(val listener: ShoppingCartListener?, private val isDescription: Boolean) : RecyclerView.Adapter<ShoppingCartViewHolder>() {

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
        holder.bindView(cartItemList[position], listener)
        if (isDescription) {
            holder.hideDeleteItem(cartItemList[position])
        }
    }

    interface ShoppingCartListener {
        fun onDeleteItem(cartId: String, itemId: Long)
        fun onUpdateItem(cartId: String, itemId: Long, qty: Int)
    }
}
