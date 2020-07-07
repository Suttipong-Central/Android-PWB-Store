package cenergy.central.com.pwb_store.model

import cenergy.central.com.pwb_store.adapter.OrderDetailAdapter

sealed class OrderDetailView {
    abstract val viewType: Int

    data class OrderProduct(val productName: String,
                            val productImage: String? = null,
                            val productPrice: String,
                            val qty: Int,
                            override val viewType: Int = OrderDetailAdapter.ORDER_PRODUCT_ITEM) : OrderDetailView()

    data class OrderDetail(val total: String,
                           val discount: Double,
                           val promotionCode: Double,
                           val shippingFee: String,
                           val orderTotal: String,
                           override val viewType: Int = OrderDetailAdapter.ORDER_CONTENT_ITEM) : OrderDetailView()
}