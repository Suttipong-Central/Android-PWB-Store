package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide

import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.isTwoHourProduct
import cenergy.central.com.pwb_store.extensions.set2HourBadge
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class ProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mImageView: ImageView = itemView.findViewById(R.id.img_product)
    private val productName: PowerBuyTextView = itemView.findViewById(R.id.txt_product_name)
    private val oldPrice: PowerBuyTextView = itemView.findViewById(R.id.txt_product_old_price)
    private val newPrice: PowerBuyTextView = itemView.findViewById(R.id.txt_product_new_price)
    private val productBrand: PowerBuyTextView = itemView.findViewById(R.id.txt_product_brand)
    private val badge2H: ImageView = itemView.findViewById(R.id.badge_2h)

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

        if (product.isSpecialPrice()) {
            showSpecialPrice()
        } else {
            hideSpecialPrice()
        }
        val brand = product.brand
        productBrand.text = if (brand != "") brand else "Brand"
        productName.text = product.name
        itemView.tag = product
        if(product.isTwoHourProduct()){
            badge2H.set2HourBadge()
        } else {
            badge2H.setImageDrawable(null)
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
