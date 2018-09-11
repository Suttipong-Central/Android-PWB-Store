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
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.AddressAdapter
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentDescriptionListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.UserInformation
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import me.a3cha.android.thaiaddress.models.District
import me.a3cha.android.thaiaddress.models.Postcode
import me.a3cha.android.thaiaddress.models.SubDistrict
import java.text.NumberFormat
import java.util.*

class PaymentDescriptionFragment : Fragment(), View.OnFocusChangeListener {
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.inputProvince -> {
                if (hasFocus) {
                    provinceInput.showDropDown()
                }
            }
            R.id.inputDistrict -> {
                if (hasFocus) {
                    districtInput.showDropDown()
                }
            }
            R.id.inputSubDistrict -> {
                if (hasFocus) {
                    subDistrictInput.showDropDown()
                }
            }
            R.id.inputPostcode -> {
                if (hasFocus) {
                    postcodeInput.showDropDown()
                }
            }
        }
    }

    // widget view
    private lateinit var recycler: RecyclerView
    private lateinit var firstNameEdt: PowerBuyEditTextBorder
    private lateinit var lastNameEdt: PowerBuyEditTextBorder
    private lateinit var contactNumberEdt: PowerBuyEditTextBorder
    private lateinit var emailEdt: PowerBuyEditTextBorder
    private lateinit var homeNoEdt: PowerBuyEditTextBorder
    private lateinit var homeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var homeSoiEdt: PowerBuyEditTextBorder
    private lateinit var homeRoadEdt: PowerBuyEditTextBorder
    private lateinit var homePhoneEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var deliveryBtn: CardView

    private lateinit var provinceInput: AutoCompleteTextView
    private lateinit var districtInput: AutoCompleteTextView
    private lateinit var subDistrictInput: AutoCompleteTextView
    private lateinit var postcodeInput: AutoCompleteTextView

    // data
    private val database = RealmController.with(context)
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
    private val provinces = database.provinces
    private var districts = emptyList<District>()
    private var subDistricts = emptyList<SubDistrict>()
    private var postcodes = emptyList<Postcode>()
    // adapter
    private var provinceAdapter: AddressAdapter? = null
    private var districtAdapter: AddressAdapter? = null
    private var subDistrictAdapter: AddressAdapter? = null
    private var postcodeAdapter: AddressAdapter? = null

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
        userInformation = database.userInformation
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
        homePhoneEdt = rootView.findViewById(R.id.tell_payment)

        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)
        deliveryBtn = rootView.findViewById(R.id.delivery_button_payment)

        // setup view input address
        provinceInput = rootView.findViewById(R.id.inputProvince)
        districtInput = rootView.findViewById(R.id.inputDistrict)
        subDistrictInput = rootView.findViewById(R.id.inputSubDistrict)
        postcodeInput = rootView.findViewById(R.id.inputPostcode)

        setupInputAddress()

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
            provinceInput.setText(member.homeCity)
            districtInput.setText(member.homeDistrict)
            subDistrictInput.setText(member.homeSubDistrict)
            postcodeInput.setText(member.homePostalCode)
            homePhoneEdt.setText(member.homePhone)
        }

        val shoppingCartAdapter = ShoppingCartAdapter(null, true)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.isNestedScrollingEnabled = false
        recycler.adapter = shoppingCartAdapter
        shoppingCartAdapter.cartItemList = this.cartItemList

        val unit = Contextor.getInstance().context.getString(R.string.baht)
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

    private fun setupInputAddress() {
        val provinceNameList = provinces.map { Pair(first = it.provinceId, second = it.nameTh) }
        var districtNameList = districts.map { Pair(first = it.districtId, second = it.nameTh) }
        var subDistrictNameList = subDistricts.map { Pair(first = it.subDistrictId, second = it.nameTh) }
        var postcodeList = postcodes.map { Pair(first = it.id, second = it.postcode.toString()) }


        // setup province
        provinceAdapter = AddressAdapter(context!!, R.layout.layout_text_item, provinceNameList)
        provinceAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                provinceInput.setText(item.second)
                provinceInput.clearFocus()

                districtInput.setText("")

                districts = database.getDistrictsByProvinceId(item.first)
                districtNameList = districts.map { Pair(first = it.districtId, second = it.nameTh) }
                districtAdapter?.setItems(districtNameList)

                hideKeyboard()
            }
        })

        // setup district
        districtAdapter = AddressAdapter(context!!, R.layout.layout_text_item, districtNameList)
        districtAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                districtInput.setText(item.second)
                districtInput.clearFocus()

                subDistrictInput.setText("")
                subDistricts = database.getSubDistrictsByDistrictId(item.first)
                subDistrictNameList = subDistricts.map { Pair(first = it.subDistrictId, second = it.nameTh) }

                subDistrictAdapter?.setItems(subDistrictNameList)

                hideKeyboard()
            }

        })

        // setup sub district
        subDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, subDistrictNameList)
        subDistrictAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                subDistrictInput.setText(item.second)
                subDistrictInput.clearFocus()

                postcodeInput.setText("")
                postcodes = database.getPostcodeBySubDistrictId(item.first)
                postcodeList = postcodes.map { Pair(first = it.id, second = it.postcode.toString()) }
                postcodeAdapter?.setItems(postcodeList)

                hideKeyboard()
            }
        })

        // setup postcode
        postcodeAdapter = AddressAdapter(context!!, R.layout.layout_text_item, postcodeList)
        postcodeAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                postcodeInput.setText(item.second)

                hideKeyboard()
            }

        })

        // setup adapter
        provinceInput.setAdapter(provinceAdapter)
        districtInput.setAdapter(districtAdapter)
        subDistrictInput.setAdapter(subDistrictAdapter)
        postcodeInput.setAdapter(postcodeAdapter)

        // setup onClick
        provinceInput.setOnClickListener { provinceInput.showDropDown() }
        districtInput.setOnClickListener { districtInput.showDropDown() }
        subDistrictInput.setOnClickListener { subDistrictInput.showDropDown() }
        postcodeInput.setOnClickListener { postcodeInput.showDropDown() }

        // setup onFocus
        provinceInput.onFocusChangeListener = this
        districtInput.onFocusChangeListener = this
        subDistrictInput.onFocusChangeListener = this
        postcodeInput.onFocusChangeListener = this
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
            homeCity = provinceInput.text.toString()
            homeDistrict = districtInput.text.toString()
            homeSubDistrict = subDistrictInput.text.toString()
            homePostalCode = postcodeInput.text.toString()
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

    private fun hideKeyboard() {
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity?.currentFocus
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }
}