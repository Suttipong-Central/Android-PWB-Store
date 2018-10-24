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
import android.widget.LinearLayout
import android.widget.RadioGroup
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.adapter.AddressAdapter
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.PwbMember
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.ValidationHelper
import cenergy.central.com.pwb_store.view.PowerBuyAutoCompleteTextStroke
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import me.a3cha.android.thaiaddress.models.District
import me.a3cha.android.thaiaddress.models.Postcode
import me.a3cha.android.thaiaddress.models.Province
import me.a3cha.android.thaiaddress.models.SubDistrict
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt


class PaymentBillingFragment : Fragment() {

    // widget view
    private lateinit var recycler: RecyclerView
    private lateinit var firstNameEdt: PowerBuyEditTextBorder
    private lateinit var lastNameEdt: PowerBuyEditTextBorder
    private lateinit var contactNumberEdt: PowerBuyEditTextBorder
    private lateinit var emailEdt: PowerBuyEditTextBorder
    private lateinit var billingFirstNameEdt: PowerBuyEditTextBorder
    private lateinit var billingLastNameEdt: PowerBuyEditTextBorder
    private lateinit var billingContactNumberEdt: PowerBuyEditTextBorder
    private lateinit var billingEmailEdt: PowerBuyEditTextBorder
    private lateinit var homeNoEdt: PowerBuyEditTextBorder
    private lateinit var homeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var homeSoiEdt: PowerBuyEditTextBorder
    private lateinit var homeRoadEdt: PowerBuyEditTextBorder
    private lateinit var homePhoneEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeNoEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var billingHomeSoiEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeRoadEdt: PowerBuyEditTextBorder
    private lateinit var billingHomePhoneEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var deliveryBtn: CardView
    private lateinit var radioGroup: RadioGroup

    private lateinit var provinceInput: PowerBuyAutoCompleteTextStroke
    private lateinit var districtInput: PowerBuyAutoCompleteTextStroke
    private lateinit var subDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var postcodeInput: PowerBuyAutoCompleteTextStroke

    private lateinit var billingProvinceInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingSubDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingPostcodeInput: PowerBuyAutoCompleteTextStroke

    private lateinit var billingLayout: LinearLayout

    private var mProgressDialog: ProgressDialog? = null

    // data
    private val database = RealmController.with(context)
    private var cartItemList: List<CartItem> = listOf()
    private var shippingAddress: AddressInformation? = null
    private var paymentProtocol: PaymentProtocol? = null
    private var paymentBillingListener: PaymentBillingListener? = null
    private var cartId: String? = null
    private var member: Member? = null
    private var pwbMemberIndex: Int? = null
    private var pwbMember: PwbMember? = null
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var contactNo: String = ""
    private var homeNo: String = ""
    private var homeBuilding: String = ""
    private var homeSoi: String = ""
    private var homeRoad: String = ""
    private var homeProvinceId: String = ""
    private var homeProvince: String = ""
    private var homeProvinceCode: String = ""
    private var homeDistrictId: String = ""
    private var homeDistrict: String = ""
    private var homeSubDistrictId: String = ""
    private var homeSubDistrict: String = ""
    private var homeCountryId: String = ""
    private var homePostalCodeId: String = ""
    private var homePostalCode: String = ""
    private var homePhone: String = ""
    private var isSameBilling = true
    private val provinces = database.provinces
    private var districts = emptyList<District>()
    private var subDistricts = emptyList<SubDistrict>()
    private var postcodes = emptyList<Postcode>()
    private var provinceNameList = getProvinceNameList()
    private var districtNameList = getDistrictNameList()
    private var subDistrictNameList = getSubDistrictNameList()
    private var postcodeList = getPostcodeList()
    private var billingProvinceNameList = getProvinceNameList()
    private var billingDistrictNameList = getDistrictNameList()
    private var billingSubDistrictNameList = getSubDistrictNameList()
    private var billingPostcodeList = getPostcodeList()

