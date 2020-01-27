package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.ShoppingCartListener
import cenergy.central.com.pwb_store.adapter.viewholder.EmptyViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.HeaderCartItemViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.ShoppingCartViewHolder
import cenergy.central.com.pwb_store.model.CacheCartItem

class ShoppingCartAdapter(val listener: ShoppingCartListener?, private val isDescription: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val HEADER_VIEW_TYPE = 0
        const val ITEM_VIEW_TYPE = 1
        const val EMPTY_VIEW_TYPE = 2
    }

    var cartItem: List<Any> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                HeaderCartItemViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_header_item_shopping_cart, parent, false))
            }
            ITEM_VIEW_TYPE -> {
                ShoppingCartViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_new_shopping_cart, parent, false))
            }
            else -> {
                EmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_new_shopping_cart, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER_VIEW_TYPE -> {
                (holder as HeaderCartItemViewHolder).bindView(cartItem[position] as String)
            }
            ITEM_VIEW_TYPE -> {
                val itemViewHolder = holder as ShoppingCartViewHolder
                val item = cartItem[position] as CacheCartItem
                itemViewHolder.bindProductView(item, listener)
                if (isDescription) {
                    itemViewHolder.hideDeleteItem(item)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (cartItem[position]) {
            is String -> HEADER_VIEW_TYPE
            is CacheCartItem -> ITEM_VIEW_TYPE
            else -> EMPTY_VIEW_TYPE
        }
    }
}
