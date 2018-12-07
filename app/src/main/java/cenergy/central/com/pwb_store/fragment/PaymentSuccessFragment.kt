package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
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
import cenergy.central.com.pwb_store.dialogs.StaffHowToDialogFragment
import cenergy.central.com.pwb_store.extensions.formatterUTC
import cenergy.central.com.pwb_store.extensions.toDate
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class PaymentSuccessFragment : Fragment(), ApiResponseCallback<OrderResponse> {

    private var preferenceManager: PreferenceManager? = null
    // widget view
    private lateinit var recycler: RecyclerView
    private lateinit var orderNumber: PowerBuyTextView
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
    private lateinit var tvDeliveryType: PowerBuyTextView
    private lateinit var tvDeliveryAddress: PowerBuyTextView
    private lateinit var tvReceiverName: PowerBuyTextView
    private lateinit var tvBillingName: PowerBuyTextView
    private lateinit var tvBillingAddress: PowerBuyTextView
    private lateinit var tvBillingTelephone: PowerBuyTextView
    private lateinit var tvBillingEmail: PowerBuyTextView
    private lateinit var tvDeliveryInfo: PowerBuyTextView
    private lateinit var tvAmount: TextView
    private lateinit var tvShippingAmount: TextView
    private lateinit var finishButton: CardView
    private lateinit var storeAddressLayout: LinearLayout
    private lateinit var billingAddressLayout: LinearLayout
    private lateinit var deliveryLayout: LinearLayout
    private lateinit var staffIconLayout: LinearLayout
    private lateinit var customerNameLayout: LinearLayout
    private lateinit var deliveryInfoLayout: LinearLayout
    private lateinit var billingEmailLayout: LinearLayout
    private lateinit var billingTelephoneLayout: LinearLayout
    private lateinit var amountLayout: LinearLayout
    private var mProgressDialog: ProgressDialog? = null

    // data
    private var orderId: String? = null
    private var cacheOrderId: String? = null
    private var orderProductListAdapter = OrderProductListAdapter()
    private var cacheCartItems: ArrayList<CacheCartItem>? = arrayListOf()
    private var database = RealmController.getInstance()
    private var deliveryType: DeliveryType? = null
    //TODO: Delete shippingInfo && billingInfo this is for testing the api still not sent about sub address in custom_attribute
    private var shippingInfo: AddressInformation? = null
    private var billingInfo: AddressInformation? = null
    private var branchAddress: Branch? = null
    private var cacheOrder: Order? = null


    companion object {
        private const val ARG_ORDER_ID = "ARG_ORDER_ID"
        private const val ARG_CART_ITEMS = "ARG_CART_ITEMS"
        private const val ARG_CACHE_ORDER_ID = "ARG_CACHE_ORDER_ID"
        private const val SAME_BILLING = 1

        fun newInstance(orderId: String, cacheCartItems: ArrayList<CacheCartItem>): PaymentSuccessFragment {
            val fragment = PaymentSuccessFragment()
            val args = Bundle()
            args.putString(ARG_ORDER_ID, orderId)
            args.putParcelableArrayList(ARG_CART_ITEMS, cacheCartItems)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = context?.let { PreferenceManager(it) }
        orderId = arguments?.getString(ARG_ORDER_ID)
        cacheOrderId = arguments?.getString(ARG_CACHE_ORDER_ID)
        cacheCartItems = arguments?.getParcelableArrayList(ARG_CART_ITEMS)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            val paymentListener = context as PaymentProtocol
            this.deliveryType = paymentListener.getSelectedDeliveryType()
            this.shippingInfo = paymentListener.getShippingAddress()
            this.billingInfo = paymentListener.getBillingAddress()
            this.branchAddress = paymentListener.getSelectedBranch()
        } catch (e: Exception) {
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
        totalPrice = rootView.findViewById(R.id.txt_total_price_order_success)
        orderDate = rootView.findViewById(R.id.txt_order_date_order_success)
        tvShippingHeader = rootView.findViewById(R.id.tvShippingHeader)
        tvDeliveryType = rootView.findViewById(R.id.tvDeliveryType)
        tvDeliveryAddress = rootView.findViewById(R.id.tvDeliveryAddress)
        tvReceiverName = rootView.findViewById(R.id.tvReceiverName)
        tvBillingName = rootView.findViewById(R.id.tvBillingName)
        tvBillingAddress = rootView.findViewById(R.id.tvBillingAddress)
        tvBillingTelephone = rootView.findViewById(R.id.tvBillingTelephone)
        tvBillingEmail = rootView.findViewById(R.id.tvBillingEmail)
        tvDeliveryInfo = rootView.findViewById(R.id.tvDeliveryInfo)
        tvShippingAmount = rootView.findViewById(R.id.tvShippingAmount)
        tvAmount = rootView.findViewById(R.id.tvAmount)

        storeAddressLayout = rootView.findViewById(R.id.storeAddressLayout)
        billingAddressLayout = rootView.findViewById(R.id.billingAddressLayout)
        customerNameLayout = rootView.findViewById(R.id.customerNameLayout)
        deliveryInfoLayout = rootView.findViewById(R.id.deliveryInfoLayout)
        billingEmailLayout = rootView.findViewById(R.id.billingEmailLayout)
        billingTelephoneLayout = rootView.findViewById(R.id.billingTelephoneLayout)
        deliveryLayout = rootView.findViewById(R.id.deliveryLayout)
        staffIconLayout = rootView.findViewById(R.id.staffIconLayout)
        amountLayout = rootView.findViewById(R.id.amountLayout)

        // customer
        name = rootView.findViewById(R.id.txt_name_order_success)
        email = rootView.findViewById(R.id.txt_email_order_success)
        contactNo = rootView.findViewById(R.id.txt_contact_no_order_success)
        // staff
        branch = rootView.findViewById(R.id.txt_branch_order_success)
        address = rootView.findViewById(R.id.txt_address_order_success)
        tel = rootView.findViewById(R.id.txt_tell_order_success)
        openToday = rootView.findViewById(R.id.txt_open_today_order_success)
        finishButton = rootView.findViewById(R.id.finish_btn_order_success)

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = orderProductListAdapter

        if (orderId != null) {
            getOrder()
        }

        if (cacheOrderId != null) {
            getOrderFromLocalDatabase(cacheOrderId!!)
            finishButton.visibility = View.GONE
        }

        staffIconLayout.setOnClickListener {
            StaffHowToDialogFragment.newInstance().show(fragmentManager, "dialog")
        }
    }

    private fun getOrder() {
        context?.let { HttpManagerMagento.getInstance(it).getOrder(orderId!!, this) }
    }

    private fun getOrderFromLocalDatabase(orderId: String) {
        cacheOrder = database.getOrder(orderId)
        cacheOrder?.let { cacheOrder -> updateViewOrder(cacheOrder) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewOrder(order: Order) {
        val orderResponse = order.orderResponse

        val unit = context!!.getString(R.string.baht)

        val shippingAddress = orderResponse!!.orderExtension?.shippingAssignments?.get(0)?.shipping?.shippingAddress
        val billingAddress = orderResponse.billingAddress

        orderProductListAdapter.listItems = orderResponse.items ?: arrayListOf()
        //Setup order number
        orderNumber.text = "${resources.getString(R.string.order_number)} ${order.orderId}"

        //Setup customer
        orderDate.text = orderResponse.createdAt.toDate().formatterUTC()
        email.text = shippingAddress?.email
        contactNo.text = shippingAddress?.telephone
        finishButton.setOnClickListener {
            finishThisPage()
        }

        // setup shipping address or pickup at store
        if (orderResponse.shippingType != DeliveryType.STORE_PICK_UP.toString()) {
            deliveryLayout.visibility = View.VISIBLE
            billingAddressLayout.visibility = View.VISIBLE
            deliveryInfoLayout.visibility = View.VISIBLE
            amountLayout.visibility = View.VISIBLE
            storeAddressLayout.visibility = View.GONE
            customerNameLayout.visibility = View.GONE

            tvShippingHeader.text = getString(R.string.delivery_detail)
            tvDeliveryType.text = orderResponse.shippingType
            tvReceiverName.text = shippingAddress?.getDisplayName()
            tvDeliveryAddress.text = getAddress(shippingAddress)
            tvDeliveryInfo.text = orderResponse.shippingDescription
            tvBillingName.text = billingAddress?.getDisplayName()
            tvBillingAddress.text = getAddress(billingAddress)
            if(billingAddress?.sameBilling == SAME_BILLING){
                billingEmailLayout.visibility = View.GONE
                billingTelephoneLayout.visibility = View.GONE
            } else {
                billingEmailLayout.visibility = View.VISIBLE
                billingTelephoneLayout.visibility = View.VISIBLE
                tvBillingEmail.text = billingAddress?.email
                tvBillingTelephone.text = billingAddress?.telephone
            }
        } else {
            deliveryLayout.visibility = View.GONE
            billingAddressLayout.visibility = View.GONE
            deliveryInfoLayout.visibility = View.GONE
            amountLayout.visibility = View.GONE
            storeAddressLayout.visibility = View.VISIBLE
            customerNameLayout.visibility = View.VISIBLE

            name.text = billingAddress?.getDisplayName()

            tvShippingHeader.text = getString(R.string.store_collection_detail)

            val branchShipping = order.branchShipping
            if (branchShipping != null) {
                branch.text = branchShipping.storeName
                address.text = branchShipping.address
                tel.text = branchShipping.phone
                openToday.text = branchShipping.description
            }
        }

        // setup total or summary
        totalPrice.text = getDisplayPrice(unit, orderResponse.baseTotal.toString())
        val amount = (orderResponse.baseTotal - orderResponse.shippingAmount)
        tvAmount.text = getDisplayPrice(unit, amount.toString())
        tvShippingAmount.text = getDisplayPrice(unit, orderResponse.shippingAmount.toString())
        mProgressDialog?.dismiss()
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

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    // {@like implement ApiResponseCallback<OrderResponse>}
    override fun success(response: OrderResponse?) {
        if (response != null) {

            // TODO: Delete shippingInfo && billingInfo... this is for testing the api still not sent about sub address in custom_attribute
            response.billingAddress = billingInfo ?: shippingInfo

            if (response.shippingType != DeliveryType.STORE_PICK_UP.toString()) {
                response.orderExtension?.shippingAssignments?.get(0)?.shipping?.shippingAddress = shippingInfo
            }

            // add shipping type
            response.shippingType = deliveryType?.toString() ?: ""

            // TBD- keep imageUrl .... remove later waiting API have imageURL
//            for (cacheItem in cacheCartItems!!) {
//                val items = response.items?.filter { it.sku == cacheItem.sku }
//                if (items != null && items.isNotEmpty()) {
//                    Log.d("OrderResponse", cacheItem.imageUrl)
//                    items[0].imageUrl = cacheItem.imageUrl
//                }
//            }

            response.items?.forEach { item ->
                val isCacheItem = cacheCartItems?.firstOrNull { it.sku == item.sku }
                if (isCacheItem != null){
                    item.imageUrl = isCacheItem.imageUrl
                } else {
                    item.isFreebie = true
                }
            }

            // save order to local database
            val userInformation = database.userInformation
            val order = Order(orderId = response.orderId ?: "", userInformation = userInformation, orderResponse = response, branchShipping = branchAddress)

            database.saveOrder(order, object : DatabaseListener{
                override fun onSuccessfully() {
                    updateViewOrder(order)
                }

                override fun onFailure(error: Throwable) {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", "" + error.message)
                }
            })
//            database.saveOrder(order)
            clearCachedCart() // clear cache item
        } else {
            mProgressDialog?.dismiss()
            showAlertDialog("", resources.getString(R.string.some_thing_wrong))
        }
    }

    override fun failure(error: APIError) {
        mProgressDialog?.dismiss()
        showAlertDialog("", error.errorMessage)
    }
    //endregion

    private fun getAddress(address: AddressInformation?): String {
        if (address == null) {
            return ""
        }
        var text = ""
        val subAddress = address.subAddress
        if (subAddress != null) {
            if (subAddress.houseNumber.isNotBlank()) {
                text += subAddress.houseNumber + ", "
            }
            if (subAddress.soi.isNotBlank()) {
                text += subAddress.soi + ", "
            }
            if (subAddress.building.isNotBlank()) {
                text += subAddress.building + ", "
            }
            if (address.street != null && address.street!![0] != null && address.street!![0]!!.isNotBlank()) {
                text += address.street!![0] + ", "
            }
            if (subAddress.subDistrict.isNotBlank()) {
                text += subAddress.subDistrict + ", "
            }
            if (subAddress.district.isNotBlank()) {
                text += subAddress.district + ", "
            }
        }
        text += address.region + ", "
        text += address.postcode
        return text
    }

    private fun clearCachedCart() {
        preferenceManager?.clearCartId()
        RealmController.getInstance().deleteAllCacheCartItem()
        Log.d("Order Success", "Cleared cached CartId and CartItem")
    }
}