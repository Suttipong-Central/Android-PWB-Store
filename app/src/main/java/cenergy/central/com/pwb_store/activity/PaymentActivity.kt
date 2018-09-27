package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.fragment.interfaces.DeliveryHomeListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerHDL
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.DeliveryType.*
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class PaymentActivity : AppCompatActivity(), CheckoutListener,
        MemberClickListener, PaymentBillingListener, DeliveryOptionsListener,
        PaymentProtocol, StorePickUpListener, DeliveryHomeListener {

    var mToolbar: Toolbar? = null

    private lateinit var preferenceManager: PreferenceManager
    private var shippingAddress: AddressInformation? = null
    private var billingAddress: AddressInformation? = null
    private lateinit var deliveryOption: DeliveryOption

    // data
    private val database = RealmController.with(this)
    private var cartId: String? = null
    private var productHDLList: ArrayList<ProductHDLBody> = arrayListOf()
    private val customDetail = CustomDetail.createCustomDetail("1", "", "00139")
    private var cartItemList: List<CartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private var deliveryOptionsList: List<DeliveryOption> = listOf()
    private var mProgressDialog: ProgressDialog? = null
    private var currentFragment: Fragment? = null
    private var memberContact: String? = null
    private var branches: ArrayList<Branch> = arrayListOf()
    private var branch: Branch? = null
    private var userInformation: UserInformation? = null
    private var deliveryType: DeliveryType? = null
    private var shippingSlotResponse: ShippingSlotResponse? = null

    companion object {
        fun intent(context: Context) {
            val intent = Intent(context, PaymentActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val preferenceManager = PreferenceManager(this)
        cartId = preferenceManager.cartId
        userInformation = database.userInformation
        initView()
        getCartItems()
        startCheckOut()
    }

    // region {@link CheckOutClickListener}
    override fun startCheckout(contactNo: String?) {
        // skip?
        if (contactNo == null) {
            membersList = listOf() // clear membersList
            startBilling()
        } else {
            memberContact = contactNo
            getMembersT1C(contactNo)
        }
    }
    // endregion

    // region {@link MemberClickListener}
    override fun onClickedMember(customerId: String) = getT1CMember(customerId)
    // endregion

    // region {@link PaymentBillingListener}
    override fun setShippingAddressInfo(shippingAddress: AddressInformation) {
        showProgressDialog()
        this.shippingAddress = shippingAddress
        cartId?.let { getDeliveryOptions(it) } // request delivery options
    }

    override fun setBillingAddressInfo(billingAddress: AddressInformation) {
        this.billingAddress = billingAddress
    }
    // endregion

    // region {@link DeliveryOptionsListener}
    override fun onSelectedOptionListener(deliveryOption: DeliveryOption) {
        this.deliveryOption = deliveryOption
        deliveryType = DeliveryType.fromString(deliveryOption.methodCode)
        when (deliveryType) {
            EXPRESS, STANDARD -> {
                showProgressDialog()
                val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress!!.email,
                        "", "", "")
                showAlertCheckPayment("", resources.getString(R.string.confrim_oder), null, subscribeCheckOut)
            }
            STORE_PICK_UP -> {
                showProgressDialog()
                getStoresDelivery()
            }
            HOME -> {
                showProgressDialog()
                getShippingHomeDelivery()
            }
        }
    }

    // endregion

    // region {@link HomeDeliveryListener}
    override fun onPaymentClickListener(slot: Slot, date: Int, month: Int, year: Int, shippingDate: String) {
        showProgressDialog()
        val periodTimeSlot = PeriodTimeSlotBody.createPeriod(year, month, date, slot.id)
        createBookingHomeDelivery(periodTimeSlot, slot, shippingDate)
    }

    private fun createBookingHomeDelivery(periodTimeSlot: PeriodTimeSlotBody, slot: Slot, shippingDate: String) {
        val bookingShippingSlot = BookingShippingSlotBody.bookingShippingSlotBody(
                productHDLList, shippingAddress!!.subAddress!!.district, shippingAddress!!.subAddress!!.subDistrict,
                shippingAddress!!.region, shippingAddress!!.postcode!!, periodTimeSlot, customDetail)
        HttpManagerHDL.getInstance().createBooking(bookingShippingSlot, object : ApiResponseCallback<BookingNumberResponse> {
            override fun success(response: BookingNumberResponse?) {
                if (response != null) {
                    Log.d("Booking number response", response.bookingNo)
                    val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress!!.email,
                            shippingDate, slot.id.toString(), slot.description)
                    showAlertCheckPayment("", resources.getString(R.string.confrim_oder), null, subscribeCheckOut)

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
    // endregion

    override fun onBackPressed() {
        backPressed()
    }

    private fun startSuccessfullyFragment(orderId: String) {
        // get cache item
        val cacheCartItems = arrayListOf<CacheCartItem>()
        cacheCartItems.addAll(RealmController.getInstance().cacheCartItems ?: arrayListOf())
        clearCachedCart() // clear cache item
        startFragment(PaymentSuccessFragment.newInstance(orderId, cacheCartItems))
    }

    private fun startStorePickupFragment() {
        val fragment = DeliveryStorePickUpFragment.newInstance()
        startFragment(fragment)
    }

    private fun startDeliveryHomeFragment() {
        val fragment = DeliveryHomeFragment.newInstance()
        startFragment(fragment)
    }

    private fun startDeliveryOptions() {
        val fragment = DeliveryOptionsFragment.newInstance()
        startFragment(fragment)
    }

    private fun startCheckOut() {
        val fragment = PaymentCheckOutFragment.newInstance()
        startFragment(fragment)
    }

    private fun startBilling() {
        val fragment = PaymentBillingFragment.newInstance()
        startFragment(fragment)
    }

    private fun startBilling(response: Member?) {
        val fragment = if (response != null) PaymentBillingFragment.newInstance(response) else
            PaymentBillingFragment.newInstance()
        startFragment(fragment)
    }

    private fun startMembersFragment() {
        val fragment = PaymentMembersFragment.newInstance()
        startFragment(fragment)
    }

    private fun startFragment(fragment: Fragment) {
        hideKeyboard()
        currentFragment = fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, fragment)
                .commit()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        preferenceManager = PreferenceManager(this)
        mToolbar?.setNavigationOnClickListener {
            backPressed()
        }
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun getCartItems() {
        preferenceManager.cartId?.let { cartId ->
            HttpManagerMagento.getInstance(this).viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
                override fun success(response: List<CartItem>?) {
                    if (response != null) {
                        cartItemList = response
                        mProgressDialog?.dismiss()
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                }
            })
        }
    }

    fun showAlertDialogCheckSkip(title: String, message: String, checkSkip: Boolean) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
        if (checkSkip) {
            builder.setPositiveButton(android.R.string.ok) { dialog, which -> startBilling() }
            builder.setNegativeButton(android.R.string.cancel) { dialog, which -> startCheckOut() }
        } else {
            builder.setPositiveButton(android.R.string.ok) { dialog, which -> startCheckOut() }
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showResponseAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
        builder.setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun clearCachedCart() {
        preferenceManager.clearCartId()
        RealmController.getInstance().deleteAllCacheCartItem()
        Log.d("Order Success", "Cleared cached CartId and CartItem")
    }

    private fun showAlertCheckPayment(title: String, message: String, storeAddress: AddressInformation?,
                                      subscribeCheckOut: SubscribeCheckOut) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which ->
                    createShippingInformation(storeAddress, subscribeCheckOut)
                }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, which ->
                    dialog.dismiss()
                    mProgressDialog?.dismiss()
                }
                .setCancelable(false)

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun createShippingInformation(storeAddress: AddressInformation?, subscribeCheckOut: SubscribeCheckOut) {
        if (cartId != null && shippingAddress != null) {
            if (storeAddress != null) { // is shipping at store?
                storeAddress.sameBilling = 0
                HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, storeAddress,
                        billingAddress
                                ?: shippingAddress!!, subscribeCheckOut, deliveryOption, // if shipping at store, BillingAddress is ShippingAddress
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
            } else {
                if (billingAddress != null) {
                    shippingAddress!!.sameBilling = 0
                } else {
                    shippingAddress!!.sameBilling = 1
                }
                HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, shippingAddress!!,
                        billingAddress ?: shippingAddress!!, subscribeCheckOut,
                        deliveryOption, object : ApiResponseCallback<ShippingInformationResponse> {
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
    }

    private fun getMembersT1C(mobile: String) {
        showProgressDialog()
        HttpMangerSiebel.getInstance().verifyMemberFromT1C(mobile, " ", object : ApiResponseCallback<List<MemberResponse>> {
            override fun success(response: List<MemberResponse>?) {
                if (response != null && response.isNotEmpty()) {
                    this@PaymentActivity.membersList = response
                    startMembersFragment()
                    mProgressDialog?.dismiss()
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialogCheckSkip("", resources.getString(R.string.not_have_user), true)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                showAlertDialogCheckSkip("", resources.getString(R.string.not_have_user), true)
            }
        })
    }

    private fun getT1CMember(customerId: String) {
        showProgressDialog()
        HttpMangerSiebel.getInstance().getT1CMember(customerId, object : ApiResponseCallback<Member> {
            override fun success(response: Member?) {
                if (response != null) {
                    startBilling(response)
                    mProgressDialog?.dismiss()
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialogCheckSkip("", resources.getString(R.string.some_thing_wrong), false)
                }
            }

            override fun failure(error: APIError) {
                showAlertDialogCheckSkip("", resources.getString(R.string.some_thing_wrong), false)
                mProgressDialog?.dismiss()
            }
        })
    }

    private fun getDeliveryOptions(cartId: String) {
        shippingAddress?.let {
            HttpManagerMagento.getInstance(this).getOrderDeliveryOptions(cartId, it,
                    object : ApiResponseCallback<List<DeliveryOption>> {
                        override fun success(response: List<DeliveryOption>?) {
                            mProgressDialog?.dismiss()
                            if (response != null) {
                                deliveryOptionsList = response
                                startDeliveryOptions()
                            } else {
                                showResponseAlertDialog("", resources.getString(R.string.some_thing_wrong))
                            }
                        }

                        override fun failure(error: APIError) {
                            mProgressDialog?.dismiss()
                            showResponseAlertDialog("", error.errorMessage)
                        }
                    })
        }
    }

    private fun getStoresDelivery() {
        HttpManagerMagento.getInstance(this).getBranches(object : ApiResponseCallback<List<Branch>> {
            override fun success(response: List<Branch>?) {
                mProgressDialog?.dismiss()
                if (response != null && userInformation != null) {
                    val branch = response.firstOrNull{it.storeId == userInformation!!.store?.storeId.toString()}
                    if (branch != null){
                        branches.add(branch)
                        response.sortedBy { it.storeId }.forEach { if (it.storeId != userInformation!!.store?.storeId.toString())branches.add(it) }
                    } else {
                        response.sortedBy { it.storeId }.forEach { branches.add(it) }
                    }
                    startStorePickupFragment()
                } else {
                    showResponseAlertDialog("", resources.getString(R.string.some_thing_wrong))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                showResponseAlertDialog("", error.errorMessage)
            }
        })
    }

    private fun getShippingHomeDelivery() {
        for (i in cartItemList.indices) {
            val productHDL = ProductHDLBody.createProductHDL("", i + 1, cartItemList[i].sku!!,
                    cartItemList[i].qty!!, "00139")
            productHDLList.add(productHDL)
        }
        val dateTime = DateTime.now()
        val period = PeriodBody.createPeriod(dateTime.year, dateTime.monthOfYear)
        val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(
                productHDLList, shippingAddress!!.subAddress!!.district, shippingAddress!!.subAddress!!.subDistrict,
                shippingAddress!!.region, shippingAddress!!.postcode!!, period, customDetail)
        HttpManagerHDL.getInstance().getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
            override fun success(response: ShippingSlotResponse?) {
                mProgressDialog?.dismiss()
                if (response != null) {
                    shippingSlotResponse = response
                    startDeliveryHomeFragment()
                } else {
                    showResponseAlertDialog("", resources.getString(R.string.some_thing_wrong))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                showResponseAlertDialog("", error.errorMessage)
            }
        })
    }

    private fun updateOrder() {
        if (cartId == null) {
            return
        }

        val email = shippingAddress?.email ?: ""
        val staffId = userInformation?.user?.staffId ?: ""
        val storeId = branch?.storeId ?: ""

        HttpManagerMagento.getInstance(this).updateOder(cartId!!, email, staffId, storeId, object : ApiResponseCallback<String> {
            override fun success(response: String?) {
                if (response != null) {
                    getOrder(response)
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

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    fun getOrder(orderId: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mToolbar?.setNavigationOnClickListener(null)

        startSuccessfullyFragment(orderId)
        mProgressDialog?.dismiss()
    }

    // region {@link PaymentProtocol}
    override fun getItems(): List<CartItem> = this.cartItemList

    override fun getMembers(): List<MemberResponse> = this.membersList

    override fun getDeliveryOptions(): List<DeliveryOption> = this.deliveryOptionsList

    override fun getSelectedDeliveryType(): DeliveryType? = this.deliveryType

    override fun getShippingAddress(): AddressInformation? = this.shippingAddress

    override fun getShippingSlot(): ShippingSlotResponse? = this.shippingSlotResponse

    override fun getBranches(): List<Branch> = this.branches

    override fun getSelectedBranch(): Branch? = this.branch
    // endregion

    // region {@link StorePickUpListener}
    override fun onUpdateStoreDetail(branch: Branch) {
        this.branch = branch
        if (currentFragment is DeliveryStorePickUpFragment) {
            (currentFragment as DeliveryStorePickUpFragment).updateStoreDetail(branch)
        }
    }

    override fun onSelectedStore(branch: Branch) {
        userInformation?.let { userInformation ->
            if (userInformation.user != null && userInformation.store != null) {
                showProgressDialog()
//                val staff = userInformation.user
//                val storeStaff = userInformation.store
//                //Check this if change to use PWB login
//                val province = database.getProvinceByNameEn(storeStaff?.province ?: "")
//                val district = database.getDistrictByNameEn(storeStaff?.district ?: "")
//                val subDistrict = database.getSubDistrictByNameEn(storeStaff?.subDistrict ?: "")
//                val postCode = database.getPostcodeByCode(storeStaff?.postalCode)
//                val storeAddress = AddressInformation.createAddress(
//                        firstName = shippingAddress?.firstname
//                                ?: "Testing", lastName = shippingAddress?.lastname
//                        ?: "Testing", email = shippingAddress?.email ?: "storepickup@testing.com",
//                        contactNo = shippingAddress?.telephone ?: "0000000000", homeNo = "",
//                        homeBuilding = "ชื่ออาคาร", homeSoi = "ซอย",
//                        homeRoad = "ถนน", homePostalCode = storeStaff?.postalCode ?: "10501",
//                        homePhone = "", provinceId = "668",
//                        provinceCode = "BKK", countryId = "TH",
//                        districtId = "25",
//                        subDistrictId = "157",
//                        postcodeId = postCode?.id?.toString() ?: "159", homeCity = province?.nameTh
//                        ?: "กรุงเทพมหานคร",
//                        homeDistrict = district?.nameTh
//                                ?: "บางรัก", homeSubDistrict = subDistrict?.nameTh ?: "บางรัก")
                val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress!!.email, "", "", "")
                // store shipping this case can be anything
                showAlertCheckPayment("", resources.getString(R.string.confrim_oder), shippingAddress, subscribeCheckOut)
            }
        }
    }
    // endregion

    private fun backPressed() {
        if (currentFragment is DeliveryStorePickUpFragment) {
            startDeliveryOptions()
            return
        }

        if (currentFragment is DeliveryHomeFragment) {
            startDeliveryOptions()
            return
        }

        if (currentFragment is DeliveryOptionsFragment) {
            startBilling()
            return
        }

        if (currentFragment is PaymentBillingFragment) {
            this.shippingAddress = null
            if (this.membersList.isNotEmpty()) {
                startMembersFragment()
            } else {
                startCheckOut()
            }
            return
        }

        if (currentFragment is PaymentMembersFragment) {
            startCheckOut()
            return
        }

        if (currentFragment is PaymentCheckOutFragment) {
            hideKeyboard()
            finish()
            return
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken, 0)
        }
    }
}