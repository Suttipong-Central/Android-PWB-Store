package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import cenergy.central.com.pwb_store.adapter.viewholder.CompareDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareShoppingCartViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.ProductItemsViewHolder
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.IViewType
import cenergy.central.com.pwb_store.model.ViewType
import cenergy.central.com.pwb_store.model.response.CompareProductResponse


class CompareProductAdapter(mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listener: CompareItemListener = mContext as CompareItemListener
    private val items = ArrayList<IViewType>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ID_PRODUCT_LIST -> ProductItemsViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_horizontal_product_compare, parent, false)
            )
            VIEW_TYPE_ID_COMPARE_HEADER -> CompareHeaderViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_compare_product_header, parent, false)
            )
            VIEW_TYPE_ID_COMPARE_DETAIL -> CompareDetailViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_grid_compare_detail, parent, false)
            )
            VIEW_TYPE_ID_SHOPPING_CART -> CompareShoppingCartViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_compare_schopping_cart, parent, false)
            )
            else -> throw Exception("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.viewTypeId) {
            VIEW_TYPE_ID_PRODUCT_LIST -> if (item is ProductItem && holder is ProductItemsViewHolder) {
                holder.bind(item, listener)
            }

            VIEW_TYPE_ID_COMPARE_DETAIL -> if (item is CompareItem && holder is CompareDetailViewHolder) {
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewTypeId
    }

    fun updateCompareProducts(products: List<CompareProduct>, compare: List<CompareProductResponse>) {
        val compareList = ProductItem(products, VIEW_TYPE_ID_PRODUCT_LIST)
        val compareListProduct = CompareItem(compare, products, VIEW_TYPE_ID_COMPARE_DETAIL)
        items.clear()
        items.add(VIEW_TYPE_COMPARE_HEADER)
        items.add(compareList)
        items.add(compareListProduct)
        notifyDataSetChanged()
    }

    companion object {
        //Static Members
        private const val VIEW_TYPE_ID_PRODUCT_LIST = 0
        private const val VIEW_TYPE_ID_COMPARE_HEADER = 1
        private const val VIEW_TYPE_ID_COMPARE_DETAIL = 2
        private const val VIEW_TYPE_ID_SHOPPING_CART = 3

        private val VIEW_TYPE_COMPARE_HEADER = ViewType(VIEW_TYPE_ID_COMPARE_HEADER)
    }

    data class ProductItem(val products: List<CompareProduct>, var viewType: Int) : ViewType(viewType)

    data class CompareItem(val compareProducts: List<CompareProductResponse>, val products: List<CompareProduct>, var viewType: Int) : ViewType(viewType)
}
