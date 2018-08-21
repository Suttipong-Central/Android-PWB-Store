package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.OrderProductListViewHolder

class OrderProductListAdapter : RecyclerView.Adapter<OrderProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductListViewHolder {
        return OrderProductListViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_order_product, parent, false))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: OrderProductListViewHolder?, position: Int) {

    }
}
