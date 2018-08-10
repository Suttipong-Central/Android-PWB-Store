package cenergy.central.com.pwb_store.adapter.viewholder

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.CompareShoppingCartAdapter
import cenergy.central.com.pwb_store.adapter.decoration.SpacesItemDecoration

class CompareShoppingCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mRecyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view_compare_shopping_cart)!!

    private val shoppingCartList = arrayListOf("hi", "Hello")

    //Data Member
    private var mAdapter: CompareShoppingCartAdapter? = null
    private var mLayoutManager: GridLayoutManager? = null

    fun setViewHolder(context: Context) {
        mAdapter = CompareShoppingCartAdapter(context)
        //mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
        mLayoutManager?.reverseLayout = true// Show Start Right.
        mRecyclerView.setHasFixedSize(true)
        mAdapter?.setCompareShoppingCart(shoppingCartList)
        mLayoutManager?.spanSizeLookup = mAdapter?.getSpanSize()
        mRecyclerView.addItemDecoration(SpacesItemDecoration(0, LinearLayoutManager.VERTICAL))
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter
    }
}
