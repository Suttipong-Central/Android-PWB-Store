package cenergy.central.com.pwb_store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareItemDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareNotShowSpecViewHolder
import cenergy.central.com.pwb_store.model.IViewType
import cenergy.central.com.pwb_store.model.ViewType
import cenergy.central.com.pwb_store.model.response.CompareItem.Companion.COMPARE_ITEM_SHORT_DESCRIPTION_CODE
import kotlinx.android.synthetic.main.item_compare_price.view.*
import kotlinx.android.synthetic.main.item_compare_rating.view.*
import java.util.*
import kotlin.Comparator

class CompareDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<IViewType>()
    private lateinit var compareItem: CompareProductAdapter.CompareItem
    val spanSize: GridLayoutManager.SpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (getItemViewType(position)) {
                VIEW_TYPE_ID_COMPARE_HEADER -> {
                    if (compareItem.products.isEmpty()){
                        4
                    } else {
                        compareItem.products.size
                    }
                }
                VIEW_TYPE_ID_COMPARE_ITEM -> 1
                VIEW_TYPE_ID_CANNOT_SHOW_SPEC -> {
                    if (compareItem.products.isEmpty()){
                        4
                    } else {
                        compareItem.products.size
                    }
                }
                else -> 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ID_COMPARE_HEADER -> CompareHeaderDetailViewHolder(
                    inflater.inflate(R.layout.list_item_text_header_compare_detail, parent, false)
            )
            VIEW_TYPE_ID_CANNOT_SHOW_SPEC -> CompareNotShowSpecViewHolder(
                    inflater.inflate(R.layout.list_item_cannot_show_spec_compare_detail, parent, false)
            )
            VIEW_TYPE_ID_COMPARE_ITEM -> CompareItemDetailViewHolder(
                    inflater.inflate(R.layout.list_item_text_compare_detail, parent, false)
            )
            VIEW_TYPE_ID_COMPARE_PRICE_ITEM -> ComparePriceItemViewHolder(
                    inflater.inflate(R.layout.item_compare_price, parent, false)
            )
            VIEW_TYPE_ID_COMPARE_RATING_ITEM -> CompareRatingItemViewHolder(
                    inflater.inflate(R.layout.item_compare_rating, parent, false)
            )
            else -> throw Exception("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.viewTypeId) {
            VIEW_TYPE_ID_COMPARE_HEADER -> if (item is CompareTitleItem && holder is CompareHeaderDetailViewHolder) {
                holder.bind(item)
            }
            VIEW_TYPE_ID_COMPARE_ITEM -> if (item is CompareItem && holder is CompareItemDetailViewHolder) {
                holder.bind(item)
            }
            VIEW_TYPE_ID_COMPARE_PRICE_ITEM -> if (item is CompareItem && holder is ComparePriceItemViewHolder) {
                holder.bind(item)
            }
            VIEW_TYPE_ID_COMPARE_RATING_ITEM -> if (item is CompareItem && holder is CompareRatingItemViewHolder) {
                holder.bind(item)
            }
            VIEW_TYPE_ID_CANNOT_SHOW_SPEC -> if (holder is CompareNotShowSpecViewHolder) {
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewTypeId
    }

    fun setCompareDetail(context: Context, compareItems: CompareProductAdapter.CompareItem) {
        this.compareItem = compareItems
        if (compareItems.compareProducts.isEmpty()) {
            items.add(VIEW_TYPE_CANNOT_SHOW_SPEC)
        } else {
            // setup sku order
            val skuOrder = arrayListOf<String>()
            compareItems.products.mapTo(skuOrder, { it.sku })

            if (BuildConfig.FLAVOR != "pwbOmniTV"){
                // add header price
                items.add(CompareTitleItem(context.getString(R.string.price), VIEW_TYPE_ID_COMPARE_HEADER))
                // add products price
                compareItems.products.forEach {
                    items.add(CompareItem(it.getProductPrice(), false, VIEW_TYPE_ID_COMPARE_PRICE_ITEM))
                }
            }

            // add header rating
            items.add(CompareTitleItem(context.getString(R.string.rating), VIEW_TYPE_ID_COMPARE_HEADER))
            // add products rating
            compareItems.products.forEach {
                items.add(CompareItem(it.rating.toString(), false, VIEW_TYPE_ID_COMPARE_RATING_ITEM))
            }

            compareItems.compareProducts.forEach {
                // add header
                items.add(CompareTitleItem(it.label, VIEW_TYPE_ID_COMPARE_HEADER))
                it.items.sortWith(Comparator { o1, o2 ->
                    skuOrder.indexOf(o1.sku).compareTo(skuOrder.indexOf(o2.sku))
                })

                it.items.forEachIndexed { _, compareItem ->
                    // short description? html
                    items.add(CompareItem(compareItem.value, it.code == COMPARE_ITEM_SHORT_DESCRIPTION_CODE,
                            VIEW_TYPE_ID_COMPARE_ITEM))
                }
            }
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ID_COMPARE_HEADER = 0
        private const val VIEW_TYPE_ID_COMPARE_ITEM = 1
        private const val VIEW_TYPE_ID_CANNOT_SHOW_SPEC = 2
        private const val VIEW_TYPE_ID_COMPARE_PRICE_ITEM = 3
        private const val VIEW_TYPE_ID_COMPARE_RATING_ITEM = 4

        private val VIEW_TYPE_CANNOT_SHOW_SPEC = ViewType(VIEW_TYPE_ID_CANNOT_SHOW_SPEC)
    }

    data class CompareTitleItem(val title: String = "", var mViewType: Int) : ViewType(mViewType)

    data class CompareItem(val detail: String = "", var isHTML: Boolean = false, var mViewType: Int) : ViewType(mViewType)

    inner class ComparePriceItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductPrice = itemView.tvProductPrice

        fun bind(item: CompareItem) {
            tvProductPrice.text = item.detail
        }
    }

    inner class CompareRatingItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratting = itemView.rating
        fun bind(item: CompareItem) {
            ratting.isEnabled = false
            ratting.setOnClickListener { /*do nothing*/ }
            ratting.rating = item.detail.toFloat()
        }
    }
}
