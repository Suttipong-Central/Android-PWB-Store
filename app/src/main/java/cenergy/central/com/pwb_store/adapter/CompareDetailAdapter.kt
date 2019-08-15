package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.CompareHeaderDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareItemDetailViewHolder
import cenergy.central.com.pwb_store.adapter.viewholder.CompareNotShowSpecViewHolder
import cenergy.central.com.pwb_store.model.IViewType
import cenergy.central.com.pwb_store.model.ViewType
import cenergy.central.com.pwb_store.model.response.CompareItem.Companion.COMPARE_ITEM_SHORT_DESCRIPTION_CODE
import java.util.*
import kotlin.Comparator

class CompareDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<IViewType>()
    val spanSize: GridLayoutManager.SpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (getItemViewType(position)) {
                VIEW_TYPE_ID_COMPARE_HEADER -> 4
                VIEW_TYPE_ID_COMPARE_ITEM -> 1
                VIEW_TYPE_ID_CANNOT_SHOW_SPEC -> 4
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
            else -> throw Exception("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.viewTypeId) {
            VIEW_TYPE_ID_COMPARE_HEADER -> if (item is CompareTitleItem && holder is CompareHeaderDetailViewHolder) {
                holder.setViewHolder(item)
            }
            VIEW_TYPE_ID_COMPARE_ITEM -> if (item is CompareItem && holder is CompareItemDetailViewHolder) {
                holder.setViewHolder(item)
            }
            VIEW_TYPE_ID_CANNOT_SHOW_SPEC -> if (holder is CompareNotShowSpecViewHolder) {
                holder.bindView()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewTypeId
    }

    fun setCompareDetail(compareItems: CompareProductAdapter.CompareItem) {
        if (compareItems.compareProducts.isEmpty()) {
            items.add(VIEW_TYPE_CANNOT_SHOW_SPEC)
        } else {
            compareItems.compareProducts.forEach { it ->
                // add header
                items.add(CompareTitleItem(it.label, VIEW_TYPE_ID_COMPARE_HEADER))

                it.items.sortWith(Comparator { o1, o2 ->
                    compareItems.order.indexOf(o1.sku).compareTo(compareItems.order.indexOf(o2.sku))
                })

                it.items.forEachIndexed { index, compareItem ->
                    // short description? html
                    items.add(CompareItem(compareItem.value,it.code == COMPARE_ITEM_SHORT_DESCRIPTION_CODE, VIEW_TYPE_ID_COMPARE_ITEM))
                }
            }
        }
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ID_COMPARE_HEADER = 0
        private const val VIEW_TYPE_ID_COMPARE_ITEM = 1
        private const val VIEW_TYPE_ID_CANNOT_SHOW_SPEC = 2

        private val VIEW_TYPE_CANNOT_SHOW_SPEC = ViewType(VIEW_TYPE_ID_CANNOT_SHOW_SPEC)
    }

    data class CompareTitleItem(val title: String = "", var mViewType: Int) : ViewType(mViewType)

    data class CompareItem(val detail: String = "", var isHTML: Boolean = false, var mViewType: Int) : ViewType(mViewType)
}
