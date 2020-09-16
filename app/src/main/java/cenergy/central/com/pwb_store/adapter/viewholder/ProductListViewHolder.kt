package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.isSpecialPrice
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import com.bumptech.glide.Glide

class ProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mImageView: ImageView = itemView.findViewById(R.id.img_product)
    private val productName: PowerBuyTextView = itemView.findViewById(R.id.txt_product_name)
    private val oldPrice: PowerBuyTextView = itemView.findViewById(R.id.txt_product_old_price)
    private val newPrice: PowerBuyTextView = itemView.findViewById(R.id.txt_product_new_price)
    private val productBrand: PowerBuyTextView = itemView.findViewById(R.id.txt_product_brand)
    private val available: AppCompatTextView = itemView.findViewById(R.id.tvAvailableHere)

    fun setViewHolder(product: Product) {

        val unit = Contextor.getInstance().context.getString(R.string.baht)

        Glide.with(Contextor.getInstance().context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .fitCenter()
                .into(mImageView)

        oldPrice.text = product.getDisplayOldPrice(unit)
        newPrice.text = product.getDisplaySpecialPrice(unit)

        val brand = product.brand
        productBrand.text = if (brand != "") brand else "Brand"
        productName.text = product.name
        itemView.tag = product
        if (product.isSpecialPrice()) {
            showSpecialPrice()
        } else {
            hideSpecialPrice()
        }
        available.text = itemView.context.getString(R.string.available_here_badge)
        available.visibility = if (product.availableThisStore) View.VISIBLE else View.GONE
    }

    private fun showSpecialPrice() {
        newPrice.visibility = View.VISIBLE
        oldPrice.setEnableStrikeThrough(true)
    }

    private fun hideSpecialPrice() {
        newPrice.visibility = View.GONE
        oldPrice.setEnableStrikeThrough(false)
    }
}
