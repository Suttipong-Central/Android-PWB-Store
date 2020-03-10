package cenergy.central.com.pwb_store.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.BaseActivity
import cenergy.central.com.pwb_store.activity.ProductDetailActivity
import cenergy.central.com.pwb_store.adapter.viewholder.LoadingViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.ProductListViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.SearchResultViewHolder
import cenergy.central.com.pwb_store.model.IViewType
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ViewType
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.utils.Analytics
import java.util.*

/**
 * Created by napabhat on 7/6/2017 AD.
 */
class ProductListAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mListViewType: MutableList<IViewType> = ArrayList()
    val spanSize: SpanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (getItemViewType(position)) {
                VIEW_TYPE_ID_LOADING, VIEW_TYPE_ID_RESULT -> 3
                else -> 1
            }
        }
    }
    private var clicked = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_ID_GRID_VIEW -> return ProductListViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_grid_product, parent, false)
            )
            VIEW_TYPE_ID_LOADING -> return LoadingViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_loading, parent, false)
            )
            else -> return SearchResultViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.list_item_loading_result, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewTypeId = getItemViewType(position)
        val viewType = mListViewType[position]
        if (viewTypeId == VIEW_TYPE_ID_GRID_VIEW) {
            if (viewType is Product && holder is ProductListViewHolder) {
                holder.setViewHolder(viewType)
                holder.itemView.setOnClickListener {
                    if (clicked) {
                        clicked = false
                        val analytics = Analytics(mContext)
                        analytics.trackViewItem(viewType.sku)
                        val intent = Intent(mContext, ProductDetailActivity::class.java)
                        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_SKU, viewType.sku)
                        (mContext as Activity).startActivityForResult(intent, BaseActivity.REQUEST_UPDATE_LANGUAGE)
                        Handler().postDelayed({ clicked = true }, 1000)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mListViewType.size
    }

    override fun getItemViewType(position: Int): Int {
        return mListViewType[position].viewTypeId
    }

    fun setProduct(products: ArrayList<Product>) {
        mListViewType.clear()
        for (product in products) {
            product.attributeID = VIEW_TYPE_ID_GRID_VIEW
            mListViewType.add(product)
        }
        notifyDataSetChanged()
    }

    fun setError() {
        mListViewType.clear()
        notifyDataSetChanged()
        mListViewType.add(VIEW_TYPE_RESULT)
        notifyItemInserted(0)
        notifyItemRangeInserted(0, mListViewType.size)
    }

    companion object {
        private const val VIEW_TYPE_ID_GRID_VIEW = 0
        private const val VIEW_TYPE_ID_LOADING = 1
        private const val VIEW_TYPE_ID_RESULT = 2
        private val VIEW_TYPE_RESULT = ViewType(VIEW_TYPE_ID_RESULT)
    }

}