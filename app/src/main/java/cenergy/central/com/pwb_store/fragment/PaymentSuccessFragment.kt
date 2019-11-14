package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.MainActivity
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.OrderProductListAdapter
import cenergy.central.com.pwb_store.dialogs.BarcodeDialogFragment
import cenergy.central.com.pwb_store.dialogs.ShareBottomSheetDialogFragment
import cenergy.central.com.pwb_store.dialogs.StaffHowToDialogFragment
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.CartTotalResponse
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.BarcodeUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.fragment_payment_success.*
import java.text.NumberFormat
import java.util.*

class PaymentSuccessFragment : Fragment(), ApiResponseCallback<OrderResponse> {
    // widget view
    private lateinit var recycler: RecyclerView
    private lateinit var orderNumber: PowerBuyTextView
    private lateinit var discountTitle: PowerBuyTextView
    private lateinit var discountPrice: PowerBuyTextView
    private lateinit var promotionTitle: PowerBuyTextView
    private lateinit var promotionPrice: PowerBuyTextView
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var orderDate: PowerBuyTextView
    private lateinit var name: PowerBuyTextView
    private lateinit var email: PowerBuyTextView
    private lateinit var contactNo: PowerBuyTextView
    private lateinit var branch: PowerBuyTextView
    private lateinit var address: PowerBuyTextView
    private lateinit var tel: PowerBuyTextView
    private lateinit var openToday: PowerBuyTextView
    private lateinit var tvShippingHeader: PowerBuyTextView
    private lateinit var tvShippingContact: PowerBuyTextView
    private lateinit var tvDeliveryType: PowerBuyTextView
    private lateinit var tvDeliveryAddress: PowerBuyTextView
    private lateinit var tvReceiverName: PowerBuyTextView
    private lateinit var tvBillingName: PowerBuyTextView
    private lateinit var tvBillingAddress: PowerBuyTextView
    private lateinit var tvBillingTelephone: PowerBuyTextView
    private lateinit var tvBillingCompany: PowerBuyTextView
    private lateinit var tvBillingTaxID: PowerBuyTextView
    private lateinit var tvDeliveryInfo: PowerBuyTextView
    private lateinit var tvAmount: TextView
    private lateinit var shippingTitle: TextView
    private lateinit var tvShippingAmount: TextView
    private lateinit var finishButton: PowerBuyIconButton
    private lateinit var storeAddressLayout: LinearLayout
    private lateinit var billingAddressLayout: LinearLayout
    private lateinit var deliveryLayout: LinearLayout
    private lateinit var staffIconLayout: LinearLayout
    private lateinit var customerNameLayout: LinearLayout
    private lateinit var deliveryInfoLayout: LinearLayout
    private lateinit var billingTelephoneLayout: LinearLayout
    private lateinit var shippingTelephoneLayout: LinearLayout
    private lateinit var discountLayout: Group
    private lateinit var couponLayout: Group
    private lateinit var shippingLayout: Group
    private var mProgressDialog: ProgressDialog? = null

    // data
    private val unit: String by lazy { resources.getString(R.string.baht) }
    private var orderId: String? = null
    private var cacheOrderId: String? = null
    private var orderProductListAdapter = OrderProductListAdapter()
    private var cacheCartItems: ArrayList<CacheCartItem>? = arrayListOf()
    private var database = RealmController.getInstance()
    private var cacheOrder: Order? = null
    private var urlRedirect: String = ""
    private lateinit var cartTotal: CartTotalResponse

    // get data activity
    private lateinit var paymentListener: PaymentProtocol
    private var deliveryType: DeliveryType? = null
    private var shippingInfo: AddressInformation? = null
    private var billingInfo: AddressInformation? = null
    private var branchAddress: Branch? = null
    private var theOneNumber: String = ""

