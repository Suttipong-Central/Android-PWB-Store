package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.model.response.Item
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class OrderProductListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    var productImage: ImageView = itemView.findViewById(R.id.product_image_list_order)
    var productName: PowerBuyTextView = itemView.findViewById(R.id.product_name_list_order)
    var productPrice: PowerBuyTextView = itemView.findViewById(R.id.product_price_list_order)
    var productQty: PowerBuyTextView = itemView.findViewById(R.id.product_qty_list_order)
    var productTotalPrice: PowerBuyTextView = itemView.findViewById(R.id.product_total_price_list_order)

    fun bindView(item: Item) {
        val unit = Contextor.getInstance().context.getString(R.string.baht)
        productImage.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_pwb_logo_detail))
        productName.text = item.name
        productPrice.text = getDisplayPrice(unit, item.basePriceIncludeTax.toString())
        productQty.text = item.qty.toString()
        productTotalPrice.text = getDisplayPrice(unit, item.baseTotalIncludeTax.toString())
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }
}
