package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class DisplayPromotionAdapter : RecyclerView.Adapter<DisplayPromotionViewHolder>() {
    var items: ArrayList<Product> = arrayListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayPromotionViewHolder {
        return DisplayPromotionViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_freebie, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DisplayPromotionViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class DisplayPromotionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val productImg = itemView.findViewById<ImageView>(R.id.imgProduct)
    private val productName = itemView.findViewById<PowerBuyTextView>(R.id.freebieNameTv)
    fun bind(product: Product) {
        productImg.setImageUrl(product.getImageUrl())
        productName.text = product.name
    }
}