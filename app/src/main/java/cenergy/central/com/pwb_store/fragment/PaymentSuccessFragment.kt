package cenergy.central.com.pwb_store.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.MainActivity
import cenergy.central.com.pwb_store.adapter.OrderProductListAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.response.Item
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class PaymentSuccessFragment : Fragment() {

    lateinit var recycler: RecyclerView
    lateinit var orderNumber: PowerBuyTextView
    lateinit var totalPrice: PowerBuyTextView
    lateinit var orderDate: PowerBuyTextView
    lateinit var name: PowerBuyTextView
    lateinit var email: PowerBuyTextView
    lateinit var contactNo: PowerBuyTextView
    lateinit var branch: PowerBuyTextView
    lateinit var address: PowerBuyTextView
    lateinit var tell: PowerBuyTextView
    lateinit var openToday: PowerBuyTextView
    lateinit var finishButton: CardView
    private var orderId: String? = null
    private var listItems: ArrayList<Item> = arrayListOf()
    private var orderProductListAdapter = OrderProductListAdapter()
    private var mProgressDialog: ProgressDialog? = null

    companion object {
        private const val ORDER_ID = "ORDER_ID"

        fun newInstance(orderId: String): PaymentSuccessFragment {
            val fragment = PaymentSuccessFragment()
            val args = Bundle()
            args.putString(ORDER_ID, orderId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getString(ORDER_ID)
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
        name = rootView.findViewById(R.id.txt_name_order_success)
        email = rootView.findViewById(R.id.txt_email_order_success)
        contactNo = rootView.findViewById(R.id.txt_contact_no_order_success)
        branch = rootView.findViewById(R.id.txt_branch_order_success)
        address = rootView.findViewById(R.id.txt_address_order_success)
        tell = rootView.findViewById(R.id.txt_tell_order_success)
        openToday = rootView.findViewById(R.id.txt_open_today_order_success)
        finishButton = rootView.findViewById(R.id.finish_btn_order_success)

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = orderProductListAdapter

        if (orderId != null) {
            getOrder()
        }
    }

    private fun getOrder() {
        HttpManagerMagento.getInstance().getOrder(orderId!!, object : ApiResponseCallback<OrderResponse> {
            override fun success(response: OrderResponse?) {
                if (response != null) {
                    RealmController.with(context).saveOrderResponse(response)
                    updateViewOrder(response)
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                showAlertDialog("", error.errorMessage)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewOrder(response: OrderResponse) {
        val unit = context!!.getString(R.string.baht)
        listItems = response.items
        orderProductListAdapter.listItems = this@PaymentSuccessFragment.listItems
        //Setup order number
        orderNumber.text = "${resources.getString(R.string.order_number)} ${response.orderId}"

        //Setup total price
        var total = 0.0
        listItems.forEach {
            total += it.baseTotalIncludeTax!!
        }
        totalPrice.text = getDisplayPrice(unit, total.toString())

        //Setup customer
        orderDate.text = response.updatedAt
        name.text = "${response.billingAddress!!.firstname} ${response.billingAddress!!.lastname}"
        email.text = response.billingAddress!!.email
        contactNo.text = response.billingAddress!!.telephone
        mProgressDialog?.dismiss()
        finishButton.setOnClickListener {
            finishThisPage()
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
}