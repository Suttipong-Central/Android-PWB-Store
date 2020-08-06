package cenergy.central.com.pwb_store.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.ProductDetailActivity
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class FreebiePromotionAdapter : RecyclerView.Adapter<FreebiePromotionViewHolder>() {
    var items: ArrayList<Product> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreebiePromotionViewHolder {
        return FreebiePromotionViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_freebie, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FreebiePromotionViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class FreebiePromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val productImg = itemView.findViewById<ImageView>(R.id.imgProduct)
    private val productName = itemView.findViewById<PowerBuyTextView>(R.id.freebieNameTv)
    private val outOfStockTv = itemView.findViewById<PowerBuyTextView>(R.id.tvOutOfStock)
    private var clicked = true

    fun bind(product: Product) {
        val context = itemView.context
        productImg.setImageUrl(product.getImageUrl())
        productName.text = product.name
        if (product.extension?.stokeItem != null && product.extension!!.stokeItem!!.isSalable) {
            itemView.setOnClickListener {
                if (clicked) {
                    clicked = false
                    val analytics = Analytics(context)
                    analytics.trackViewItem(product.sku)
                    ProductDetailActivity.startActivity(context, product.sku,
                            product.getPricePerStore())
                    Handler().postDelayed({ clicked = true }, 1000)
                }
            }
            outOfStockTv.visibility = View.GONE
        } else {
            outOfStockTv.visibility = View.VISIBLE
            itemView.setOnClickListener(null)
        }
    }
}