    private var province: Province? = null
    private var district: District? = null
    private var subDistrict: SubDistrict? = null
    private var postcode: Postcode? = null
    private var billingProvince: Province? = null
    private var billingDistrict: District? = null
    private var billingSubDistrict: SubDistrict? = null
    private var billingPostcode: Postcode? = null
    // adapter
    private var provinceAdapter: AddressAdapter? = null
    private var districtAdapter: AddressAdapter? = null
    private var subDistrictAdapter: AddressAdapter? = null
    private var postcodeAdapter: AddressAdapter? = null
    private var billingProvinceAdapter: AddressAdapter? = null
    private var billingDistrictAdapter: AddressAdapter? = null
    private var billingSubDistrictAdapter: AddressAdapter? = null
    private var billingPostcodeAdapter: AddressAdapter? = null

    companion object {
        private const val ARG_MEMBER = "arg_member"
        private const val ARG_MEMBER_INDEX = "arg_member_index"
        private const val IS_SAME_BILLING = 1
        private const val IS_NOT_SAME_BILLING = 0

        fun newInstance(): PaymentBillingFragment {
            val fragment = PaymentBillingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(member: Member): PaymentBillingFragment {
            val fragment = PaymentBillingFragment()
            val args = Bundle()
            args.putParcelable(ARG_MEMBER, member)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(pwbMemberIndex: Int): PaymentBillingFragment {
            val fragment = PaymentBillingFragment()
            val args = Bundle()
            args.putInt(ARG_MEMBER_INDEX, pwbMemberIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentProtocol = context as PaymentProtocol
        paymentBillingListener = context as PaymentBillingListener
        cartItemList = paymentProtocol?.getItems() ?: arrayListOf()
        shippingAddress = paymentProtocol?.getShippingAddress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = context?.let { PreferenceManager(it) }
        cartId = preferenceManager?.cartId
        member = arguments?.getParcelable(ARG_MEMBER)
        pwbMemberIndex = arguments?.getInt(ARG_MEMBER_INDEX)
        pwbMemberIndex?.let { pwbMember = paymentProtocol?.getPWBMemberByIndex(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_billing, container, false)
        setupView(rootView)
        return rootView
    }

    private fun setupView(rootView: View) {
        //User
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        //shipping address
        homeNoEdt = rootView.findViewById(R.id.house_no_payment)
        homeBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        homeSoiEdt = rootView.findViewById(R.id.soi_payment)
        homeRoadEdt = rootView.findViewById(R.id.street_payment)
        homePhoneEdt = rootView.findViewById(R.id.tell_payment)
        // setup view input address
        provinceInput = rootView.findViewById(R.id.input_province)
        districtInput = rootView.findViewById(R.id.input_district)
        subDistrictInput = rootView.findViewById(R.id.input_sub_district)
        postcodeInput = rootView.findViewById(R.id.input_postcode)

        //Billing address
        billingFirstNameEdt = rootView.findViewById(R.id.first_name_billing)
        billingLastNameEdt = rootView.findViewById(R.id.last_name_billing)
        billingContactNumberEdt = rootView.findViewById(R.id.contact_number_billing)
        billingEmailEdt = rootView.findViewById(R.id.email_billing)
        billingLayout = rootView.findViewById(R.id.billing_address_layout_payment)
        billingHomeNoEdt = rootView.findViewById(R.id.billing_house_no_payment)
        billingHomeBuildingEdit = rootView.findViewById(R.id.billing_place_or_building_payment)
        billingHomeSoiEdt = rootView.findViewById(R.id.billing_soi_payment)
        billingHomeRoadEdt = rootView.findViewById(R.id.billing_street_payment)
        billingHomePhoneEdt = rootView.findViewById(R.id.billing_tell_payment)
        // setup view input billing address
        billingProvinceInput = rootView.findViewById(R.id.billing_input_province)
        billingDistrictInput = rootView.findViewById(R.id.billing_input_district)
        billingSubDistrictInput = rootView.findViewById(R.id.billing_input_sub_district)
        billingPostcodeInput = rootView.findViewById(R.id.billing_input_postcode)

        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)
        deliveryBtn = rootView.findViewById(R.id.delivery_button_payment)

        //Set Input type
        contactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        contactNumberEdt.setTextLength(10)
        billingContactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        billingContactNumberEdt.setTextLength(10)
        homePhoneEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        homePhoneEdt.setTextLength(10)
        billingHomePhoneEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        billingHomePhoneEdt.setTextLength(10)
        emailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        billingEmailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

        radioGroup = rootView.findViewById(R.id.radio_group)

        checkSameBilling()
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_no -> {
                    isSameBilling = false
                    checkSameBilling()
                }
                R.id.radio_yes -> {
                    isSameBilling = true
                    checkSameBilling()
                }
            }
        }

        setupInputAddress()

        //Setup Member
        when {
            hasPwbMember() -> pwbMember?.let { pwbMember ->
                firstNameEdt.setText(pwbMember.firstname ?: "")
                lastNameEdt.setText(pwbMember.lastname ?: "")
                contactNumberEdt.setText(pwbMember.getShipping().telephone!!)
                emailEdt.setText(pwbMember.email!!)
                homeNoEdt.setText(pwbMember.getShipping().subAddress!!.houseNo!!)
                homeBuildingEdit.setText("")
                homeSoiEdt.setText("")
                homeRoadEdt.setText(pwbMember.getShipping().street!![0])

                // validate province with local db
                val province = database.getProvince(pwbMember.getShipping().regionId!!.toLong())
                if (province != null) {
                    provinceInput.setText(province.nameTh)
                    this.province = province
                    this.districts = database.getDistrictsByProvinceId(province.provinceId)
                    this.districtNameList = getDistrictNameList()
                    this.districtAdapter?.setItems(this.districtNameList)
                }

                // validate district with local db
                val district = database.getDistrict(pwbMember.getShipping().subAddress!!.districtId!!.toLong())
                if (district != null) {
                    districtInput.setText(district.nameTh)
                    this.district = district
                    this.subDistricts = database.getSubDistrictsByDistrictId(district.districtId)
                    this.subDistrictNameList = getSubDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.subDistrictNameList)
                }

                // validate sub district with local db
                val subDistrict = database.getSubDistrict(pwbMember.getShipping().subAddress!!.subDistrictId!!.toLong())
                if (subDistrict != null) {
                    subDistrictInput.setText(subDistrict.nameTh)
                    this.subDistrict = subDistrict
                    this.postcodes = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                    this.postcodeList = getPostcodeList()
                    this.postcodeAdapter?.setItems(this.postcodeList)
                }

                // validate postcode with local db
                val postcode = database.getPostcode(pwbMember.getShipping().subAddress!!.postcodeId!!.toLong())
                if (postcode != null) {
                    postcodeInput.setText(postcode.postcode.toString())
                    this.postcode = postcode
                }

                homePhoneEdt.setText(pwbMember.getShipping().telephone!!)
            }
            // t1c member
            hasMember() -> member?.let { member ->

                firstNameEdt.setText(member.getFirstName())
                lastNameEdt.setText(member.getLastName())
                contactNumberEdt.setText(member.mobilePhone)
                emailEdt.setText(member.email)

                // has address?
                if (member.addresses == null || member.addresses!!.isEmpty()) {
                    return
                }

                val memberAddress = member.addresses!![0]
                homeNoEdt.setText(memberAddress.homeNo ?: "")
                homeBuildingEdit.setText(memberAddress.building ?: "")
                homeSoiEdt.setText(memberAddress.soi ?: "")
                homeRoadEdt.setText(memberAddress.road ?: "")

                // validate province with local db
                val province = database.getProvinceByName(memberAddress.province)
                if (province != null) {
                    provinceInput.setText(province.nameTh)
                    this.province = province
                    this.districts = database.getDistrictsByProvinceId(province.provinceId)
                    this.districtNameList = getDistrictNameList()
                    this.districtAdapter?.setItems(this.districtNameList)
                }

                // validate district with local db
                val district = database.getDistrictByName(memberAddress.district)
                if (district != null) {
                    districtInput.setText(district.nameTh)
                    this.district = district
                    this.subDistricts = database.getSubDistrictsByDistrictId(district.districtId)
                    this.subDistrictNameList = getSubDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.subDistrictNameList)
                }

                // validate sub district with local db
                val subDistrict = database.getSubDistrictByName(memberAddress.subDistrict)
                if (subDistrict != null) {
                    subDistrictInput.setText(subDistrict.nameTh)
                    this.subDistrict = subDistrict
                    this.postcodes = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                    this.postcodeList = getPostcodeList()
                    this.postcodeAdapter?.setItems(this.postcodeList)
                }

                // validate postcode with local db
                val postcode = database.getPostcodeByCode(memberAddress.postcode)
                if (postcode != null) {
                    postcodeInput.setText(postcode.postcode.toString())
                    this.postcode = postcode
                }

                homePhoneEdt.setText(member.homePhone)
            }
            else -> shippingAddress?.let { shippingAddress ->
                firstNameEdt.setText(shippingAddress.firstname)
                lastNameEdt.setText(shippingAddress.lastname)
                contactNumberEdt.setText(shippingAddress.telephone)
                emailEdt.setText(shippingAddress.email)
                homeNoEdt.setText(shippingAddress.subAddress!!.houseNumber)
                homeBuildingEdit.setText(shippingAddress.subAddress!!.building)
                homeSoiEdt.setText(shippingAddress.subAddress!!.soi)
                homeRoadEdt.setText(shippingAddress.street!![0]!!)

                // validate province with local db
                val province = database.getProvinceByNameTh(shippingAddress.region)
                if (province != null) {
                    provinceInput.setText(province.nameTh)
                    this.province = province
                    this.districts = database.getDistrictsByProvinceId(province.provinceId)
                    this.districtNameList = getDistrictNameList()
                    this.districtAdapter?.setItems(this.districtNameList)
                }

                // validate district with local db
                val district = database.getDistrictByNameTh(shippingAddress.subAddress!!.district)
                if (district != null) {
                    districtInput.setText(district.nameTh)
                    this.district = district
                    this.subDistricts = database.getSubDistrictsByDistrictId(district.districtId)
                    this.subDistrictNameList = getSubDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.subDistrictNameList)
                }

                // validate sub district with local db
                val subDistrict = database.getSubDistrictByNameTh(shippingAddress.subAddress!!.subDistrict)
                if (subDistrict != null) {
                    subDistrictInput.setText(subDistrict.nameTh)
                    this.subDistrict = subDistrict
                    this.postcodes = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                    this.postcodeList = getPostcodeList()
                    this.postcodeAdapter?.setItems(this.postcodeList)
                }

                // validate postcode with local db
                val postcode = database.getPostcodeByCode(shippingAddress.subAddress!!.postcode)
                if (postcode != null) {
                    postcodeInput.setText(postcode.postcode.toString())
                    this.postcode = postcode
                }

                homePhoneEdt.setText(shippingAddress.subAddress!!.mobile)
            }
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
        val vat = total * 0.07
        totalPrice.text = getDisplayPrice(unit, (total + vat).roundToInt().toString())
        deliveryBtn.setOnClickListener {
            checkConfirm()
        }
    }

