package cenergy.central.com.pwb_store.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.extensions.setImageUrl
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
            qtyTextView.text = orderProduct.qty.toString()
        }
    }

    inner class OrderContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val totalTextView = itemView.tvTotal
        private val discountTextView = itemView.tvDiscount
        private val promotionTextView = itemView.tvPromotion
        private val shippingFeeTextView = itemView.tvShippingFee
        private val orderTotalTextView = itemView.tvOrderTotal

        fun bind(orderDetail: OrderDetailView.OrderDetail) {
            totalTextView.text = orderDetail.total
            discountTextView.text = orderDetail.discount.toString()
            promotionTextView.text = orderDetail.promotionCode.toString()
            shippingFeeTextView.text = orderDetail.shippingFee
            orderTotalTextView.text = orderDetail.orderTotal
        }
    }
}