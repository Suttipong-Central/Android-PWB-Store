package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentDescriptionListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import java.text.NumberFormat
import java.util.*

class PaymentDescriptionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var firstNameEdt: PowerBuyEditTextBorder
    private lateinit var lastNameEdt: PowerBuyEditTextBorder
    private lateinit var contactNumberEdt: PowerBuyEditTextBorder
    private lateinit var emailEdt: PowerBuyEditTextBorder
    private lateinit var houseNoEdt: PowerBuyEditTextBorder
    private lateinit var placeOrBuildingEdit: PowerBuyEditTextBorder
    private lateinit var soiEdt: PowerBuyEditTextBorder
    private lateinit var streetEdt: PowerBuyEditTextBorder
    private lateinit var provinceEdt: PowerBuyEditTextBorder
    private lateinit var districtEdt: PowerBuyEditTextBorder
    private lateinit var subDistrictEdt: PowerBuyEditTextBorder
    private lateinit var postCodeEdt: PowerBuyEditTextBorder
    private lateinit var tellEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var paymentBtn: CardView
    private var cartItemList: List<CartItem> = listOf()
    private var paymentClickListener: PaymentClickListener? = null
    private var paymentDescriptionListener: PaymentDescriptionListener? = null
    private var mProgressDialog: ProgressDialog? = null
    private var cartId: String? = null
    private var member: Member? = null
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var contactNo: String

    companion object {
        private const val MEMBER = "MEMBER"

        fun newInstance(): PaymentDescriptionFragment {
            val fragment = PaymentDescriptionFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(member: Member): PaymentDescriptionFragment {
            val fragment = PaymentDescriptionFragment()
            val args = Bundle()
            args.putParcelable(MEMBER, member)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentClickListener = context as PaymentClickListener
        paymentDescriptionListener = context as PaymentDescriptionListener
        cartItemList = paymentDescriptionListener!!.getItemList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = context?.let { PreferenceManager(it) }
        cartId = preferenceManager?.cartId
        member = arguments?.getParcelable(MEMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_description, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        houseNoEdt = rootView.findViewById(R.id.house_no_payment)
        placeOrBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        soiEdt = rootView.findViewById(R.id.soi_payment)
        streetEdt = rootView.findViewById(R.id.street_payment)
        provinceEdt = rootView.findViewById(R.id.province_payment)
        districtEdt = rootView.findViewById(R.id.district_payment)
        subDistrictEdt = rootView.findViewById(R.id.sub_district_payment)
        postCodeEdt = rootView.findViewById(R.id.post_code_payment)
        tellEdt = rootView.findViewById(R.id.tell_payment)
        paymentBtn = rootView.findViewById(R.id.payment_button_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)

        //Set Input type
        contactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        contactNumberEdt.setTextLength(10)
        tellEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        tellEdt.setTextLength(10)
        emailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS)

        //Set Member
        member?.let { member ->
            firstNameEdt.setText(member.getFirstName())
            lastNameEdt.setText(member.getLastName())
            emailEdt.setText(member.email)
            contactNumberEdt.setText(member.mobilePhone)
        }

        val shoppingCartAdapter = ShoppingCartAdapter(null, true)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = shoppingCartAdapter
        shoppingCartAdapter.cartItemList = this.cartItemList

        val unit = Contextor.getInstance().context.getString(R.string.baht)
        var total = 0.0
        cartItemList.forEach {
            total += it.qty!! * it.price!!
        }
        totalPrice.text = getDisplayPrice(unit, total.toString())
        paymentBtn.setOnClickListener {
            checkConfirm()
        }
    }

    private fun checkConfirm() {
        showProgressDialog()
        if (firstNameEdt.getText().isNotEmpty() && lastNameEdt.getText().isNotEmpty() &&
                emailEdt.getText().isNotEmpty() && contactNumberEdt.getText().isNotEmpty()) {
            firstName = firstNameEdt.getText()
            lastName = lastNameEdt.getText()
            email = emailEdt.getText()
            contactNo = contactNumberEdt.getText()
            showAlertCheckPayment("", resources.getString(R.string.comfrim_oder))
        } else {
            mProgressDialog?.dismiss()
            showAlertDialog("", resources.getString(R.string.fill_in_important_information))
        }
    }

    private fun createBilling() {
        if (cartId != null) {
            val shippingAddress = AddressInformation.createTestAddress(firstName, lastName, email, contactNo)
            val billingAddress = AddressInformation.createTestAddress(firstName, lastName, email, contactNo)

            HttpManagerMagento.getInstance().createShippingInformation(cartId!!, shippingAddress, billingAddress,
                    object : ApiResponseCallback<ShippingInformationResponse> {
                        override fun success(response: ShippingInformationResponse?) {
                            if (response != null) {
                                updateOrder()
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
    }

    private fun updateOrder() {
        HttpManagerMagento.getInstance().updateOder(cartId!!, object : ApiResponseCallback<String> {
            override fun success(response: String?) {
                if (response != null) {
                    mProgressDialog?.dismiss()
                    clearCachedCart()
                    paymentClickListener?.onPaymentClickListener(response)
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

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun showAlertCheckPayment(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which -> createBilling() }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, which -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
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

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(context)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun clearCachedCart() {
        val preferenceManager = context?.let { PreferenceManager(it) }
        preferenceManager?.clearCartId()
        RealmController.getInstance().deleteAllCacheCartItem()
        Log.d("Order Success", "Cleared cached CartId and CartItem")
    }
}