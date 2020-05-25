package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.adapter.CompareDetailAdapter
import cenergy.central.com.pwb_store.adapter.CompareProductAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration
import kotlinx.android.synthetic.main.list_item_grid_compare_detail.view.*

class CompareDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context
    private val compareItemRecyclerView = itemView.recycler_view

    fun bind(item: CompareProductAdapter.CompareItem) {

        val compareDetailAdapter = CompareDetailAdapter()
        val layoutManager = if (item.products.isEmpty()){
            GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
        } else {
            GridLayoutManager(context, item.products.size, LinearLayoutManager.VERTICAL, false)
        }

        layoutManager.spanSizeLookup = compareDetailAdapter.spanSize
        compareItemRecyclerView.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
        compareItemRecyclerView.layoutManager = layoutManager
        compareItemRecyclerView.adapter = compareDetailAdapter
        compareDetailAdapter.setCompareDetail(context, item)
        compareItemRecyclerView.isNestedScrollingEnabled = false
    }
}
