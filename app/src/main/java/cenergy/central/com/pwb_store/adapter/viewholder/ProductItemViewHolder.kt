package cenergy.central.com.pwb_store.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus
import cenergy.central.com.pwb_store.model.CompareProduct
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_compare_product_item.view.*
import org.greenrobot.eventbus.EventBus


class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvProductName = itemView.txt_product_description
    private val imgProduct = itemView.img_product
    private val btnCancel = itemView.img_cancel
    private val btnAddToCart = itemView.btnAddToCart


    fun bind(compareProduct: CompareProduct) {
        Glide.with(itemView.context)
                .load(compareProduct.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .fitCenter()
                .into(imgProduct)
        tvProductName.text = compareProduct.name
        btnCancel.setOnClickListener { EventBus.getDefault().post(CompareDeleteBus(compareProduct, true)) }
        btnAddToCart.setOnClickListener {
            //TODO: Add to cart }
        }
    }
}
