package cenergy.central.com.pwb_store.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.OrderDetailAdapter
import cenergy.central.com.pwb_store.extensions.toPriceDisplay
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.OrderDetailView
import cenergy.central.com.pwb_store.model.TotalSegment
import cenergy.central.com.pwb_store.model.response.PaymentCartTotal
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_order_details.*


class OrderDetailDialog : BottomSheetDialogFragment() {
    private val orderDetailAdapter by lazy { OrderDetailAdapter() }

    private var paymentProtocol: PaymentProtocol? = null
    private var cartTotal: PaymentCartTotal? = null
    private var cacheItems: List<CacheCartItem> = listOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.paymentProtocol = context as PaymentProtocol
        this.cartTotal = paymentProtocol?.getCartTotal()
        this.cacheItems = paymentProtocol?.getCacheItems() ?: listOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        updateOrderDetail()
    }

    private fun updateOrderDetail() {
        cartTotal?.let {
            // setup product item
            val items = arrayListOf<OrderDetailView>()
            it.items?.mapTo(items, { cartItem ->
                val cacheItem = cacheItems.firstOrNull { c -> c.sku == cartItem.sku }
                OrderDetailView.OrderProduct(cartItem.name
                        ?: "", cacheItem?.imageUrl, (cartItem.price
                        ?: 0.0).toPriceDisplay(), cartItem.qty ?: 1)
            })

            // setup detail
            var totalPrice = it.totalPrice
            val coupon = it.totalSegment?.firstOrNull { it2 -> it2.code == TotalSegment.COUPON_KEY }
            var discountPrice = it.discountPrice
            var promotionDiscount = 0.0
            if (coupon != null) {
                val couponDiscount = TotalSegment.getCouponDiscount(coupon.value)
                promotionDiscount = couponDiscount?.couponAmount.toStringDiscount()
                discountPrice -= promotionDiscount
                totalPrice -= promotionDiscount
                totalPrice -= it.shippingAmount
            }
            val detailItem = OrderDetailView.OrderDetail(
                    orderTotal = it.totalPrice.toPriceDisplay(),
                    discount = it.discountPrice,
                    promotionCode = promotionDiscount,
                    shippingFee = if (it.shippingAmount == 0.0) getString(R.string.not_found_shipping_amount) else it.shippingAmount.toPriceDisplay(),
                    total = totalPrice.toPriceDisplay()
            )

            items.add(detailItem)
            orderDetailAdapter.items = items
        }
    }

    private fun initAdapter() {
        rvOrderItems?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderDetailAdapter
        }
    }
}