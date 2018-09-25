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
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.MainActivity
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.OrderProductListAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.Item
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class PaymentSuccessFragment : Fragment(), ApiResponseCallback<OrderResponse> {

    // widget view
    lateinit var recycler: RecyclerView
    lateinit var orderNumber: PowerBuyTextView
    lateinit var totalPrice: PowerBuyTextView
    lateinit var orderDate: PowerBuyTextView
    lateinit var name: PowerBuyTextView
    lateinit var email: PowerBuyTextView
    lateinit var contactNo: PowerBuyTextView
    lateinit var branch: PowerBuyTextView
    lateinit var address: PowerBuyTextView
    lateinit var tel: PowerBuyTextView
    lateinit var openToday: PowerBuyTextView
    lateinit var tvShippingHeader: PowerBuyTextView
    lateinit var tvDeliveryType: PowerBuyTextView
    lateinit var tvDeliveryAddress: PowerBuyTextView
    lateinit var tvReceiverName: PowerBuyTextView
    lateinit var finishButton: CardView
    lateinit var storeAddressLayout: LinearLayout
    lateinit var deliveryLayout: LinearLayout
    private var mProgressDialog: ProgressDialog? = null

    // data
    private var orderId: String? = null
    private var cacheOrderId: String? = null
    private var listItems: List<Item>? = arrayListOf()
    private var orderProductListAdapter = OrderProductListAdapter()
    private var cacheCartItems: ArrayList<CacheCartItem>? = arrayListOf()
    private var database = RealmController.with(context)
    private var deliveryType: DeliveryType? = null
    //TODO: Delete shippingInfo... this is for testing the api still not sent about sub address in custom_attribute
    private var shippingInfo: AddressInformation? = null


    companion object {
        private const val ARG_ORDER_ID = "ARG_ORDER_ID"
        private const val ARG_CART_ITEMS = "ARG_CART_ITEMS"
        private const val ARG_CACHE_ORDER_ID = "ARG_CACHE_ORDER_ID"

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
        storeAddressLayout = rootView.findViewById(R.id.storeAddressLayout)
        deliveryLayout = rootView.findViewById(R.id.deliveryLayout)

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
    }

    private fun getOrder() {
        context?.let { HttpManagerMagento.getInstance(it).getOrder(orderId!!, this) }
    }

    private fun getOrderFromLocalDatabase(cacheOrderResponseId: String) {
        val cacheOrderResponse = database.getOrder(cacheOrderResponseId)
        val orderResponse = cacheOrderResponse.orderResponse
        val userInformation = cacheOrderResponse.userInformation
        orderResponse?.let { updateViewOrder(it, userInformation) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewOrder(order: OrderResponse, userInformation: UserInformation?) {
        listItems = order.items

        val unit = context!!.getString(R.string.baht)

        orderProductListAdapter.listItems = listItems ?: arrayListOf()
        //Setup order number
        orderNumber.text = "${resources.getString(R.string.order_number)} ${order.orderId}"

        //Setup total price
        totalPrice.text = getDisplayPrice(unit, order.baseTotal.toString())

        //Setup customer
        orderDate.text = order.updatedAt
        name.text = "${order.billingAddress!!.firstname} ${order.billingAddress!!.lastname}"
        email.text = order.billingAddress!!.email
        contactNo.text = order.billingAddress!!.telephone
        mProgressDialog?.dismiss()
        finishButton.setOnClickListener {
            finishThisPage()
        }

        // setup shipping address or pickup at store
        if (order.shippingType != DeliveryType.STORE_PICK_UP.toString()) {
            deliveryLayout.visibility = View.VISIBLE
            storeAddressLayout.visibility = View.GONE

            val address = order.billingAddress
            val subAddress = address?.subAddress
            tvShippingHeader.text = getString(R.string.delivery_detail)
            tvDeliveryType.text = order.shippingType
            tvReceiverName.text = address?.getDisplayName()
//            getAddress(order.billingAddress) //TODO: create get address
            tvDeliveryAddress.text = "${subAddress?.houseNumber ?: ""}, ${subAddress?.soi
                    ?: ""}, ${subAddress?.building ?: ""}, ${subAddress?.subDistrict
                    ?: ""}, ${subAddress?.district ?: ""}, ${address?.region
                    ?: ""}, ${address?.postcode
                    ?: ""}".trim()

        } else {
            deliveryLayout.visibility = View.GONE
            storeAddressLayout.visibility = View.VISIBLE
            tvShippingHeader.text = getString(R.string.store_collection_detail)

            val store = userInformation?.store
            branch.text = "${store?.storeName} <Test>"
            address.text = "${store?.number ?: ""} ${store?.moo ?: ""} ${store?.soi
                    ?: ""} ${store?.road ?: ""} ${store?.building ?: ""} ${store?.subDistrict
                    ?: ""} ${store?.district ?: ""} ${store?.province ?: ""} ${store?.postalCode
                    ?: ""}".trim()
            tel.text = "-"
            openToday.text = "-"
        }
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
    override fun success(orderResponse: OrderResponse?) {
        if (orderResponse != null) {
            // TBD- keep imageUrl .... remove later waiting API have imageURL
            for (cacheItem in cacheCartItems!!) {
                val items = orderResponse.items?.filter { it.sku == cacheItem.sku }
                if (items != null && items.isNotEmpty()) {
                    Log.d("OrderResponse", cacheItem.imageUrl)
                    items[0].imageUrl = cacheItem.imageUrl
                }
            }

            //TODO: Delete shippingInfo... this is for testing the api still not sent about sub address in custom_attribute
            orderResponse.billingAddress = shippingInfo

            // add shipping type
            orderResponse.shippingType = deliveryType?.toString() ?: ""

            // save order to local database
            val userInformation = database.userInformation
            database.saveOrder(Order(orderId = orderResponse.orderId
                    ?: "", userInformation = userInformation, orderResponse = orderResponse))

            updateViewOrder(orderResponse, userInformation)
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

        var text: String = ""
        val subAddress = address.subAddress
        "${subAddress?.houseNumber ?: ""}, ${subAddress?.soi
                ?: ""}, ${subAddress?.building ?: ""}, ${subAddress?.subDistrict
                ?: ""}, ${subAddress?.district ?: ""}, ${address?.region
                ?: ""}, ${address?.postcode
                ?: ""}"

        if (subAddress?.houseNumber == null) {
            text += subAddress?.houseNumber + ", "
        }

    }

}