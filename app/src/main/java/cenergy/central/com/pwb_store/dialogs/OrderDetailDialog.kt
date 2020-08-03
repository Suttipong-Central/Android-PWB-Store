package cenergy.central.com.pwb_store.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.OrderDetailAdapter
import cenergy.central.com.pwb_store.extensions.toPriceDisplay
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.OrderDetailView
import cenergy.central.com.pwb_store.model.TotalSegment
import cenergy.central.com.pwb_store.realm.RealmController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_order_details.*


class OrderDetailDialog : BottomSheetDialogFragment() {
    private val orderDetailAdapter by lazy { OrderDetailAdapter() }

    private var paymentProtocol: PaymentProtocol? = null
    private var cacheItems: List<CacheCartItem> = listOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.paymentProtocol = context as PaymentProtocol
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
        cacheItems = RealmController.getInstance().cacheCartItems
        val cartTotal = paymentProtocol?.getCartTotal()
        cartTotal?.let {
            // setup product item
            val items = arrayListOf<OrderDetailView>()
            cacheItems.sortedBy { c -> c.itemId }.mapTo(items, { cacheItem ->
                val cartItem = it.items?.firstOrNull { cItem -> cacheItem.itemId == cItem.id }
                OrderDetailView.OrderProduct(cacheItem.name ?: "",
                        cacheItem.imageUrl, (cartItem?.price ?: 0.0).toPriceDisplay(), cacheItem.qty
                        ?: 1)
            })

            // setup detail
            var totalPrice = it.totalPrice
            var discountPrice = 0.0
            var promotionDiscount = 0.0
            val discount = it.totalSegment?.firstOrNull { it2 -> it2.code == TotalSegment.DISCOUNT_KEY }
            if (discount != null) {
                discountPrice = discount.value.toString().toStringDiscount()
            }
            val coupon = it.totalSegment?.firstOrNull { it2 -> it2.code == TotalSegment.COUPON_KEY }
            if (coupon != null) {
                val couponDiscount = TotalSegment.getCouponDiscount(coupon.value.toString())
                promotionDiscount = couponDiscount?.couponAmount.toStringDiscount()
                discountPrice -= promotionDiscount
                totalPrice -= promotionDiscount
            }
            if (discountPrice > 0) {
                totalPrice -= discountPrice
            }

            val detailItem = OrderDetailView.OrderDetail(
                    orderTotal = totalPrice,
                    discount = discountPrice,
                    promotionCode = promotionDiscount,
                    shippingFee = if (it.shippingAmount == 0.0) getString(R.string.not_found_shipping_amount) else it.shippingAmount.toPriceDisplay(),
                    total = (it.totalPrice - it.shippingAmount)
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
