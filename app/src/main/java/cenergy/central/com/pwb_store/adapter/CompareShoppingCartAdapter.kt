package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.CompareShoppingCartButtonViewHolder
import cenergy.central.com.pwb_store.model.CompareList

class CompareShoppingCartAdapter(context: Context) : RecyclerView.Adapter<CompareShoppingCartButtonViewHolder>() {

    companion object {
        const val VIEW_TYPE_ID_PRODUCT = 1
    }

    lateinit var productCompareLists: CompareList

    private val mSpanSize = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (getItemViewType(position)) {
                VIEW_TYPE_ID_PRODUCT -> 1
                else -> 1
            }
        }
    }

    fun setCompareShoppingCart(productCompareLists: CompareList) {
        this.productCompareLists = productCompareLists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompareShoppingCartButtonViewHolder {
        return CompareShoppingCartButtonViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_compare_shopping_cart_button, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productCompareLists.compareProducts.size
    }

    override fun onBindViewHolder(holder: CompareShoppingCartButtonViewHolder, position: Int) {
        val shoppingCart = productCompareLists.compareProducts[position]
        holder.setProductCompare(shoppingCart)
    }

    fun getSpanSize(): GridLayoutManager.SpanSizeLookup {
        return mSpanSize
    }
}
