package cenergy.central.com.pwb_store.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.TextUtils
import android.util.Log
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
import cenergy.central.com.pwb_store.extensions.getPostcodeList
import cenergy.central.com.pwb_store.extensions.toDistinctId
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.ValidationHelper
import cenergy.central.com.pwb_store.view.PowerBuyAutoCompleteTextStroke
import cenergy.central.com.pwb_store.view.PowerBuyEditTextBorder
import cenergy.central.com.pwb_store.view.PowerBuyIconButton
import cenergy.central.com.pwb_store.view.PowerBuyTextView
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
    private lateinit var theOneEdt: PowerBuyEditTextBorder
    private lateinit var homeNoEdt: PowerBuyEditTextBorder
    private lateinit var homeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var homeSoiEdt: PowerBuyEditTextBorder
    private lateinit var homeRoadEdt: PowerBuyEditTextBorder
    private lateinit var homePhoneEdt: PowerBuyEditTextBorder
    private lateinit var billingFirstNameEdt: PowerBuyEditTextBorder
    private lateinit var billingLastNameEdt: PowerBuyEditTextBorder
    private lateinit var billingContactNumberEdt: PowerBuyEditTextBorder
//    private lateinit var billingEmailEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeNoEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeBuildingEdit: PowerBuyEditTextBorder
    private lateinit var billingHomeSoiEdt: PowerBuyEditTextBorder
    private lateinit var billingHomeRoadEdt: PowerBuyEditTextBorder
    private lateinit var companyEdt: PowerBuyEditTextBorder
    private lateinit var taxIdEdt: PowerBuyEditTextBorder
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var deliveryBtn: PowerBuyIconButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioTaxGroup: RadioGroup

    private lateinit var provinceInput: PowerBuyAutoCompleteTextStroke
    private lateinit var districtInput: PowerBuyAutoCompleteTextStroke
    private lateinit var subDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var postcodeInput: PowerBuyAutoCompleteTextStroke

    private lateinit var billingProvinceInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingSubDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var billingPostcodeInput: PowerBuyAutoCompleteTextStroke

    private lateinit var billingLayout: LinearLayout
    private lateinit var taxInvoiceLayout: LinearLayout

    private var mProgressDialog: ProgressDialog? = null

    // data
    private lateinit var preferenceManager: PreferenceManager
    private var defaultLanguage = AppLanguage.TH.key
    private val database by lazy { RealmController.getInstance() }
    private var cartItemList: List<CartItem> = listOf()
    private var shippingAddress: AddressInformation? = null
    private var billingAddress: AddressInformation? = null
    private lateinit var paymentProtocol: PaymentProtocol
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
    private var company: String = ""
    private var vatId: String = ""
    private var isSameBilling = true
    private var isRequireTaxInvoice = false

    // shipping data address
    private var provinceList = listOf<Province>()
    private var districtList = listOf<District>()
    private var subDistrictList = listOf<SubDistrict>()
    private var postcodeList = listOf<Postcode>()

    // billing data address
    private var billingDistrictList = listOf<District>()
    private var billingSubDistrictList = listOf<SubDistrict>()
    private var billingPostcodeList = listOf<Postcode>()

    // data shipping
    private var province: Province? = null
    private var district: District? = null
    private var subDistrict: SubDistrict? = null
    private var postcode: Postcode? = null

    // data billing
    private var billingProvince: Province? = null
    private var billingDistrict: District? = null
    private var billingSubDistrict: SubDistrict? = null
    private var billingPostcode: Postcode? = null
    // adapter
    private lateinit var provinceAdapter: AddressAdapter
    private lateinit var districtAdapter: AddressAdapter
    private lateinit var subDistrictAdapter: AddressAdapter
    private lateinit var postcodeAdapter: AddressAdapter
    private lateinit var billingProvinceAdapter: AddressAdapter
    private lateinit var billingDistrictAdapter: AddressAdapter
    private lateinit var billingSubDistrictAdapter: AddressAdapter
    private lateinit var billingPostcodeAdapter: AddressAdapter

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
        preferenceManager = PreferenceManager(context)
        defaultLanguage = preferenceManager.getDefaultLanguage()
        paymentProtocol = context as PaymentProtocol
        paymentBillingListener = context as PaymentBillingListener
        cartItemList = paymentProtocol.getItems()
        shippingAddress = paymentProtocol.getShippingAddress()
        billingAddress = paymentProtocol.getBillingAddress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferenceManager = context?.let { PreferenceManager(it) }
        cartId = preferenceManager?.cartId
        member = arguments?.getParcelable(ARG_MEMBER)
        pwbMemberIndex = arguments?.getInt(ARG_MEMBER_INDEX)
        pwbMemberIndex?.let { pwbMember = paymentProtocol.getPWBMemberByIndex(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment_billing, container, false)
        setupView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputAddress()
        loadProvinceList() // on start
        setupCartItems()
    }

    private fun setupCartItems() {
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

    private fun verifyMember() {
        //Setup Member
        when {
            (shippingAddress != null) -> {
                handleCacheMember()
                dismissProgressDialog()
            }
            hasPwbMember() -> handlePwbMember()
            hasMember() -> handleT1CMember()
            else -> dismissProgressDialog()
        }
    }

    private fun handleCacheMember() {
        if (shippingAddress != null) {
            firstNameEdt.setText(shippingAddress!!.firstname)
            lastNameEdt.setText(shippingAddress!!.lastname)
            contactNumberEdt.setText(shippingAddress!!.telephone)
            emailEdt.setText(shippingAddress!!.email)
            homeNoEdt.setText(shippingAddress!!.subAddress?.houseNumber ?: "")
            homeBuildingEdit.setText(shippingAddress!!.subAddress?.building ?: "")
            homeSoiEdt.setText(shippingAddress!!.subAddress?.soi ?: "")
            homeRoadEdt.setText(shippingAddress!!.subAddress?.addressLine ?: "")
            homePhoneEdt.setText(shippingAddress!!.subAddress?.mobile ?: "")

            // has input about vat?
            val vatId = shippingAddress!!.vatId
            if (vatId.isNotBlank()) {
                radioTaxGroup.check(R.id.radio_tax_yes)
                companyEdt.setText(shippingAddress!!.company)
                taxIdEdt.setText(vatId)
            }

            // validate province with local db
            val province = database.getProvince(shippingAddress!!.regionId)
            if (province != null) {
                this.province = province
                this.districtList = database.getDistrictsByProvinceId(province.provinceId)
                this.districtAdapter.setItems(this.districtList)
                provinceInput.setText(province.name)
                districtInput.setText("")
                subDistrictInput.setText("")
                postcodeInput.setText("")
                districtInput.setEnableInput(true)
                subDistrictInput.setEnableInput(false)
                postcodeInput.setEnableInput(false)
            } else {
                return
            }

            // validate district with local db
            val district = database.getDistrict(shippingAddress!!.subAddress?.districtId)
            if (district != null) {
                this.district = district
                this.subDistrictList = database.getSubDistrictsByDistrictId(district.districtId)
                this.subDistrictAdapter.setItems(this.subDistrictList.toDistinctId())
                districtInput.setText(district.name)
                subDistrictInput.setText("")
                postcodeInput.setText("")
                subDistrictInput.setEnableInput(true)
                postcodeInput.setEnableInput(false)
            } else {
                return
            }

            // validate sub district with local db
            val subDistrict = database.getSubDistrict(shippingAddress!!.subAddress?.subDistrictId)
            if (subDistrict != null) {
                this.subDistrict = subDistrict
                this.postcodeList = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                this.postcodeAdapter.setItems(this.postcodeList)
                subDistrictInput.setText(subDistrict.name)
                postcodeInput.setText("")
                postcodeInput.setEnableInput(true)
            } else {
                return
            }

            // validate postcode with local db
            val postcode = database.getPostcode(shippingAddress!!.subAddress?.postcodeId)
            if (postcode != null) {
                postcodeInput.setText(postcode.postcode)
                this.postcode = postcode
            }

            /**
             * Has billing address?
             * Is shipping address not same billing?
             */
            if (billingAddress != null && shippingAddress!!.sameBilling == IS_NOT_SAME_BILLING) {
                radioGroup.check(R.id.radio_no)
                setupBillingInput(billingAddress!!)
            }
        }
    }

    private fun setupBillingInput(billingAddress: AddressInformation) {
        billingFirstNameEdt.setText(billingAddress.firstname)
        billingLastNameEdt.setText(billingAddress.lastname)
        billingContactNumberEdt.setText(billingAddress.telephone)
//        billingEmailEdt.setText(billingAddress.email)
        billingHomeNoEdt.setText(billingAddress.subAddress?.houseNumber ?: "")
        billingHomeBuildingEdit.setText(billingAddress.subAddress?.building ?: "")
        billingHomeSoiEdt.setText(billingAddress.subAddress?.soi ?: "")
        billingHomeRoadEdt.setText(billingAddress.subAddress?.addressLine ?: "")

        // has input about vat?
        val vatId = billingAddress.vatId
        if (vatId.isNotBlank()) {
            radioTaxGroup.check(R.id.radio_tax_yes)
            companyEdt.setText(billingAddress.company)
            taxIdEdt.setText(vatId)
        }

        // validate province with local db
        val province = database.getProvince(billingAddress.regionId)
        if (province != null) {
            this.billingProvince = province
            this.billingDistrictList = database.getDistrictsByProvinceId(province.provinceId)
            this.billingDistrictAdapter.setItems(this.billingDistrictList)
            billingProvinceInput.setText(province.name)
            billingDistrictInput.setText("")
            billingSubDistrictInput.setText("")
            billingPostcodeInput.setText("")
            billingDistrictInput.setEnableInput(true)
            billingSubDistrictInput.setEnableInput(false)
            billingPostcodeInput.setEnableInput(false)
        } else {
            return
        }

        // validate district with local db
        val district = database.getDistrict(billingAddress.subAddress?.districtId)
        if (district != null) {
            this.billingDistrict = district
            this.billingSubDistrictList = database.getSubDistrictsByDistrictId(district.districtId)
            this.billingSubDistrictAdapter.setItems(this.billingSubDistrictList.toDistinctId())
            billingDistrictInput.setText(district.name)
            billingSubDistrictInput.setText("")
            billingPostcodeInput.setText("")
            billingSubDistrictInput.setEnableInput(true)
            billingPostcodeInput.setEnableInput(false)
        } else {
            return
        }

        // validate sub district with local db
        val subDistrict = database.getSubDistrict(billingAddress.subAddress?.subDistrictId)
        if (subDistrict != null) {
            this.billingSubDistrict = subDistrict
            this.billingPostcodeList = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
            this.billingPostcodeAdapter.setItems(this.billingPostcodeList)
            billingSubDistrictInput.setText(subDistrict.name)
            billingPostcodeInput.setText("")
            billingPostcodeInput.setEnableInput(true)
        } else {
            return
        }

        // validate postcode with local db
        val postcode = database.getPostcode(billingAddress.subAddress?.postcodeId)
        if (postcode != null) {
            billingPostcodeInput.setText(postcode.postcode)
            this.billingPostcode = postcode
        }
    }

    private fun handlePwbMember() {
        if (pwbMember == null) {
            return
        }

        val member = pwbMember!!
        firstNameEdt.setText(member.firstname ?: "")
        lastNameEdt.setText(member.lastname ?: "")
        contactNumberEdt.setText(member.telephone ?: "")
        emailEdt.setText(member.email ?: "")
        homeNoEdt.setText(member.subAddress?.houseNo ?: "")
        homeBuildingEdit.setText(member.subAddress?.building ?: "")
        homeSoiEdt.setText(member.subAddress?.soi ?: "")
        homeRoadEdt.setText(member.subAddress?.street ?: "")
        homePhoneEdt.setText(member.telephone ?: "")

        // validate province with local db
        val provinceId = member.regionId
        if (provinceId != null) {
            val province = database.getProvince(provinceId.toString())
            if (province != null) {
                this.province = province
                provinceInput.setText(province.name)
                districtInput.setText("")
                subDistrictInput.setText("")
                postcodeInput.setText("")
                districtInput.setEnableInput(true)
                subDistrictInput.setEnableInput(false)
                postcodeInput.setEnableInput(false)

                val subAddress = member.subAddress ?: return

                // load district and verify customer address
                val districtId = subAddress.districtId
                val subDistrictId = subAddress.subDistrictId
                val postcodeStr = member.postcode ?: ""
                if (districtId.isNotBlank()) {
                    if (subDistrictId.isNotBlank()) {
                        memberLoadDistrict(province.provinceId, districtId, subDistrictId, postcodeStr)
                    } else {
                        memberLoadDistrict(province.provinceId, districtId)
                    }
                }
            }
            dismissProgressDialog()
        } else {
            dismissProgressDialog()
        }
    }

    private fun handleT1CMember() {
        if (member == null) {
            return
        }

        val t1cMember = member!!
        firstNameEdt.setText(t1cMember.getFirstName())
        lastNameEdt.setText(t1cMember.getLastName())
        contactNumberEdt.setText(t1cMember.mobilePhone)
        emailEdt.setText(t1cMember.email ?: "")
        theOneEdt.setText(t1cMember.cardNo)
        homePhoneEdt.setText(t1cMember.homePhone)

        // has address?
        if (t1cMember.addresses != null && t1cMember.addresses!!.isNotEmpty()) {
            val memberAddress = t1cMember.addresses!![0]
            homeNoEdt.setText(memberAddress.homeNo ?: "")
            homeBuildingEdit.setText(memberAddress.building ?: "")
            homeSoiEdt.setText(memberAddress.soi ?: "")
            homeRoadEdt.setText(memberAddress.road ?: "")

            // validate province with local db
            val province = database.getProvinceByName(memberAddress.province)
            if (province != null) {
                this.province = province
                provinceInput.setText(province.name)
                districtInput.setText("")
                subDistrictInput.setText("")
                postcodeInput.setText("")
                districtInput.setEnableInput(true)
                subDistrictInput.setEnableInput(false)
                postcodeInput.setEnableInput(false)

                // load district and verify customer address
                val districtName = memberAddress.district ?: ""
                val subDistrictName = memberAddress.subDistrict ?: ""
                val postcodeStr = memberAddress.postcode ?: ""
                if (districtName.isNotBlank()) {
                    if (subDistrictName.isNotBlank()) {
                        memberLoadDistrict(province.provinceId, districtName, subDistrictName,
                                postcodeStr, false)
                    } else {
                        memberLoadDistrict(province.provinceId, districtName)
                    }
                }
            }
            dismissProgressDialog()
        } else {
            dismissProgressDialog()
        }

    }

    private fun setupView(rootView: View) {
        // Shipping address
        firstNameEdt = rootView.findViewById(R.id.first_name_payment)
        lastNameEdt = rootView.findViewById(R.id.last_name_payment)
        contactNumberEdt = rootView.findViewById(R.id.contact_number_payment)
        emailEdt = rootView.findViewById(R.id.email_payment)
        theOneEdt = rootView.findViewById(R.id.t1_member_no_payment)
        homeNoEdt = rootView.findViewById(R.id.house_no_payment)
        homeBuildingEdit = rootView.findViewById(R.id.place_or_building_payment)
        homeSoiEdt = rootView.findViewById(R.id.soi_payment)
        homeRoadEdt = rootView.findViewById(R.id.street_payment)
        homePhoneEdt = rootView.findViewById(R.id.tell_payment)
        // Setup view input address
        provinceInput = rootView.findViewById(R.id.input_province)
        districtInput = rootView.findViewById(R.id.input_district)
        subDistrictInput = rootView.findViewById(R.id.input_sub_district)
        postcodeInput = rootView.findViewById(R.id.input_postcode)

        // Tax invoice layout
        taxInvoiceLayout = rootView.findViewById(R.id.tax_invoice_layout)
        companyEdt = rootView.findViewById(R.id.input_company)
        taxIdEdt = rootView.findViewById(R.id.input_tax_id)

        // Billing address
        billingLayout = rootView.findViewById(R.id.billing_address_layout_payment)
        billingFirstNameEdt = rootView.findViewById(R.id.first_name_billing)
        billingLastNameEdt = rootView.findViewById(R.id.last_name_billing)
        billingContactNumberEdt = rootView.findViewById(R.id.contact_number_billing)
//        billingEmailEdt = rootView.findViewById(R.id.email_billing)
        billingHomeNoEdt = rootView.findViewById(R.id.billing_house_no_payment)
        billingHomeBuildingEdit = rootView.findViewById(R.id.billing_place_or_building_payment)
        billingHomeSoiEdt = rootView.findViewById(R.id.billing_soi_payment)
        billingHomeRoadEdt = rootView.findViewById(R.id.billing_street_payment)
        // Setup view input billing address
        billingProvinceInput = rootView.findViewById(R.id.billing_input_province)
        billingDistrictInput = rootView.findViewById(R.id.billing_input_district)
        billingSubDistrictInput = rootView.findViewById(R.id.billing_input_sub_district)
        billingPostcodeInput = rootView.findViewById(R.id.billing_input_postcode)

        recycler = rootView.findViewById(R.id.recycler_product_list_payment)
        totalPrice = rootView.findViewById(R.id.txt_total_price_payment_description)
        deliveryBtn = rootView.findViewById(R.id.paymentButton)

        // Set Input type
        contactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        contactNumberEdt.setTextLength(10)
        billingContactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        billingContactNumberEdt.setTextLength(10)
        homePhoneEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        homePhoneEdt.setTextLength(10)
        emailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
//        billingEmailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        taxIdEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        taxIdEdt.setTextLength(13)

        //set T1 icon
        theOneEdt.setDrawableStart(R.drawable.ic_the1)

        radioGroup = rootView.findViewById(R.id.radio_group)
        radioTaxGroup = rootView.findViewById(R.id.radio_tax_group)

        checkRequireTaxInvoice()
        radioTaxGroup.setOnCheckedChangeListener { radioTaxGroup, _ ->
            when (radioTaxGroup.checkedRadioButtonId) {
                R.id.radio_tax_no -> {
                    isRequireTaxInvoice = false
                    checkRequireTaxInvoice()
                }
                R.id.radio_tax_yes -> {
                    isRequireTaxInvoice = true
                    checkRequireTaxInvoice()
                }
            }
        }

        checkSameBilling()
        radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
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
    }

    private fun checkSameBilling() {
        if (isSameBilling) {
            billingLayout.visibility = View.GONE
        } else {
            billingLayout.visibility = View.VISIBLE
        }
    }

    private fun checkRequireTaxInvoice() {
        if (!isRequireTaxInvoice) {
            taxInvoiceLayout.visibility = View.GONE
        } else {
            taxInvoiceLayout.visibility = View.VISIBLE
        }
    }

    private fun hasMember(): Boolean {
        return member != null
    }

    private fun hasPwbMember(): Boolean {
        return pwbMember != null
    }

    private fun setupInputAddress() {
        // setup dropdown
        // setup province input
        initProvinceShippingInput()
        initProvinceBillingInput()

        // setup district
        initDistrictShippingInput()
        initDistrictBillingInput()

        // setup sub district
        initSubDistrictShippingInput()
        initSubDistrictBillingInput()

        // setup postcode
        initPostcodeShippingInput()
        initPostcodeBillingInput()

        // setup adapter
        provinceInput.setAdapter(provinceAdapter)
        districtInput.setAdapter(districtAdapter)
        subDistrictInput.setAdapter(subDistrictAdapter)
        postcodeInput.setAdapter(postcodeAdapter)

        // setup onClick
        provinceInput.setEnableInput(true)
        districtInput.setEnableInput(false)
        subDistrictInput.setEnableInput(false)
        postcodeInput.setEnableInput(false)

        // setup adapter
        billingProvinceInput.setAdapter(billingProvinceAdapter)
        billingDistrictInput.setAdapter(billingDistrictAdapter)
        billingSubDistrictInput.setAdapter(billingSubDistrictAdapter)
        billingPostcodeInput.setAdapter(billingPostcodeAdapter)

        // setup onClick
        billingProvinceInput.setEnableInput(true)
        billingDistrictInput.setEnableInput(false)
        billingSubDistrictInput.setEnableInput(false)
        billingPostcodeInput.setEnableInput(false)
    }

    private fun checkConfirm() {
        showProgressDialog()
        if (isSameBilling) {
            if (!hasEmptyInput()) {
                // setup value
                val shippingAddress = createShipping(IS_SAME_BILLING)
                mProgressDialog?.dismiss()
                paymentBillingListener?.setShippingAddressInfo(shippingAddress)
            } else {
                mProgressDialog?.dismiss()
                showAlertDialog("", resources.getString(R.string.fill_in_important_information))
            }
        } else {
            if (!hasEmptyInput() && !hasBillingEmptyInput()) {
                // setup value shipping
                val billingAddress = createBilling(IS_NOT_SAME_BILLING)
                val shippingAddress = createShipping(IS_NOT_SAME_BILLING)
                mProgressDialog?.dismiss()
                paymentBillingListener?.setBillingAddressInfo(billingAddress)
                paymentBillingListener?.setShippingAddressInfo(shippingAddress)
            } else {
                mProgressDialog?.dismiss()
                showAlertDialog("", resources.getString(R.string.fill_in_important_information))
            }
        }
    }

    private fun createShipping(sameBilling: Int): AddressInformation {
        firstName = firstNameEdt.getText()
        lastName = lastNameEdt.getText()
        email = emailEdt.getText()
        contactNo = contactNumberEdt.getText()
        homeNo = homeNoEdt.getText()
        homeBuilding = homeBuildingEdit.getText()
        homeSoi = homeSoiEdt.getText()
        homeRoad = homeRoadEdt.getText()
        homeProvinceId = province?.provinceId ?: ""
        homeProvince = province?.name ?: ""
        homeCountryId = province?.countryId ?: ""
        homeProvinceCode = province?.code ?: ""
        homeDistrictId = district?.districtId ?: ""
        homeDistrict = district?.name ?: ""
        homeSubDistrictId = subDistrict?.subDistrictId ?: ""
        homeSubDistrict = subDistrict?.name ?: ""
        homePostalCodeId = postcode?.postcodeId ?: ""
        homePostalCode = postcode?.postcode ?: ""
        homePhone = homePhoneEdt.getText()
        company = if (isRequireTaxInvoice) companyEdt.getText() else ""
        vatId = if (isRequireTaxInvoice) taxIdEdt.getText() else ""

        return AddressInformation.createAddress(
                firstName = firstName, lastName = lastName, email = email, contactNo = contactNo,
                homeNo = homeNo, homeBuilding = homeBuilding, homeSoi = homeSoi, homeRoad = homeRoad,
                homePostalCode = homePostalCode, homePhone = homePhone, provinceId = homeProvinceId,
                provinceCode = homeProvinceCode, countryId = homeCountryId, districtId = homeDistrictId,
                subDistrictId = homeSubDistrictId, postcodeId = homePostalCodeId, homeCity = homeProvince,
                homeDistrict = homeDistrict, homeSubDistrict = homeSubDistrict,
                sameBilling = sameBilling,
                company = if (sameBilling == IS_SAME_BILLING) company else "",
                vatId = if (sameBilling == IS_SAME_BILLING) vatId else "")
    }

    private fun createBilling(sameBilling: Int): AddressInformation {
        firstName = billingFirstNameEdt.getText()
        lastName = billingLastNameEdt.getText()
//        email = billingEmailEdt.getText()
        contactNo = billingContactNumberEdt.getText()
        homeNo = billingHomeNoEdt.getText()
        homeBuilding = billingHomeBuildingEdit.getText()
        homeSoi = billingHomeSoiEdt.getText()
        homeRoad = billingHomeRoadEdt.getText()
        homeProvinceId = billingProvince?.provinceId ?: ""
        homeProvince = billingProvince?.name ?: ""
        homeCountryId = billingProvince?.countryId ?: ""
        homeProvinceCode = billingProvince?.code ?: ""
        homeDistrictId = billingDistrict?.districtId ?: ""
        homeDistrict = billingDistrict?.name ?: ""
        homeSubDistrictId = billingSubDistrict?.subDistrictId ?: ""
        homeSubDistrict = billingSubDistrict?.name ?: ""
        homePostalCodeId = billingPostcode?.postcodeId ?: ""
        homePostalCode = billingPostcode?.postcode ?: ""
        company = if (isRequireTaxInvoice) companyEdt.getText() else ""
        vatId = if (isRequireTaxInvoice) taxIdEdt.getText() else ""

        return AddressInformation.createAddress(
                firstName = firstName, lastName = lastName, email = "", contactNo = contactNo,
                homeNo = homeNo, homeBuilding = homeBuilding, homeSoi = homeSoi, homeRoad = homeRoad,
                homePostalCode = homePostalCode, homePhone = "", provinceId = homeProvinceId,
                provinceCode = homeProvinceCode, countryId = homeCountryId, districtId = homeDistrictId,
                subDistrictId = homeSubDistrictId, postcodeId = homePostalCodeId, homeCity = homeProvince,
                homeDistrict = homeDistrict, homeSubDistrict = homeSubDistrict,
                sameBilling = null, company = company, vatId = vatId)
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
            contactNumberEdt.setError(getString(R.string.error_form_phone_number_invalid))
        }
        homeNoEdt.setError(validator.validText(homeNoEdt.getText()))
        homeRoadEdt.setError(validator.validText(homeRoadEdt.getText()))
        provinceInput.setError(validator.validText(provinceInput.getText()))
        districtInput.setError(validator.validText(districtInput.getText()))
        subDistrictInput.setError(validator.validText(subDistrictInput.getText()))
        postcodeInput.setError(validator.validText(postcodeInput.getText()))

        return (firstNameEdt.getError() != null || lastNameEdt.getError() != null || emailEdt.getError() != null
                || contactNumberEdt.getError() != null || homeNoEdt.getError() != null || provinceInput.getError() != null
                || districtInput.getError() != null || subDistrictInput.getError() != null || postcodeInput.getError() != null
                || homeRoadEdt.getError() != null || hasRequireTaxInvoice())
    }

    private fun hasBillingEmptyInput(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        // setup error
        billingFirstNameEdt.setError(validator.validText(billingFirstNameEdt.getText()))
        billingLastNameEdt.setError(validator.validText(billingLastNameEdt.getText()))
//        billingEmailEdt.setError(validator.validEmail(billingEmailEdt.getText()))
        if (billingContactNumberEdt.getText().isNotEmpty()) {
            billingContactNumberEdt.setError(validator.validThaiPhoneNumber(billingContactNumberEdt.getText()))
        } else {
            billingContactNumberEdt.setError(getString(R.string.error_form_phone_number_invalid))
        }
        billingHomeNoEdt.setError(validator.validText(billingHomeNoEdt.getText()))
        billingHomeRoadEdt.setError(validator.validText(billingHomeRoadEdt.getText()))
        billingProvinceInput.setError(validator.validText(billingProvinceInput.getText()))
        billingDistrictInput.setError(validator.validText(billingDistrictInput.getText()))
        billingSubDistrictInput.setError(validator.validText(billingSubDistrictInput.getText()))
        billingPostcodeInput.setError(validator.validText(billingPostcodeInput.getText()))

        return (billingFirstNameEdt.getError() != null || billingLastNameEdt.getError() != null
                || billingContactNumberEdt.getError() != null || billingHomeNoEdt.getError() != null || billingProvinceInput.getError() != null
                || billingDistrictInput.getError() != null || billingSubDistrictInput.getError() != null || billingPostcodeInput.getError() != null
                || billingHomeRoadEdt.getError() != null)
    }

    private fun hasRequireTaxInvoice(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        taxIdEdt.setError(validator.validText(taxIdEdt.getText()))
        return if (isRequireTaxInvoice) {
            taxIdEdt.getError() != null
        } else {
            false
        }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, _ -> dialog.dismiss() }

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
            if (mProgressDialog!!.isShowing) return

            mProgressDialog?.show()
        }
    }

    private fun dismissProgressDialog() {
        if (mProgressDialog != null && isAdded) {
            mProgressDialog!!.dismiss()
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

    private fun loadProvinceList() {
        context?.let {
            showProgressDialog()
            HttpManagerMagento.getInstance(it).getProvinces(false, object : ApiResponseCallback<List<Province>> {
                override fun success(response: List<Province>?) {
                    response?.let { provinceList ->
                        // update dropdown province
                        this@PaymentBillingFragment.provinceList = provinceList
                        provinceAdapter.setItems(provinceList)
                        billingProvinceAdapter.setItems(provinceList)
                    }
                    verifyMember() // check has member information?
//                    dismissProgressDialog()
                }

                override fun failure(error: APIError) {
                    Log.e("PaymentBillingFragment", "can't get province list")
                    dismissProgressDialog()
                }
            })
        }
    }

    private fun loadDistrictList(province: Province, isShipping: Boolean = true) {
        context?.let {
            showProgressDialog()
            HttpManagerMagento.getInstance(it).getDistricts(province.provinceId, object : ApiResponseCallback<List<District>> {
                override fun success(response: List<District>?) {
                    response?.let { districtList ->
                        if (isShipping) {
                            this@PaymentBillingFragment.districtList = districtList
                            this@PaymentBillingFragment.districtAdapter.setItems(districtList)
                        } else {
                            this@PaymentBillingFragment.billingDistrictList = districtList
                            this@PaymentBillingFragment.billingDistrictAdapter.setItems(districtList)
                        }
                        dismissProgressDialog()
                    }
                }

                override fun failure(error: APIError) {
                    Log.e("PaymentBillingFragment", "can't get district list")
                    dismissProgressDialog()
                }
            })
        }
    }

    private fun loadSubDistrictList(district: District, isShipping: Boolean = true) {
        context?.let {
            showProgressDialog()
            HttpManagerMagento.getInstance(it).getSubDistricts(district.provinceId, district.districtId,
                    object : ApiResponseCallback<List<SubDistrict>> {
                        override fun success(response: List<SubDistrict>?) {
                            response?.let { subDistrictList ->
                                if (isShipping) {
                                    this@PaymentBillingFragment.subDistrictList = subDistrictList
                                    this@PaymentBillingFragment.subDistrictAdapter.setItems(subDistrictList.toDistinctId())
                                } else {
                                    this@PaymentBillingFragment.billingSubDistrictList = subDistrictList
                                    this@PaymentBillingFragment.billingSubDistrictAdapter.setItems(subDistrictList.toDistinctId())
                                }
                                dismissProgressDialog()
                            }
                        }

                        override fun failure(error: APIError) {
                            Log.e("PaymentBillingFragment", "can't get district list")
                            dismissProgressDialog()
                        }
                    })
        }
    }

    private fun initProvinceShippingInput() {
        provinceAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        provinceAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedProvince = item as Province
                this@PaymentBillingFragment.province = selectedProvince
                provinceInput.setError(null) // clear error
                provinceInput.setText(selectedProvince.name)
                provinceInput.clearAllFocus()
                districtInput.setText("")
                subDistrictInput.setText("")
                postcodeInput.setText("")
                districtInput.setEnableInput(true)
                subDistrictInput.setEnableInput(false)
                postcodeInput.setEnableInput(false)
                loadDistrictList(selectedProvince) // load district
                hideKeyboard()
            }
        })
    }

    private fun initProvinceBillingInput() {
        billingProvinceAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        billingProvinceAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedProvince = item as Province
                this@PaymentBillingFragment.billingProvince = selectedProvince
                billingProvinceInput.setError(null) // clear error
                billingProvinceInput.setText(selectedProvince.name)
                billingProvinceInput.clearAllFocus()
                billingDistrictInput.setText("")
                billingSubDistrictInput.setText("")
                billingPostcodeInput.setText("")
                billingDistrictInput.setEnableInput(true)
                billingSubDistrictInput.setEnableInput(false)
                billingPostcodeInput.setEnableInput(false)
                loadDistrictList(selectedProvince, false) // load district
                hideKeyboard()
            }
        })
    }

    private fun initDistrictShippingInput() {
        districtAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        districtAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedDistrict = item as District
                this@PaymentBillingFragment.district = selectedDistrict
                districtInput.setError(null) // clear error
                districtInput.setText(selectedDistrict.name)
                districtInput.clearAllFocus()
                subDistrictInput.setText("")
                subDistrictInput.setEnableInput(true)
                postcodeInput.setEnableInput(false)
                loadSubDistrictList(selectedDistrict)
                hideKeyboard()
            }

        })
    }

    private fun initDistrictBillingInput() {
        billingDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        billingDistrictAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedDistrict = item as District
                this@PaymentBillingFragment.billingDistrict = selectedDistrict

                billingDistrictInput.setError(null) // clear error
                billingDistrictInput.setText(selectedDistrict.name)
                billingDistrictInput.clearAllFocus()
                billingSubDistrictInput.setText("")
                billingSubDistrictInput.setEnableInput(true)
                billingPostcodeInput.setEnableInput(false)
                loadSubDistrictList(selectedDistrict, false)
                hideKeyboard()
            }

        })
    }

    private fun initSubDistrictShippingInput() {
        subDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        subDistrictAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedSubDistrict = item as SubDistrict
                this@PaymentBillingFragment.subDistrict = selectedSubDistrict

                subDistrictInput.setError(null) // clear error
                subDistrictInput.setText(selectedSubDistrict.name)
                subDistrictInput.clearAllFocus()
                postcodeInput.setText("")
                postcodeInput.setEnableInput(true)
                postcodeList = subDistrictList.getPostcodeList(selectedSubDistrict.subDistrictId)
                postcodeAdapter.setItems(postcodeList)
                hideKeyboard()
            }
        })
    }

    private fun initSubDistrictBillingInput() {
        billingSubDistrictAdapter = AddressAdapter(context!!, R.layout.layout_text_item, arrayListOf())
        billingSubDistrictAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedSubDistrict = item as SubDistrict
                this@PaymentBillingFragment.billingSubDistrict = selectedSubDistrict

                billingSubDistrictInput.setError(null) // clear error
                billingSubDistrictInput.setText(selectedSubDistrict.name)
                billingSubDistrictInput.clearAllFocus()
                billingPostcodeInput.setText("")
                billingPostcodeInput.setEnableInput(true)
                billingPostcodeList = billingSubDistrictList.getPostcodeList(selectedSubDistrict.subDistrictId)
                billingPostcodeAdapter.setItems(billingPostcodeList)
                hideKeyboard()
            }
        })
    }

    private fun initPostcodeShippingInput() {
        postcodeAdapter = AddressAdapter(context!!, R.layout.layout_text_item, postcodeList)
        postcodeAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedPostcode = item as Postcode
                this@PaymentBillingFragment.postcode = selectedPostcode

                postcodeInput.setError(null) // clear error
                postcodeInput.setText(selectedPostcode.postcode)
                postcodeInput.clearAllFocus()
                hideKeyboard()
            }
        })
    }

    private fun initPostcodeBillingInput() {
        billingPostcodeAdapter = AddressAdapter(context!!, R.layout.layout_text_item, billingPostcodeList)
        billingPostcodeAdapter.setCallback(object : AddressAdapter.FilterClickListener {
            override fun onItemClickListener(item: AddressAdapter.AddressItem) {
                val selectedPostcode = item as Postcode
                this@PaymentBillingFragment.billingPostcode = selectedPostcode

                billingPostcodeInput.setError(null) // clear error
                billingPostcodeInput.setText(selectedPostcode.postcode)
                billingPostcodeInput.clearAllFocus()
                hideKeyboard()
            }
        })
    }

    // region member find address
    private fun memberLoadDistrict(provinceId: String, districtStr: String,
                                   subDistrictStr: String = "", postcodeStr: String = "", isPwbMember: Boolean = true) {
        context?.let {
            showProgressDialog()
            HttpManagerMagento.getInstance(it).getDistricts(provinceId, object : ApiResponseCallback<List<District>> {
                override fun success(response: List<District>?) {
                    response?.let { districtList ->
                        this@PaymentBillingFragment.districtList = districtList
                        this@PaymentBillingFragment.districtAdapter.setItems(districtList)

                        val district = if (isPwbMember) {
                            districtList.find { district -> district.districtId == districtStr } //by districtId
                        } else {
                            districtList.find { district -> district.name == districtStr } //by district.name
                        }
                        memberSetDistrict(provinceId, district, subDistrictStr, postcodeStr, isPwbMember) // set district
                    }
                }

                override fun failure(error: APIError) {
                    Log.e("PaymentBillingFragment", "member can't get district list")
                    dismissProgressDialog()
                }
            })
        }
    }

    private fun memberSetDistrict(provinceId: String, district: District?, subDistrictStr: String,
                                  postcodeStr: String, isPwbMember: Boolean) {
        // found district?
        if (district != null) {
            this@PaymentBillingFragment.district = district
            districtInput.setText(district.name)
            subDistrictInput.setText("")
            postcodeInput.setText("")
            subDistrictInput.setEnableInput(true)
            postcodeInput.setEnableInput(false)

            if (subDistrictStr.isNotBlank()) {
                memberLoadSubDistrict(provinceId, district.districtId, subDistrictStr, postcodeStr, isPwbMember)
            } else {
                dismissProgressDialog()
            }
        } else {
            dismissProgressDialog()
        }
    }

    private fun memberLoadSubDistrict(provinceId: String, districtId: String,
                                      subDistrictStr: String = "", postcodeStr: String, isPwbMember: Boolean) {
        context?.let {
            showProgressDialog()
            HttpManagerMagento.getInstance(it).getSubDistricts(provinceId, districtId, object : ApiResponseCallback<List<SubDistrict>> {
                override fun success(response: List<SubDistrict>?) {
                    response?.let { subDistrictList ->
                        this@PaymentBillingFragment.subDistrictList = subDistrictList
                        this@PaymentBillingFragment.subDistrictAdapter.setItems(subDistrictList.toDistinctId())

                        val subDistrict = if (isPwbMember) {
                            subDistrictList.find { subDistrict -> subDistrict.subDistrictId == subDistrictStr } // by districtId
                        } else {
                            subDistrictList.find { subDistrict -> subDistrict.name == subDistrictStr } // by district.name
                        }
                        subDistrict?.let { it1 -> memberSetSubDistrict(it1, response, postcodeStr) } // set subDistrict
                    }
                }

                override fun failure(error: APIError) {
                    Log.e("PaymentBillingFragment", "member can't get district list")
                    dismissProgressDialog()
                }
            })
        }
    }

    private fun memberSetSubDistrict(subDistrict: SubDistrict?, subDistricts: List<SubDistrict>, postcodeStr: String) {
        if (subDistrict != null) {
            this.subDistrict = subDistrict
            subDistrictInput.setText(subDistrict.name)

            // setup member postcode
            val postcodeData = subDistricts.getPostcodeList(subDistrictId = subDistrict.subDistrictId)
            this.postcodeList = postcodeData
            this.postcodeAdapter.setItems(this.postcodeList)

            val realPostcode = postcodeData.find { it.postcode == postcodeStr }
            if (realPostcode != null) {
                this.postcode = realPostcode
                postcodeInput.setText("")
                postcodeInput.setEnableInput(true)
                postcodeInput.setText(realPostcode.postcode)
            } else {
                postcodeInput.setEnableInput(true)
                postcodeInput.setText("")
            }
        }

        dismissProgressDialog()
    }
}
