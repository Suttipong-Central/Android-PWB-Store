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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentDescriptionListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
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
    private lateinit var homeNoEdt: PowerBuyEditTextBorder
    private lateinit var homeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var homeSoiEdt: PowerBuyEditTextBorder
    private lateinit var homeRoadEdt: PowerBuyEditTextBorder
    private lateinit var homeCityEdt: PowerBuyEditTextBorder
    private lateinit var homeDistrictEdt: PowerBuyEditTextBorder
    private lateinit var homeSubDistrictEdt: PowerBuyEditTextBorder
    private lateinit var homePostalCodeEdt: PowerBuyEditTextBorder
    private lateinit var homePhoneEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var deliveryBtn: CardView
    private var cartItemList: List<CartItem> = listOf()
    private var paymentDescriptionListener: PaymentDescriptionListener? = null
    private var getDeliveryOptionsListenerListener: DeliveryOptionsListener? = null
    private var mProgressDialog: ProgressDialog? = null
    private var cartId: String? = null
    private var member: Member? = null
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var contactNo: String
    private lateinit var homeNo: String
    private lateinit var homeBuilding: String
    private lateinit var homeSoi: String
    private lateinit var homeRoad: String
    private lateinit var homeCity: String
    private lateinit var homeDistrict: String
    private lateinit var homeSubDistrict: String
    private lateinit var homePostalCode: String
    private lateinit var homePhone: String
    private lateinit var userInformation: UserInformation

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
        paymentDescriptionListener = context as PaymentDescriptionListener
        getDeliveryOptionsListenerListener = context as DeliveryOptionsListener
        cartItemList = paymentDescriptionListener!!.getItemList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = context?.let { PreferenceManager(it) }
        userInformation = RealmController.with(context).userInformation
        cartId = preferenceManager?.cartId
        member = arguments?.getParcelable(MEMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_description, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        //User
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        //Billing address
        homeNoEdt = rootView.findViewById(R.id.house_no_payment)
        homeBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        homeSoiEdt = rootView.findViewById(R.id.soi_payment)
        homeRoadEdt = rootView.findViewById(R.id.street_payment)
        homeCityEdt = rootView.findViewById(R.id.province_payment)
        homeDistrictEdt = rootView.findViewById(R.id.district_payment)
        homeSubDistrictEdt = rootView.findViewById(R.id.sub_district_payment)
        homePostalCodeEdt = rootView.findViewById(R.id.post_code_payment)
        homePhoneEdt = rootView.findViewById(R.id.tell_payment)

        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)
        deliveryBtn = rootView.findViewById(R.id.delivery_button_payment)

        //Set Input type
        contactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        contactNumberEdt.setTextLength(10)
        homePhoneEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        homePhoneEdt.setTextLength(10)
        emailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS)

        //Set Member
        member?.let { member ->
            firstNameEdt.setText(member.getFirstName())
            lastNameEdt.setText(member.getLastName())
            contactNumberEdt.setText(member.mobilePhone)
            emailEdt.setText(member.email)

            homeNoEdt.setText(member.homeNo)
            homeBuildingEdit.setText(member.homeBuilding)
            homeSoiEdt.setText(member.homeSoi)
            homeRoadEdt.setText(member.homeRoad)
            homeCityEdt.setText(member.homeCity)
            homeDistrictEdt.setText(member.homeDistrict)
            homeSubDistrictEdt.setText(member.homeSubDistrict)
            homePostalCodeEdt.setText(member.homePostalCode)
            homePhoneEdt.setText(member.homePhone)
        }

        val shoppingCartAdapter = ShoppingCartAdapter(null, true)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = shoppingCartAdapter
        shoppingCartAdapter.cartItemList = this.cartItemList

        val unit = Contextor.getInstance().context.getString(R.string.baht)
        val database = RealmController.getInstance()
        var total = 0.0
        cartItemList.forEach {
            if (database.getCacheCartItem(it.id) != null) {
                total += it.qty!! * it.price!!
            }
        }
        totalPrice.text = getDisplayPrice(unit, total.toString())
        deliveryBtn.setOnClickListener {
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

            homeNo = homeNoEdt.getText()
            homeBuilding = homeBuildingEdit.getText()
            homeSoi = homeSoiEdt.getText()
            homeRoad = homeRoadEdt.getText()
            homeCity = homeCityEdt.getText()
            homeDistrict = homeDistrictEdt.getText()
            homeSubDistrict = homeSubDistrictEdt.getText()
            homePostalCode = homePostalCodeEdt.getText()
            homePhone = homePhoneEdt.getText()

            val shippingAddress = AddressInformation.createAddress(
                    firstName, lastName, email, contactNo, homeNo, homeBuilding, homeSoi,
                    homeRoad, homeCity, homeDistrict, homeSubDistrict, homePostalCode, homePhone)

            mProgressDialog?.dismiss()
            getDeliveryOptionsListenerListener?.onDeliveryOptions(shippingAddress)

        } else {
            mProgressDialog?.dismiss()
            showAlertDialog("", resources.getString(R.string.fill_in_important_information))
        }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which -> dialog.dismiss() }

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
}