    companion object {
        private const val TAG = "PaymentSuccessFragment"
        private const val ARG_ORDER_ID = "ARG_ORDER_ID"
        private const val ARG_CART_ITEMS = "ARG_CART_ITEMS"
        private const val ARG_CACHE_ORDER_ID = "ARG_CACHE_ORDER_ID"
        private const val ARG_URL_REDIRECT = "ARG_URL_REDIRECT"
        private const val SAME_BILLING = 1

        fun newInstance(orderId: String, cacheCartItems: ArrayList<CacheCartItem>, urlRedirect: String): PaymentSuccessFragment {
            val fragment = PaymentSuccessFragment()
            val args = Bundle()
            args.putString(ARG_ORDER_ID, orderId)
            args.putParcelableArrayList(ARG_CART_ITEMS, cacheCartItems)
            args.putString(ARG_URL_REDIRECT, urlRedirect)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceByHistory(orderId: String): PaymentSuccessFragment {
            val fragment = PaymentSuccessFragment()
            val args = Bundle()
            args.putString(ARG_CACHE_ORDER_ID, orderId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            paymentListener = context as PaymentProtocol
            deliveryType = paymentListener.getSelectedDeliveryType()
            shippingInfo = paymentListener.getShippingAddress()
            billingInfo = paymentListener.getBillingAddress()
            branchAddress = paymentListener.getSelectedBranch()
            cartTotal = paymentListener.getCartTotalResponse()
            theOneNumber = paymentListener.getT1CardNumber()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getString(ARG_ORDER_ID)
            cacheOrderId = it.getString(ARG_CACHE_ORDER_ID)
            cacheCartItems = it.getParcelableArrayList(ARG_CART_ITEMS)
            urlRedirect = it.getString(ARG_URL_REDIRECT, "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_success, container, false)
        setupView(rootView)
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(rootView: View) {
        showProgressDialog()
        recycler = rootView.findViewById(R.id.recycler_view_order_detail_list)
        orderNumber = rootView.findViewById(R.id.order_number_order_success)
        discountTitle = rootView.findViewById(R.id.discount_order_success)
        discountPrice = rootView.findViewById(R.id.txt_discount_order_success)
        promotionTitle = rootView.findViewById(R.id.promotion_order_success)
        promotionPrice = rootView.findViewById(R.id.txt_promotion_order_success)
        totalPrice = rootView.findViewById(R.id.txt_total_price_order_success)
        orderDate = rootView.findViewById(R.id.txt_order_date_order_success)
        tvShippingHeader = rootView.findViewById(R.id.shipping_header_order_success)
        tvDeliveryType = rootView.findViewById(R.id.txt_delivery_type_order_success)
        tvDeliveryAddress = rootView.findViewById(R.id.txt_delivery_address_order_success)
        tvShippingContact = rootView.findViewById(R.id.txt_shipping_contact_order_success)
        tvReceiverName = rootView.findViewById(R.id.txt_receiver_name_order_success)
        tvBillingName = rootView.findViewById(R.id.txt_billing_name_order_success)
        tvBillingAddress = rootView.findViewById(R.id.txt_billing_address_order_success)
        tvBillingTelephone = rootView.findViewById(R.id.txt_billing_tel_order_success)
        tvBillingCompany = rootView.findViewById(R.id.txt_billing_company_order_success)
        tvBillingTaxID = rootView.findViewById(R.id.txt_billing_vat_order_success)
        tvDeliveryInfo = rootView.findViewById(R.id.txt_delivery_option_order_success)
        shippingTitle = rootView.findViewById(R.id.delivery_price_order_success)
        tvShippingAmount = rootView.findViewById(R.id.txt_delivery_price_order_success)
        tvAmount = rootView.findViewById(R.id.txt_total_order_success)

        storeAddressLayout = rootView.findViewById(R.id.storeAddressLayout)
        billingAddressLayout = rootView.findViewById(R.id.billingAddressLayout)
        customerNameLayout = rootView.findViewById(R.id.customerNameLayout)
        deliveryInfoLayout = rootView.findViewById(R.id.deliveryInfoLayout)
        billingTelephoneLayout = rootView.findViewById(R.id.billingTelephoneLayout)
        shippingTelephoneLayout = rootView.findViewById(R.id.shippingTelephoneLayout)
        deliveryLayout = rootView.findViewById(R.id.deliveryLayout)
        staffIconLayout = rootView.findViewById(R.id.staffIconLayout)

        // customer
        name = rootView.findViewById(R.id.txt_name_order_success)
        email = rootView.findViewById(R.id.txt_email_order_success)
        contactNo = rootView.findViewById(R.id.txt_contact_no_order_success)
        // staff
        branch = rootView.findViewById(R.id.txt_branch_order_success)
        address = rootView.findViewById(R.id.txt_address_order_success)
        tel = rootView.findViewById(R.id.txt_tell_order_success)
        openToday = rootView.findViewById(R.id.txt_open_today_order_success)
        finishButton = rootView.findViewById(R.id.buttonFinished)
        discountLayout = rootView.findViewById(R.id.layoutDiscount)
        couponLayout = rootView.findViewById(R.id.layoutCouponDiscount)
        shippingLayout = rootView.findViewById(R.id.layoutDeliveryDiscount)

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = orderProductListAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (orderId != null) {
            retrieveOrder()
        }

        if (cacheOrderId != null) {
            getOrderFromLocalDatabase(cacheOrderId!!)
            finishButton.visibility = View.GONE
        }

        staffIconLayout.setOnClickListener {
            StaffHowToDialogFragment.newInstance().show(fragmentManager, "dialog")
        }
    }

    fun retrieveOrder() {
        if (mProgressDialog != null && !mProgressDialog!!.isShowing) {
            showProgressDialog()
        }
        context?.let { HttpManagerMagento.getInstance(it).getOrder(orderId!!, this) }
    }

    private fun getOrderFromLocalDatabase(orderId: String) {
        cacheOrder = database.getOrder(orderId)
        cacheOrder?.let { cacheOrder -> updateViewOrder(cacheOrder) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewOrder(order: Order) {
        val shippingAddress = order.shippingAddress
                ?: throw IllegalArgumentException("Shipping address must not be null")
        val billingAddress = order.billingAddress
                ?: throw IllegalArgumentException("Billing address must not be null")

        updateLabel()
        setupBarcodeView(order)

        orderProductListAdapter.listItems = order.items
        //Setup order number
        orderNumber.text = "${getString(R.string.order_number)} ${order.orderId}"

        // Setup the 1 number
        val t1cNumber = order.t1cEarnCardNumber
        tvTheOneNumber.text = if (t1cNumber.isEmpty()) "-" else t1cNumber

        //Setup customer
        orderDate.text = context?.let { order.getDisplayTimeCreated(it) }
        email.text = shippingAddress.email

        // setup billing
        tvBillingName.text = billingAddress.getDisplayName()
        tvBillingAddress.text = getAddress(billingAddress)
        tvBillingTelephone.text = if (billingAddress.telephone.isBlank()) { "-" } else billingAddress.telephone
        tvBillingCompany.text = if (billingAddress.company.isBlank()) { "-" } else billingAddress.company
        tvBillingTaxID.text = if (billingAddress.vatId.isBlank()) { "-" } else billingAddress.vatId

        // setup shipping address or pickup at store
        when (DeliveryType.fromString(order.shippingType)) {
            DeliveryType.STORE_PICK_UP,
            DeliveryType.STORE_PICK_UP_ISPU -> setupShippingStorePickupView(order.branchShipping, order.billingAddress)
            else ->  setupShipping(order, shippingAddress)
        }

        // setup total or summary
        var discount = order.discountPrice.toStringDiscount()
        val couponDiscount = order.coupon?.discountAmount?: 0.0
        if (couponDiscount > 0){
            discount -= couponDiscount
            couponLayout.visibility = View.VISIBLE
            promotionPrice.text = getDisplayDiscount(unit, couponDiscount.toString())
        } else {
            couponLayout.visibility = View.GONE
        }
        discountLayout.visibility = if (discount > 0) View.VISIBLE else View.GONE
        discountPrice.text = getDisplayDiscount(unit, discount.toString())
        shippingLayout.visibility = if (order.shippingAmount > 0) View.VISIBLE else View.GONE
        tvShippingAmount.text = getDisplayPrice(unit, order.shippingAmount.toString())
        totalPrice.text = getDisplayPrice(unit, order.baseTotal.toString())
        tvAmount.text = getDisplayPrice(unit, order.total.toString())
        finishButton.setOnClickListener {
            finishThisPage()
        }
        mProgressDialog?.dismiss()
    }

    private fun setupBarcodeView(order: Order) {
        if (order.paymentRedirect.isNotBlank()) {
            tvPaymentDescription.visibility = View.VISIBLE
            ivPaymentBarcode.visibility = View.VISIBLE
            shareButton.visibility = View.VISIBLE
            layoutHowToDoNext.visibility = View.GONE
            val bitmapBarcode = BarcodeUtils.createQRCode(order.paymentRedirect)
            ivPaymentBarcode.setImageBitmap(bitmapBarcode)
            ivPaymentBarcode.setOnClickListener {
                BarcodeDialogFragment.newInstance(order.paymentRedirect).show(fragmentManager, "dialog")
            }
            shareButton.setOnClickListener {
                val shareBottomSheetFragment = ShareBottomSheetDialogFragment.newInstance(order.paymentRedirect)
                shareBottomSheetFragment.show(childFragmentManager, ShareBottomSheetDialogFragment.TAG)
            }
        } else {
            layoutHowToDoNext.visibility = View.VISIBLE
            tvPaymentDescription.visibility = View.GONE
            ivPaymentBarcode.visibility = View.GONE
            shareButton.visibility = View.GONE
        }
    }

    private fun setupShipping(order: Order, shippingAddress: AddressInformation) {
        deliveryLayout.visibility = View.VISIBLE
        deliveryInfoLayout.visibility = View.VISIBLE
        storeAddressLayout.visibility = View.GONE
        customerNameLayout.visibility = View.VISIBLE

        name.text = shippingAddress.getDisplayName()
        contactNo.text = shippingAddress.telephone

        tvShippingHeader.text = getString(R.string.delivery_detail)
        tvDeliveryType.text = when (DeliveryType.fromString(order.shippingType)) {
            DeliveryType.EXPRESS -> getString(R.string.express)
            DeliveryType.STANDARD -> getString(R.string.standard)
            DeliveryType.STORE_PICK_UP -> getString(R.string.collect)
            DeliveryType.HOME -> getString(R.string.home_delivery)
            else -> ""
        }

        tvReceiverName.text = shippingAddress.getDisplayName()
        tvDeliveryAddress.text = getAddress(shippingAddress)
        tvShippingContact.text = shippingAddress.telephone
        tvDeliveryInfo.text = when (DeliveryType.fromString(order.shippingType)) {
            DeliveryType.EXPRESS -> getString(R.string.express_delivery_desc)
            DeliveryType.STANDARD -> getString(R.string.standard_delivery_desc)
            DeliveryType.HOME -> getString(R.string.home_delivery_desc)
            else -> ""
        }
    }

    private fun setupShippingStorePickupView(branchShipping: Branch?, billingAddress: AddressInformation?) {
        deliveryLayout.visibility = View.GONE
        deliveryInfoLayout.visibility = View.GONE
        storeAddressLayout.visibility = View.VISIBLE
        customerNameLayout.visibility = View.VISIBLE
        tvShippingHeader.text = getString(R.string.store_collection_detail)

        name.text = billingAddress?.getDisplayName()?: "-"
        contactNo.text = billingAddress?.telephone?: "-"

        if (branchShipping != null) {
            branch.text = if (branchShipping.storeName.isBlank()) { "-" } else branchShipping.storeName
            address.text = branchShipping.getBranchAddress()
            tel.text = if (branchShipping.phone.isBlank()) { "-" } else branchShipping.phone
            openToday.text = if (branchShipping.description.isBlank()) { "-" } else branchShipping.description
        }
    }

    private fun updateLabel() {
        // setup label
        view?.let { rootView ->
            rootView.findViewById<PowerBuyTextView>(R.id.header_order_success).text = getString(R.string.title_order_success)
            rootView.findViewById<PowerBuyTextView>(R.id.next_step_order_success).text = getString(R.string.next_step)
            rootView.findViewById<PowerBuyTextView>(R.id.staff_description_order_success).text = getString(R.string.staff_descriptions)
            rootView.findViewById<PowerBuyTextView>(R.id.for_staff_order_success).text = getString(R.string.for_staff)
            rootView.findViewById<PowerBuyTextView>(R.id.tvPaymentDescription).text = getString(R.string.payment_description)
            rootView.findViewById<PowerBuyTextView>(R.id.order_info_sub_header_order_success).text = getString(R.string.personal_detail)
            rootView.findViewById<PowerBuyTextView>(R.id.order_date_order_success).text = getString(R.string.order_date)
            rootView.findViewById<PowerBuyTextView>(R.id.name_order_success).text = getString(R.string.customer_name)
            rootView.findViewById<PowerBuyTextView>(R.id.email_order_success).text = getString(R.string.email)
            rootView.findViewById<PowerBuyTextView>(R.id.contact_no_order_success).text = getString(R.string.tell)
            rootView.findViewById<PowerBuyTextView>(R.id.delivery_option_order_success).text = getString(R.string.label_delivery_info)
            rootView.findViewById<PowerBuyTextView>(R.id.branch_order_success).text = getString(R.string.branch)
            rootView.findViewById<PowerBuyTextView>(R.id.address_order_success).text = getString(R.string.address)
            rootView.findViewById<PowerBuyTextView>(R.id.tell_order_success).text = getString(R.string.phone)
            rootView.findViewById<PowerBuyTextView>(R.id.shipping_contact_order_success).text = getString(R.string.tell)
            rootView.findViewById<PowerBuyTextView>(R.id.open_today_order_success).text = getString(R.string.open_today)
            rootView.findViewById<PowerBuyTextView>(R.id.header_billing_address_order_success).text = getString(R.string.label_billing_address)
            rootView.findViewById<PowerBuyTextView>(R.id.billing_name_order_success).text = getString(R.string.receiver_name)
            rootView.findViewById<PowerBuyTextView>(R.id.billing_address_order_success).text = getString(R.string.address)
            rootView.findViewById<PowerBuyTextView>(R.id.billing_tel_order_success).text = getString(R.string.tell)
            rootView.findViewById<PowerBuyTextView>(R.id.billing_company_order_success).text = getString(R.string.company)
            rootView.findViewById<PowerBuyTextView>(R.id.billing_vat_order_success).text = getString(R.string.tax_id_success)
            rootView.findViewById<PowerBuyTextView>(R.id.delivery_type_order_success).text = getString(R.string.label_shipping_type)
            rootView.findViewById<PowerBuyTextView>(R.id.receiver_name_order_success).text = getString(R.string.receiver_name)
            rootView.findViewById<PowerBuyTextView>(R.id.delivery_address_order_success).text = getString(R.string.address)
            rootView.findViewById<PowerBuyTextView>(R.id.order_detail_order_success).text = getString(R.string.order_detail)
            rootView.findViewById<PowerBuyTextView>(R.id.product_recycler_order_success).text = getString(R.string.product)
            rootView.findViewById<PowerBuyTextView>(R.id.price_recycler_order_success).text = getString(R.string.price)
            rootView.findViewById<PowerBuyTextView>(R.id.qty_recycler_order_success).text = getString(R.string.qty)
            rootView.findViewById<PowerBuyTextView>(R.id.total_recycler_order_success).text = getString(R.string.total)
            rootView.findViewById<PowerBuyTextView>(R.id.total_order_success).text = getString(R.string.total)
            rootView.findViewById<PowerBuyTextView>(R.id.txt_delivery_price_order_success).text = getString(R.string.delivery_price)
            rootView.findViewById<PowerBuyTextView>(R.id.total_result_order_success).text = getString(R.string.total_price)
            finishButton.setText(getString(R.string.finished))
        }
        // end set label
    }

    private fun finishThisPage() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context?.let {
            ActivityCompat.startActivity(it, intent, ActivityOptionsCompat
                    .makeScaleUpAnimation(finishButton, 0, 0, finishButton.width, finishButton.height)
                    .toBundle())
        }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun getDisplayDiscount(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "-%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun showAlertDialog(title: String, message: String) { //TODO: Refactor show alert dialog.
        val builder = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    // {@like implement ApiResponseCallback<OrderResponse>}
    override fun success(response: OrderResponse?) {
        if (response != null) {

            response.billingAddress = billingInfo ?: shippingInfo

            if (response.shippingType != DeliveryType.STORE_PICK_UP.methodCode && response.shippingType != DeliveryType.STORE_PICK_UP_ISPU.methodCode) {
                response.orderExtension?.shippingAssignments?.get(0)?.shipping?.shippingAddress = shippingInfo
            }

            // add shipping type
            response.shippingType = deliveryType?.methodCode
                    ?: DeliveryType.STORE_PICK_UP.methodCode

            response.items?.forEach { item ->
                val isCacheItem = cacheCartItems?.firstOrNull { it.sku == item.sku }
                if (isCacheItem != null) {
                    item.imageUrl = isCacheItem.imageUrl
                } else {
                    item.isFreebie = true
                }
            }
            // save order to local database
            val order = Order.asOrder(orderResponse = response, branchShipping = branchAddress,
                    paymentRedirect = urlRedirect, theOneNumber = theOneNumber)

            database.saveOrder(order, object : DatabaseListener{
                override fun onSuccessfully() {
                    updateViewOrder(order)
                }

                override fun onFailure(error: Throwable) {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", error.message?: resources.getString(R.string.some_thing_wrong))
                }
            })
        } else {
            mProgressDialog?.dismiss()
            showAlertDialog("", resources.getString(R.string.some_thing_wrong))
        }
    }

    override fun failure(error: APIError) {
        mProgressDialog?.dismiss()
        showAlertDialog("", error.errorMessage?: resources.getString(R.string.some_thing_wrong))
    }
    //endregion

    private fun getAddress(address: AddressInformation?): String {
        if (address == null) {
            return ""
        }
        var text = ""
        val subAddress = address.subAddress
        if (subAddress != null) {
            if (!subAddress.houseNumber.isNullOrEmpty()) {
                text += subAddress.houseNumber + ", "
            }
            if (!subAddress.soi.isNullOrEmpty()) {
                text += subAddress.soi + ", "
            }
            if (!subAddress.building.isNullOrEmpty()) {
                text += subAddress.building + ", "
            }
            if (!subAddress.addressLine.isNullOrEmpty()) {
                text += subAddress.addressLine + ", "
            }
            if (!subAddress.subDistrict.isNullOrEmpty()) {
                text += subAddress.subDistrict + ", "
            }
            if (!subAddress.district.isNullOrEmpty()) {
                text += subAddress.district + ", "
            }
        }
        text += address.region + ", "
        text += address.postcode
        return text
    }
}