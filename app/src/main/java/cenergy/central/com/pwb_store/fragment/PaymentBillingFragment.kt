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

class PaymentBillingFragment : Fragment() {

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

    private lateinit var provinceInput: PowerBuyAutoCompleteTextStroke
    private lateinit var districtInput: PowerBuyAutoCompleteTextStroke
    private lateinit var subDistrictInput: PowerBuyAutoCompleteTextStroke
    private lateinit var postcodeInput: PowerBuyAutoCompleteTextStroke
    private var mProgressDialog: ProgressDialog? = null

    // data
    private val database = RealmController.with(context)
    private var cartItemList: List<CartItem> = listOf()
    private var shippingAddress: AddressInformation? = null
    private var paymentProtocol: PaymentProtocol? = null
    private var paymentBillingListener: PaymentBillingListener? = null
    private var cartId: String? = null
    private var member: Member? = null
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
    private val provinces = database.provinces
    private var districts = emptyList<District>()
    private var subDistricts = emptyList<SubDistrict>()
    private var postcodes = emptyList<Postcode>()
    private var provinceNameList = getProvinceNameList()
    private var districtNameList = getDistrictNameList()
    private var subDistrictNameList = getSubDistrictNameList()
    private var postcodeList = getPostcodeList()


    private var province: Province? = null
    private var district: District? = null
    private var subDistrict: SubDistrict? = null
    private var postcode: Postcode? = null
    // adapter
    private var provinceAdapter: AddressAdapter? = null
    private var districtAdapter: AddressAdapter? = null
    private var subDistrictAdapter: AddressAdapter? = null
    private var postcodeAdapter: AddressAdapter? = null

    companion object {
        private const val MEMBER = "MEMBER"

        fun newInstance(): PaymentBillingFragment {
            val fragment = PaymentBillingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(member: Member): PaymentBillingFragment {
            val fragment = PaymentBillingFragment()
            val args = Bundle()
            args.putParcelable(MEMBER, member)
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
        member = arguments?.getParcelable(MEMBER)
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

        //Set Input type
        contactNumberEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        contactNumberEdt.setTextLength(10)
        homePhoneEdt.setEditTextInputType(InputType.TYPE_CLASS_NUMBER)
        homePhoneEdt.setTextLength(10)
        emailEdt.setEditTextInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

        setupInputAddress()

        //Setup Member
        if (hasMember()) {
            member?.let { member ->
                firstNameEdt.setText(member.getFirstName())
                lastNameEdt.setText(member.getLastName())
                contactNumberEdt.setText(member.mobilePhone)
                emailEdt.setText(member.email)
                homeNoEdt.setText(member.homeNo)
                homeBuildingEdit.setText(member.homeBuilding)
                homeSoiEdt.setText(member.homeSoi)
                homeRoadEdt.setText(member.homeRoad)

                // validate province with local db
                val province = database.getProvinceByNameTh(member.homeCity)
                if (province != null) {
                    provinceInput.setText(member.homeCity)
                    this.province = province
                    this.districts = database.getDistrictsByProvinceId(province.provinceId)
                    this.districtNameList = getDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.districtNameList)
                }

                // validate district with local db
                val district = database.getDistrictByNameTh(member.homeDistrict)
                if (district != null) {
                    districtInput.setText(member.homeDistrict)
                    this.district = district
                    this.subDistricts = database.getSubDistrictsByDistrictId(district.districtId)
                    this.subDistrictNameList = getSubDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.subDistrictNameList)
                }

                // validate sub district with local db
                val subDistrict = database.getSubDistrictByNameTh(member.homeSubDistrict)
                if (subDistrict != null) {
                    subDistrictInput.setText(member.homeSubDistrict)
                    this.subDistrict = subDistrict
                    this.postcodes = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                    this.postcodeList = getPostcodeList()
                    this.postcodeAdapter?.setItems(this.postcodeList)
                }

                // validate postcode with local db
                val postcode = database.getPostcodeByCode(member.homePostalCode)
                if (postcode != null) {
                    postcodeInput.setText(member.homePostalCode)
                    this.postcode = postcode
                }

                homePhoneEdt.setText(member.homePhone)
            }
        } else {
            shippingAddress?.let { shippingAddress ->
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
                    provinceInput.setText(shippingAddress.region)
                    this.province = province
                    this.districts = database.getDistrictsByProvinceId(province.provinceId)
                    this.districtNameList = getDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.districtNameList)
                }

                // validate district with local db
                val district = database.getDistrictByNameTh(shippingAddress.subAddress!!.district)
                if (district != null) {
                    districtInput.setText(shippingAddress.subAddress!!.district)
                    this.district = district
                    this.subDistricts = database.getSubDistrictsByDistrictId(district.districtId)
                    this.subDistrictNameList = getSubDistrictNameList()
                    this.subDistrictAdapter?.setItems(this.subDistrictNameList)
                }

                // validate sub district with local db
                val subDistrict = database.getSubDistrictByNameTh(shippingAddress.subAddress!!.subDistrict)
                if (subDistrict != null) {
                    subDistrictInput.setText(shippingAddress.subAddress!!.subDistrict)
                    this.subDistrict = subDistrict
                    this.postcodes = database.getPostcodeBySubDistrictId(subDistrict.subDistrictId)
                    this.postcodeList = getPostcodeList()
                    this.postcodeAdapter?.setItems(this.postcodeList)
                }

                // validate postcode with local db
                val postcode = database.getPostcodeByCode(shippingAddress.subAddress!!.postcode)
                if (postcode != null) {
                    postcodeInput.setText(shippingAddress.subAddress!!.postcode)
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
        totalPrice.text = getDisplayPrice(unit, total.toString())
        deliveryBtn.setOnClickListener {
            checkConfirm()
        }
    }

    private fun hasMember(): Boolean {
        return member != null
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
    }

    private fun checkConfirm() {
        showProgressDialog()
        if (!hasEmptyInput()) {
            // setup value
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

            val shippingAddress = AddressInformation.createAddress(
                    firstName = firstName, lastName = lastName, email = email, contactNo = contactNo,
                    homeNo = homeNo, homeBuilding = homeBuilding, homeSoi = homeSoi, homeRoad = homeRoad,
                    homePostalCode = homePostalCode, homePhone = homePhone, provinceId = homeProvinceId,
                    provinceCode = homeProvinceCode, countryId = homeCountryId, districtId = homeDistrictId,
                    subDistrictId = homeSubDistrictId, postcodeId = homePostalCodeId, homeCity = homeProvince,
                    homeDistrict = homeDistrict, homeSubDistrict = homeSubDistrict)

            mProgressDialog?.dismiss()
            paymentBillingListener?.setShippingAddressInfo(shippingAddress)

        } else {
            mProgressDialog?.dismiss()
            showAlertDialog("", resources.getString(R.string.fill_in_important_information))
        }
    }

    private fun hasEmptyInput(): Boolean {
        val validator = ValidationHelper.getInstance(context!!)
        // setup error
        firstNameEdt.setError(validator.validText(firstNameEdt.getText()))
        lastNameEdt.setError(validator.validText(lastNameEdt.getText()))
        emailEdt.setError(validator.validEmail(emailEdt.getText()))
        contactNumberEdt.setError(validator.validThaiPhoneNumber(contactNumberEdt.getText()))
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
