package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.model.Item
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class OrderProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var productImage: ImageView = itemView.findViewById(R.id.product_image_list_order)
    private var productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_order)
    private var productPrice: PowerBuyTextView = itemView.findViewById(R.id.product_price_list_order)
    private var productQty: PowerBuyTextView = itemView.findViewById(R.id.product_qty_list_order)
    private var productTotalPrice: PowerBuyTextView = itemView.findViewById(R.id.product_total_price_list_order)
    private var productTitleFreebie: PowerBuyTextView = itemView.findViewById(R.id.tvTitleFreebie)

    fun bindView(item: Item) {
        val unit = itemView.context.getString(R.string.baht)
        productImage.setImageUrl(item.imageUrl)
        productName.text = item.name
        productQty.text = item.qty.toString()
        if (!item.isFreebie){
            productTitleFreebie.visibility = View.GONE
            productPrice.text = getDisplayPrice(unit, item.basePriceIncludeTax.toString())
            productTotalPrice.text = getDisplayPrice(unit, item.baseTotalIncludeTax.toString())
        } else {
            productTitleFreebie.visibility = View.VISIBLE
            productTotalPrice.text = "FREE"
            productTotalPrice.setTextColor(ContextCompat.getColor(itemView.context, R.color.freeColor))
        }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }
}