    private fun checkSameBilling() {
        if (isSameBilling) {
            billingLayout.visibility = View.GONE
        } else {
            billingLayout.visibility = View.VISIBLE
        }
    }

    private fun hasMember(): Boolean {
        return member != null
    }

    private fun hasPwbMember(): Boolean {
        return pwbMember != null
    }

    private fun setupInputAddress() {
        // setup province
        provinceAdapter = AddressAdapter(context!!, R.layout.layout_text_item, provinceNameList)
        provinceAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                provinceInput.setError(null) // clear error
                provinceInput.setText(item.second)
                provinceInput.clearAllFocus()
                province = database.getProvince(item.first)
                districtInput.setText("")
                districts = database.getDistrictsByProvinceId(item.first)
                districtNameList = getDistrictNameList()
                districtAdapter?.setItems(districtNameList)
                hideKeyboard()
            }
        })

        // setup district
        districtAdapter = AddressAdapter(context!!, R.layout.layout_text_item, districtNameList)
        districtAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                districtInput.setError(null) // clear error
                districtInput.setText(item.second)
                districtInput.clearAllFocus()
                district = database.getDistrict(item.first)
                subDistrictInput.setText("")
                subDistricts = database.getSubDistrictsByDistrictId(item.first)
                subDistrictNameList = getSubDistrictNameList()
                subDistrictAdapter?.setItems(subDistrictNameList)
                hideKeyboard()
            }

        })

        // setup sub district
        subDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, subDistrictNameList)
        subDistrictAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                subDistrictInput.setError(null) // clear error
                subDistrictInput.setText(item.second)
                subDistrictInput.clearAllFocus()
                subDistrict = database.getSubDistrict(item.first)
                postcodeInput.setText("")
                postcodes = database.getPostcodeBySubDistrictId(item.first)
                postcodeList = getPostcodeList()
                postcodeAdapter?.setItems(postcodeList)
                hideKeyboard()
            }
        })

        // setup postcode
        postcodeAdapter = AddressAdapter(context!!, R.layout.layout_text_item, postcodeList)
        postcodeAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                postcodeInput.setError(null) // clear error
                postcodeInput.setText(item.second)
                postcodeInput.clearAllFocus()
                postcode = database.getPostcode(item.first)
                hideKeyboard()
            }
        })

        // setup billing province
        billingProvinceAdapter = AddressAdapter(context!!, R.layout.layout_text_item, billingProvinceNameList)
        billingProvinceAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                billingProvinceInput.setError(null) // clear error
                billingProvinceInput.setText(item.second)
                billingProvinceInput.clearAllFocus()
                billingProvince = database.getProvince(item.first)
                billingDistrictInput.setText("")
                districts = database.getDistrictsByProvinceId(item.first)
                billingDistrictNameList = getDistrictNameList()
                billingDistrictAdapter?.setItems(billingDistrictNameList)
                hideKeyboard()
            }
        })

        // setup billing district
        billingDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, billingDistrictNameList)
        billingDistrictAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                billingDistrictInput.setError(null) // clear error
                billingDistrictInput.setText(item.second)
                billingDistrictInput.clearAllFocus()
                billingDistrict = database.getDistrict(item.first)
                billingPostcodeInput.setText("")
                subDistricts = database.getSubDistrictsByDistrictId(item.first)
                billingSubDistrictNameList = getSubDistrictNameList()
                billingSubDistrictAdapter?.setItems(billingSubDistrictNameList)
                hideKeyboard()
            }

        })

        // setup billing sub district
        billingSubDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, billingSubDistrictNameList)
        billingSubDistrictAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                billingSubDistrictInput.setError(null) // clear error
                billingSubDistrictInput.setText(item.second)
                billingSubDistrictInput.clearAllFocus()
                billingSubDistrict = database.getSubDistrict(item.first)
                billingPostcodeInput.setText("")
                postcodes = database.getPostcodeBySubDistrictId(item.first)
                billingPostcodeList = getPostcodeList()
                billingPostcodeAdapter?.setItems(billingPostcodeList)
                hideKeyboard()
            }
        })

        // setup billing postcode
        billingPostcodeAdapter = AddressAdapter(context!!, R.layout.layout_text_item, billingPostcodeList)
        billingPostcodeAdapter?.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: Pair<Long, String>) {
                billingPostcodeInput.setError(null) // clear error
                billingPostcodeInput.setText(item.second)
                billingPostcodeInput.clearAllFocus()
                billingPostcode = database.getPostcode(item.first)
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
        // setup adapter

        billingProvinceInput.setAdapter(billingProvinceAdapter)
        billingDistrictInput.setAdapter(billingDistrictAdapter)
        billingSubDistrictInput.setAdapter(billingSubDistrictAdapter)
        billingPostcodeInput.setAdapter(billingPostcodeAdapter)

        // setup onClick
        billingProvinceInput.setOnClickListener { billingProvinceInput.showDropDown() }
        billingDistrictInput.setOnClickListener { billingDistrictInput.showDropDown() }
        billingSubDistrictInput.setOnClickListener { billingSubDistrictInput.showDropDown() }
        billingPostcodeInput.setOnClickListener { billingPostcodeInput.showDropDown() }
    }

    private fun checkConfirm() {
        showProgressDialog()
        if (isSameBilling) {
            if (!hasEmptyInput()) {
                // setup value
                val shippingAddress = setupShipping(IS_SAME_BILLING)
                mProgressDialog?.dismiss()
                paymentBillingListener?.setShippingAddressInfo(shippingAddress)
            } else {
                mProgressDialog?.dismiss()
                showAlertDialog("", resources.getString(R.string.fill_in_important_information))
            }
        } else {
            if (!hasEmptyInput() && !hasBillingEmptyInput()) {
                // setup value shipping
                val billingAddress = setupBilling(IS_NOT_SAME_BILLING)
                val shippingAddress = setupShipping(IS_NOT_SAME_BILLING)
                mProgressDialog?.dismiss()
                paymentBillingListener?.setBillingAddressInfo(billingAddress)
                paymentBillingListener?.setShippingAddressInfo(shippingAddress)
            } else {
                mProgressDialog?.dismiss()
                showAlertDialog("", resources.getString(R.string.fill_in_important_information))
            }
        }
    }

    private fun setupShipping(sameBilling: Int): AddressInformation {
        firstName = firstNameEdt.getText()
        lastName = lastNameEdt.getText()
        email = emailEdt.getText()
        contactNo = contactNumberEdt.getText()
        homeNo = homeNoEdt.getText()
        homeBuilding = homeBuildingEdit.getText()
        homeSoi = homeSoiEdt.getText()
        homeRoad = homeRoadEdt.getText()
        homeProvinceId = province?.provinceId?.toString() ?: ""
        homeProvince = province?.nameTh ?: ""
        homeCountryId = province?.countryId ?: ""
        homeProvinceCode = province?.code ?: ""
        homeDistrictId = district?.districtId?.toString() ?: ""
        homeDistrict = district?.nameTh ?: ""
        homeSubDistrictId = subDistrict?.subDistrictId?.toString() ?: ""
        homeSubDistrict = subDistrict?.nameTh ?: ""
        homePostalCodeId = postcode?.id?.toString() ?: ""
        homePostalCode = postcode?.postcode?.toString() ?: ""
        homePhone = homePhoneEdt.getText()

        return AddressInformation.createAddress(
                firstName = firstName, lastName = lastName, email = email, contactNo = contactNo,
                homeNo = homeNo, homeBuilding = homeBuilding, homeSoi = homeSoi, homeRoad = homeRoad,
                homePostalCode = homePostalCode, homePhone = homePhone, provinceId = homeProvinceId,
                provinceCode = homeProvinceCode, countryId = homeCountryId, districtId = homeDistrictId,
                subDistrictId = homeSubDistrictId, postcodeId = homePostalCodeId, homeCity = homeProvince,
                homeDistrict = homeDistrict, homeSubDistrict = homeSubDistrict, sameBilling = sameBilling)
    }

    private fun setupBilling(sameBilling: Int): AddressInformation {
        firstName = billingFirstNameEdt.getText()
        lastName = billingLastNameEdt.getText()
        email = billingEmailEdt.getText()
        contactNo = billingContactNumberEdt.getText()
        homeNo = billingHomeNoEdt.getText()
        homeBuilding = billingHomeBuildingEdit.getText()
        homeSoi = billingHomeSoiEdt.getText()
        homeRoad = billingHomeRoadEdt.getText()
        homeProvinceId = billingProvince?.provinceId?.toString() ?: ""
        homeProvince = billingProvince?.nameTh ?: ""
        homeCountryId = billingProvince?.countryId ?: ""
        homeProvinceCode = billingProvince?.code ?: ""
        homeDistrictId = billingDistrict?.districtId?.toString() ?: ""
        homeDistrict = billingDistrict?.nameTh ?: ""
        homeSubDistrictId = billingSubDistrict?.subDistrictId?.toString() ?: ""
        homeSubDistrict = billingSubDistrict?.nameTh ?: ""
        homePostalCodeId = billingPostcode?.id?.toString() ?: ""
        homePostalCode = billingPostcode?.postcode?.toString() ?: ""
        homePhone = billingHomePhoneEdt.getText()

        return AddressInformation.createAddress(
                firstName = firstName, lastName = lastName, email = email, contactNo = contactNo,
                homeNo = homeNo, homeBuilding = homeBuilding, homeSoi = homeSoi, homeRoad = homeRoad,
                homePostalCode = homePostalCode, homePhone = homePhone, provinceId = homeProvinceId,
                provinceCode = homeProvinceCode, countryId = homeCountryId, districtId = homeDistrictId,
                subDistrictId = homeSubDistrictId, postcodeId = homePostalCodeId, homeCity = homeProvince,
                homeDistrict = homeDistrict, homeSubDistrict = homeSubDistrict, sameBilling = sameBilling)
    }

    private fun hasEmptyInput(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        // setup error
        firstNameEdt.setError(validator.validText(firstNameEdt.getText()))
        lastNameEdt.setError(validator.validText(lastNameEdt.getText()))
        emailEdt.setError(validator.validEmail(emailEdt.getText()))
        if (contactNumberEdt.getText().isNotEmpty()) {
            contactNumberEdt.setError(validator.validThaiPhoneNumber(contactNumberEdt.getText()))
        } else {
            contactNumberEdt.setError(validator.validThaiPhoneNumber("9999999999"))
        }
        homeNoEdt.setError(validator.validText(homeNoEdt.getText()))
        homeRoadEdt.setError(validator.validText(homeRoadEdt.getText()))
        provinceInput.setError(validator.validText(provinceInput.getText()))
        districtInput.setError(validator.validText(districtInput.getText()))
        subDistrictInput.setError(validator.validText(subDistrictInput.getText()))
        postcodeInput.setError(validator.validText(postcodeInput.getText()))

        return (firstNameEdt.getError() != null || lastNameEdt.getError() != null || emailEdt.getError() != null
                || contactNumberEdt.getError() != null || homeNoEdt.getError() != null || provinceInput.getError() != null
                || districtInput.getError() != null || subDistrictInput.getError() != null || postcodeInput.getError() != null)
    }

    private fun hasBillingEmptyInput(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        // setup error
        billingFirstNameEdt.setError(validator.validText(billingFirstNameEdt.getText()))
        billingLastNameEdt.setError(validator.validText(billingLastNameEdt.getText()))
        billingEmailEdt.setError(validator.validEmail(billingEmailEdt.getText()))
        if (billingContactNumberEdt.getText().isNotEmpty()) {
            billingContactNumberEdt.setError(validator.validThaiPhoneNumber(billingContactNumberEdt.getText()))
        } else {
            billingContactNumberEdt.setError(validator.validThaiPhoneNumber("9999999999"))
        }
        billingHomeNoEdt.setError(validator.validText(billingHomeNoEdt.getText()))
        billingHomeRoadEdt.setError(validator.validText(billingHomeRoadEdt.getText()))
        billingProvinceInput.setError(validator.validText(billingProvinceInput.getText()))
        billingDistrictInput.setError(validator.validText(billingDistrictInput.getText()))
        billingSubDistrictInput.setError(validator.validText(billingSubDistrictInput.getText()))
        billingPostcodeInput.setError(validator.validText(billingPostcodeInput.getText()))

        return (billingFirstNameEdt.getError() != null || billingLastNameEdt.getError() != null || billingEmailEdt.getError() != null
                || billingContactNumberEdt.getError() != null || billingHomeNoEdt.getError() != null || billingProvinceInput.getError() != null
                || billingDistrictInput.getError() != null || billingSubDistrictInput.getError() != null || billingPostcodeInput.getError() != null)
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

    private fun getProvinceNameList(): List<Pair<Long, String>> {
        return provinces.map { Pair(first = it.provinceId, second = it.nameTh) }
    }

    private fun getDistrictNameList(): List<Pair<Long, String>> {
        return districts.map { Pair(first = it.districtId, second = it.nameTh) }
    }

    private fun getSubDistrictNameList(): List<Pair<Long, String>> {
        return subDistricts.map { Pair(first = it.subDistrictId, second = it.nameTh) }
    }

    private fun getPostcodeList(): List<Pair<Long, String>> {
        return postcodes.map { Pair(first = it.id, second = it.postcode.toString()) }
    }
}
