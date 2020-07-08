package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
import cenergy.central.com.pwb_store.extensions.toDisplayPriceDisplay
import cenergy.central.com.pwb_store.extensions.toPriceDisplay
import cenergy.central.com.pwb_store.model.OrderDetailView
import kotlinx.android.synthetic.main.item_order_detail.view.*
import kotlinx.android.synthetic.main.item_order_product.view.*

class OrderDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items = arrayListOf<OrderDetailView>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ORDER_PRODUCT_ITEM -> {
                OrderProductViewHolder(inflater.inflate(R.layout.item_order_product, parent, false))
            }
            ORDER_CONTENT_ITEM -> {
                OrderContentViewHolder(inflater.inflate(R.layout.item_order_detail, parent, false))
            }
            else -> throw Exception("Not found view type: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is OrderProductViewHolder -> {
                holder.bind(item as OrderDetailView.OrderProduct)
            }
            is OrderContentViewHolder -> {
                holder.bind(item as OrderDetailView.OrderDetail)
            }
        }
    }

    companion object {
        const val ORDER_PRODUCT_ITEM = 1
        const val ORDER_CONTENT_ITEM = 2
    }

    inner class OrderProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImageView = itemView.ivProductImage
        private val productNameTextView = itemView.tvProductName
        private val productPriceTextView = itemView.tvProductPrice
        private val qtyTextView = itemView.tvProductQty

        fun bind(orderProduct: OrderDetailView.OrderProduct) {
            orderProduct.productImage?.let { productImageView.setImageUrl(it, R.drawable.ic_placeholder) }
            productNameTextView.text = orderProduct.productName
            productPriceTextView.text = orderProduct.productPrice
            qtyTextView.text = itemView.context.resources.getString(R.string.label_items, orderProduct.qty)
        }
    }

    inner class OrderContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val totalTextView = itemView.tvTotal
        private val discountTextView = itemView.tvDiscount
        private val promotionTextView = itemView.tvPromotion
        private val shippingFeeTextView = itemView.tvShippingFee
        private val orderTotalTextView = itemView.tvOrderTotal

        fun bind(orderDetail: OrderDetailView.OrderDetail) {
            totalTextView.text = orderDetail.total.toPriceDisplay()
            if (orderDetail.discount > 0.0) {
                discountTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.dangerColor))
                discountTextView.text = orderDetail.discount.toDisplayPriceDisplay()
            } else {
                discountTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blackText))
                discountTextView.text = orderDetail.discount.toPriceDisplay()
            }
            if (orderDetail.promotionCode > 0.0) {
                promotionTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.dangerColor))
                promotionTextView.text = orderDetail.promotionCode.toDisplayPriceDisplay()
            } else {
                promotionTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blackText))
                promotionTextView.text = orderDetail.promotionCode.toPriceDisplay()
            }
            shippingFeeTextView.text = orderDetail.shippingFee
            orderTotalTextView.text = orderDetail.orderTotal.toPriceDisplay()
        }
    }
}