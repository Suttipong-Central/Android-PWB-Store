package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration
import kotlinx.android.synthetic.main.list_item_grid_compare_detail.view.*

class CompareDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val compareItemRecyclerView = itemView.recycler_view

    fun bind(item: CompareProductAdapter.CompareItem) {

        val compareDetailAdapter = CompareDetailAdapter()
        val layoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)

        layoutManager.spanSizeLookup = compareDetailAdapter.spanSize
        compareItemRecyclerView.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
        compareItemRecyclerView.layoutManager = layoutManager
        compareItemRecyclerView.adapter = compareDetailAdapter
        compareDetailAdapter.setCompareDetail(context, item)
        compareItemRecyclerView.isNestedScrollingEnabled = false
    }
}
