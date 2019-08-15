package cenergy.central.com.pwb_store.adapter.viewholder

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.adapter.ProductItemAdapter
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import kotlinx.android.synthetic.main.list_item_horizontal_product_compare.view.*

class ProductItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context
    private val productRecyclerView = itemView.recycler_view

    fun bind(item: CompareProductAdapter.ProductItem, listener: CompareItemListener) {
        val productAdapter = ProductItemAdapter(item.products, listener)
        val mLayoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
        productRecyclerView.layoutManager = mLayoutManager
        productRecyclerView.adapter = productAdapter
        productRecyclerView.setHasFixedSize(true)
    }
}
