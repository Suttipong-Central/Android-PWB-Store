package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.is1HourProduct
import cenergy.central.com.pwb_store.extensions.isSpecialPrice
import cenergy.central.com.pwb_store.extensions.set1HourBadge
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
    private val badge1H: ImageView = itemView.findViewById(R.id.badge_2h)
    private val saleBadgeImage: ImageView = itemView.findViewById(R.id.ivSaleBadge)
    private val saleText: PowerBuyTextView = itemView.findViewById(R.id.tvDiscountPercentage)
    private val context = itemView.context

    fun setViewHolder(product: Product) {

        val unit = Contextor.getInstance().context.getString(R.string.baht)

        Glide.with(Contextor.getInstance().context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .crossFade()
                .fitCenter()
                .into(mImageView)

        oldPrice.text = product.getDisplayOldPrice(unit)
        newPrice.text = product.getDisplaySpecialPrice(unit)
        saleText.text = context.getString(R.string.format_product_sale, product.getDiscountPercentage())

        if (product.isSpecialPrice()) {
            showSpecialPrice()
            saleBadgeImage.visibility = View.VISIBLE
            saleText.visibility = View.VISIBLE
        } else {
            hideSpecialPrice()
            saleBadgeImage.visibility = View.INVISIBLE
            saleText.visibility = View.INVISIBLE
        }
        val brand = product.brand
        productBrand.text = if (brand != "") brand else "Brand"
        productName.text = product.name
        itemView.tag = product

        if (product.is1HourProduct()) {
            badge1H.set1HourBadge()
        } else {
            badge1H.setImageDrawable(null)
        }
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
