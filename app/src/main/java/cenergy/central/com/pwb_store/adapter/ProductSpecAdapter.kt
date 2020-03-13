package cenergy.central.com.pwb_store.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.viewholder.SpecViewHolder
import cenergy.central.com.pwb_store.extensions.isEvent
import cenergy.central.com.pwb_store.extensions.isOdd
import cenergy.central.com.pwb_store.model.Specification

class ProductSpecAdapter(private val specItems :List<Specification> = arrayListOf()): RecyclerView.Adapter<SpecViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product_spec, parent, false)
        return SpecViewHolder(view)
    }

    override fun getItemCount(): Int = specItems.size

    override fun onBindViewHolder(holder: SpecViewHolder, position: Int) {
        holder.bind(specItems[position], position.isOdd())
    }
}