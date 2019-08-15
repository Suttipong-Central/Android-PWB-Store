package cenergy.central.com.pwb_store.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.ProductItemViewHolder
import cenergy.central.com.pwb_store.model.CompareProduct

class ProductItemAdapter(val products: List<CompareProduct>) : RecyclerView.Adapter<ProductItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_compare_product_item, parent, false)
        return ProductItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
       holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}