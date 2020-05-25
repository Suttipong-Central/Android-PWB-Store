package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import cenergy.central.com.pwb_store.adapter.viewholder.ProductItemViewHolder
import cenergy.central.com.pwb_store.model.CompareProduct

class ProductItemAdapter(val products: List<CompareProduct>,val listener: CompareItemListener) : RecyclerView.Adapter<ProductItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_compare_product_item, parent, false)
        return ProductItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
       holder.bind(products[position], listener)
    }

    override fun getItemCount(): Int = products.size
}
