package cenergy.central.com.pwb_store.adapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import cenergy.central.com.pwb_store.extensions.isProductInStock
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus
import cenergy.central.com.pwb_store.model.CompareProduct
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_compare_product_item.view.*
import org.greenrobot.eventbus.EventBus

class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val layout = itemView.layout_card
    private val tvProductName = itemView.txt_product_description
    private val imgProduct = itemView.img_product
    private val btnCancel = itemView.img_cancel
    private val btnAddToCart = itemView.btnAddToCart

    fun bind(compareProduct: CompareProduct, listener: CompareItemListener) {
        Glide.with(itemView.context)
                .load(compareProduct.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .fitCenter()
                .into(imgProduct)
        tvProductName.text = compareProduct.name
        if (BuildConfig.FLAVOR != "pwbOmniTv") {
            btnAddToCart.isEnabled = compareProduct.isSalable
            btnAddToCart.background = ContextCompat.getDrawable(itemView.context,
                    if (compareProduct.isSalable) R.drawable.button_primary else R.drawable.button_unselected)
            btnAddToCart.setOnClickListener { listener.onClickAddToCart(compareProduct) }
        } else {
            btnAddToCart.visibility = View.GONE
        }
        layout.setOnClickListener { EventBus.getDefault().post(CompareDetailBus(compareProduct, true, layout)) }
        btnCancel.setOnClickListener { EventBus.getDefault().post(CompareDeleteBus(compareProduct, true)) }
    }
}