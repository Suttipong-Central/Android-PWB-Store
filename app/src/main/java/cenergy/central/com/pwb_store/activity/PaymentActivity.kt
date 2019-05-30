package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentTypeClickListener
import cenergy.central.com.pwb_store.extensions.getPaymentType
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.fragment.interfaces.DeliveryHomeListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerHDL
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.DeliveryType.*
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import kotlin.collections.ArrayList

class PaymentActivity : BaseActivity(), CheckoutListener,
        MemberClickListener, PaymentBillingListener, DeliveryOptionsListener,
        PaymentProtocol, StorePickUpListener, DeliveryHomeListener, PaymentTypeClickListener {

    var mToolbar: Toolbar? = null

    private var shippingAddress: AddressInformation? = null
    private var billingAddress: AddressInformation? = null
    private lateinit var deliveryOption: DeliveryOption
    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView

    // data
    private val database = RealmController.getInstance()
    private var cartId: String? = null
    private var productHDLList: ArrayList<ProductHDLBody> = arrayListOf()
    private var customDetail = CustomDetail.createCustomDetail("1", "", "00139")
    private var cartItemList: List<CartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private var pwbMembersList: List<PwbMember> = listOf()
    private var deliveryOptionsList: List<DeliveryOption> = listOf()
    private var mProgressDialog: ProgressDialog? = null
    private var currentFragment: Fragment? = null
    private var memberContact: String? = null
    private var branches: ArrayList<Branch?> = arrayListOf()
    private var branch: Branch? = null
    private var userInformation: UserInformation? = null
    private var deliveryType: DeliveryType? = null
    private val enableShippingSlot: ArrayList<ShippingSlot> = arrayListOf()
    private var specialSKUList: List<Long>? = null
    private var cacheCartItems = listOf<CacheCartItem>()
    private var paymentMethods = listOf<PaymentMethod>()
    private val paymentMethod = PaymentMethod("payatstore",  "Pay at store")

    // date
    private val dateTime = DateTime.now()
    private var year = dateTime.year
    private var month = dateTime.monthOfYear

    companion object {
        fun intent(context: Context) {
            val intent = Intent(context, PaymentActivity::class.java)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        languageButton.visibility = View.INVISIBLE
        handleChangeLanguage() // update language
        initView()

        showProgressDialog()
        cartId = preferenceManager.cartId
        userInformation = database.userInformation
        specialSKUList = getSpecialSKUList()
        cacheCartItems = database.cacheCartItems
        paymentMethods = cacheCartItems.getPaymentType(this)
        startCheckOut() // default page
        getCartItems()
    }

    override fun getSwitchButton(): LanguageButton? = languageButton

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        if (currentFragment is PaymentSuccessFragment) {
            (currentFragment as PaymentSuccessFragment).retrieveOrder() // update content
        }
    }

    override fun getStateView(): NetworkStateView? = networkStateView

    // region {@link CheckOutClickListener}
    override fun startCheckout(contactNo: String?) {
        // skip?
        if (contactNo == null) {
            if(currentState == NetworkInfo.State.CONNECTED){
                membersList = listOf() // clear membersList
                startBilling()
            } else {
                showAlertDialog("", getString(R.string.not_connected_network))
            }
        } else {
            memberContact = contactNo
            getCustomerPWB(contactNo)
        }
    }
    // endregion

    //region MemberClickListener
    override fun onClickedPwbMember(pwbMemberIndex: Int) {
        startBilling(pwbMemberIndex)
    }

    override fun onClickedT1CMember(customerId: String) = getT1CMember(customerId)
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
                createShippingInformation(null, subscribeCheckOut)
            }
            STORE_PICK_UP -> {
                showProgressDialog()
                getStoresDelivery() // default page
            }
            HOME -> {
                showProgressDialog()
                getShippingHomeDelivery()
            }
        }
    }

    private fun isUserChatAndShop(): Boolean {
        return if (userInformation != null) {
            if (userInformation!!.user != null) {
                userInformation!!.user!!.getChatAndShopUser()
            } else {
                false
            }
        } else {
            false
        }
    }

    // region {@link PaymentTypesClickListener}
    override fun onPaymentTypeClickListener(paymentMethods: PaymentMethod) {
        showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethods)
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
        HttpManagerHDL.getInstance(this@PaymentActivity).createBooking(bookingShippingSlot, object : ApiResponseCallback<BookingNumberResponse> {
            override fun success(response: BookingNumberResponse?) {
                if (response != null) {
                    val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress!!.email,
                            shippingDate, slot.id.toString(), slot.description)
                    createShippingInformation(null, subscribeCheckOut)
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
    }
    // endregion

    override fun onBackPressed() {
        backPressed()
    }

    private fun startSuccessfullyFragment(orderId: String) {
        languageButton.visibility = View.VISIBLE
        val cacheCart = ArrayList<CacheCartItem>()
        cacheCart.addAll(cacheCartItems)
        startFragment(PaymentSuccessFragment.newInstance(orderId, cacheCart))
        clearCachedCart() // clear cache item
    }

    private fun startStorePickupFragment(totalBranch: Int) {
        val fragment = DeliveryStorePickUpFragment.newInstance(totalBranch)
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

    private fun startBilling(pwbMemberIndex: Int) {
        val fragment = PaymentBillingFragment.newInstance(pwbMemberIndex)
        startFragment(fragment)
    }

    private fun startSelectMethod() {
        val fragment = PaymentSelectMethodFragment.newInstance()
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
        mToolbar?.setNavigationOnClickListener {
            backPressed()
        }
    }

    private fun showProgressDialog() {
        if (!isFinishing) {
            if (mProgressDialog == null) {
                mProgressDialog = DialogUtils.createProgressDialog(this)
                mProgressDialog?.show()
            } else {
                mProgressDialog?.show()
            }
        }
    }

    private fun getCartItems() {
        preferenceManager.cartId?.let { cartId ->
            HttpManagerMagento.getInstance(this).viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
                override fun success(response: List<CartItem>?) {
                    mProgressDialog?.dismiss()
                    if (response != null) {
                        cartItemList = response
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    DialogHelper(this@PaymentActivity).showErrorDialog(error)
                }
            })
        }
    }

    fun showAlertDialogCheckSkip(title: String, message: String, checkSkip: Boolean) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
        if (checkSkip) {
            builder.setPositiveButton(android.R.string.ok) { _, _ -> startBilling() }
            builder.setNegativeButton(android.R.string.cancel) { _, _ -> startCheckOut() }
        } else {
            builder.setPositiveButton(android.R.string.ok) { _, _ -> startCheckOut() }
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) { dialog, _ -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showFinishActivityDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) {
                    dialog, _ -> dialog.dismiss()
                    finish()
                }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showAlertCheckPayment(title: String, message: String, paymentMethods: PaymentMethod) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { _, _ ->
                    updateOrder(paymentMethods)
                }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, _ ->
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
                billingAddress?.sameBilling = 0
                shippingAddress?.sameBilling = 0
                HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, storeAddress,
                        billingAddress ?: shippingAddress!!, subscribeCheckOut, deliveryOption, // if shipping at store, BillingAddress is ShippingAddress
                        object : ApiResponseCallback<ShippingInformationResponse> {
                            override fun success(response: ShippingInformationResponse?) {
                                mProgressDialog?.dismiss()
                                if (response != null) {
                                    if (isUserChatAndShop()){
                                        selectPaymentTypes()
                                    } else {
                                        showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethod)
                                    }
                                } else {
                                    showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                                }
                            }

                            override fun failure(error: APIError) {
                                mProgressDialog?.dismiss()
                                DialogHelper(this@PaymentActivity).showErrorDialog(error)
                            }
                        })
            } else {
                HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, shippingAddress!!,
                        billingAddress ?: shippingAddress!!, subscribeCheckOut,
                        deliveryOption, object : ApiResponseCallback<ShippingInformationResponse> {
                    override fun success(response: ShippingInformationResponse?) {
                        mProgressDialog?.dismiss()
                        if (response != null) {
                            if (isUserChatAndShop()){
                                selectPaymentTypes()
                            } else {
                                showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethod)
                            }
                        } else {
                            showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                        }
                    }

                    override fun failure(error: APIError) {
                        mProgressDialog?.dismiss()
                        DialogHelper(this@PaymentActivity).showErrorDialog(error)
                    }
                })
            }
        }
    }

    private fun selectPaymentTypes() {
        if(paymentMethods.isEmpty()){
            showFinishActivityDialog("", getString(R.string.not_found_payment_methods))
        } else {
            startSelectMethod()
        }
    }

    private fun getCustomerPWB(mobile: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getPWBCustomer(mobile, object : ApiResponseCallback<List<PwbMember>> {
            override fun success(response: List<PwbMember>?) {
                runOnUiThread {
                    if (response != null && response.isNotEmpty()) { // it can be null
                        this@PaymentActivity.pwbMembersList = response
                        mProgressDialog?.dismiss()
                        startMembersFragment()
                    } else {
                        getMembersT1C(mobile)
                    }
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    getMembersT1C(mobile)
                }
                Log.d("Payment", error.errorCode ?: "")
            }

        })
    }

    private fun getMembersT1C(mobile: String) {
        HttpMangerSiebel.getInstance(this).verifyMemberFromT1C(mobile, " ", object : ApiResponseCallback<List<MemberResponse>> {
            override fun success(response: List<MemberResponse>?) {
                mProgressDialog?.dismiss()
                if (response != null && response.isNotEmpty()) {
                    this@PaymentActivity.membersList = response
                    startMembersFragment()
                } else {
                    showAlertDialogCheckSkip("", resources.getString(R.string.not_have_user), true)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                if (error.errorCode == null) {
                    showAlertDialog("", getString(R.string.not_connected_network))
                } else {
                    showAlertDialogCheckSkip("", resources.getString(R.string.not_have_user), true)
                }
            }
        })
    }

    private fun getT1CMember(customerId: String) {
        showProgressDialog()
        HttpMangerSiebel.getInstance(this).getT1CMember(customerId, object : ApiResponseCallback<Member> {
            override fun success(response: Member?) {
                mProgressDialog?.dismiss()
                if (response != null) {
                    startBilling(response)
                } else {
                    showAlertDialogCheckSkip("", resources.getString(R.string.some_thing_wrong), false)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                if (error.errorCode == null) {
                    showAlertDialog("", getString(R.string.not_connected_network))
                } else {
                    showAlertDialogCheckSkip("", resources.getString(R.string.some_thing_wrong), false)
                }
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
                                showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                            }
                        }

                        override fun failure(error: APIError) {
                            mProgressDialog?.dismiss()
                            DialogHelper(this@PaymentActivity).showErrorDialog(error)
                        }
                    })
        }
    }

    private fun getStoresDelivery() {
        this.branches = arrayListOf()
        HttpManagerMagento.getInstance(this).getBranches(object : ApiResponseCallback<BranchResponse> {
            override fun success(response: BranchResponse?) {
                mProgressDialog?.dismiss()
                if (response != null && userInformation != null) {
                    val branch = response.items.firstOrNull { it.storeId == userInformation!!.store?.storeId.toString() }
                    if (branch != null) {
                        branches.add(branch)
                        response.items.sortedBy { it.storeId }.forEach { if (it.storeId != userInformation!!.store?.storeId.toString()) branches.add(it) }
                    } else {
                        response.items.sortedBy { it.storeId }.forEach { branches.add(it) }
                    }
                    startStorePickupFragment(response.totalBranch)
                } else {
                    showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
    }

    private fun getShippingHomeDelivery() {
        productHDLList = arrayListOf()
        for (i in cartItemList.indices) {
            val productHDL = ProductHDLBody.createProductHDL("", i + 1, cartItemList[i].sku!!,
                    cartItemList[i].qty!!, "00139")
            productHDLList.add(productHDL)
        }

        // check is hdl delivery type 2?
        for (item in cartItemList) {
            if ((getSpecialSKUList() ?: arrayListOf()).contains(item.sku!!.toLong())) {
                customDetail = CustomDetail(deliveryType = "2", deliveryByStore = "00139", deliveryToStore = "")
            }
        }
        val period = PeriodBody.createPeriod(year, month)
        val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                district = shippingAddress!!.subAddress!!.district, subDistrict = shippingAddress!!.subAddress!!.subDistrict,
                province = shippingAddress!!.region, postalId = shippingAddress!!.postcode!!,
                period = period, customDetail = customDetail)
        HttpManagerHDL.getInstance(this@PaymentActivity).getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
            override fun success(response: ShippingSlotResponse?) {
                if (response != null) {
                    if (response.shippingSlot.isNotEmpty()) {
                        if (response.shippingSlot.size > 14) {
                            for (i in response.shippingSlot.indices) {
                                if (enableShippingSlot.size == 14) {
                                    break
                                }
                                enableShippingSlot.add(response.shippingSlot[i])
                            }
                            mProgressDialog?.dismiss()
                            startDeliveryHomeFragment()
                        } else {
                            response.shippingSlot.forEach {
                                enableShippingSlot.add(it)
                            }
                            if (enableShippingSlot.size == 14) {
                                mProgressDialog?.dismiss()
                                startDeliveryHomeFragment()
                            } else {
                                getNextMonthShippingSlot()
                            }
                        }
                    } else {
                        getNextMonthShippingSlot()
                    }
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", getString(R.string.not_have_day_to_delivery))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
    }

    fun getNextMonthShippingSlot() {
        val period: PeriodBody
        if (month == 12) {
            // next year
            year += 1
            month = 1
            period = PeriodBody.createPeriod(year, month)
        } else {
            // next month
            month += 1
            period = PeriodBody.createPeriod(year, month)
        }
        val shippingSlotBody = ShippingSlotBody.createShippingSlotBody(productHDLs = productHDLList,
                district = shippingAddress!!.subAddress!!.district, subDistrict = shippingAddress!!.subAddress!!.subDistrict,
                province = shippingAddress!!.region, postalId = shippingAddress!!.postcode!!,
                period = period, customDetail = customDetail)
        HttpManagerHDL.getInstance(this@PaymentActivity).getShippingSlot(shippingSlotBody, object : ApiResponseCallback<ShippingSlotResponse> {
            override fun success(response: ShippingSlotResponse?) {
                if (response != null && response.shippingSlot.isNotEmpty()) {
                    for (i in response.shippingSlot.indices) {
                        if (enableShippingSlot.size == 14) {
                            break
                        }
                        enableShippingSlot.add(response.shippingSlot[i])
                    }
                    mProgressDialog?.dismiss()
                    startDeliveryHomeFragment()
                } else {
                    getNextMonthShippingSlot()
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
    }

    private fun updateOrder(paymentMethod: PaymentMethod) {
        if (cartId == null) {
            return
        }
        showProgressDialog()

        val email = shippingAddress?.email ?: ""
        val staffId = userInformation?.user?.staffId ?: ""
        val storeId = branch?.storeId?: if (userInformation?.user?.storeId != null) userInformation?.user?.storeId.toString() else ""

        HttpManagerMagento.getInstance(this).updateOder(cartId!!, paymentMethod, email, staffId, storeId, object : ApiResponseCallback<String> {
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
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
    }

    fun getOrder(orderId: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mToolbar?.setNavigationOnClickListener(null)

        startSuccessfullyFragment(orderId)
        mProgressDialog?.dismiss()
    }

    // region {@link PaymentProtocol}
    override fun getItems(): List<CartItem> = this.cartItemList

    override fun getPWBMembers(): List<PwbMember> = this.pwbMembersList

    override fun getPWBMemberByIndex(index: Int): PwbMember? {
        return if (pwbMembersList.isNotEmpty()) {
            this.pwbMembersList[index]
        } else {
            null
        }
    }

    override fun getMembers(): List<MemberResponse> = this.membersList

    override fun getDeliveryOptions(): List<DeliveryOption> = this.deliveryOptionsList

    override fun getSelectedDeliveryType(): DeliveryType? = this.deliveryType

    override fun getShippingAddress(): AddressInformation? = this.shippingAddress

    override fun getBillingAddress(): AddressInformation? = this.billingAddress

    override fun getEnableDateShipping(): ArrayList<ShippingSlot> = this.enableShippingSlot

    override fun getBranches(): ArrayList<Branch?> = this.branches

    override fun getSelectedBranch(): Branch? = this.branch

    override fun getPaymentMethods(): List<PaymentMethod> = this.paymentMethods
    // endregion

    // region {@link StorePickUpListener}
    override fun onUpdateStoreDetail(branch: Branch) {
        if (currentFragment is DeliveryStorePickUpFragment) {
            (currentFragment as DeliveryStorePickUpFragment).updateStoreDetail(branch)
        }
    }

    override fun onSelectedStore(branch: Branch) {
        this.branch = branch
        userInformation?.let { userInformation ->
            if (userInformation.user != null && userInformation.store != null) {
                showProgressDialog()
                val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress!!.email, "", "", "")
                // store shipping this case can be anything
                createShippingInformation(shippingAddress, subscribeCheckOut)
            }
        }
    }
    // endregion

    private fun getSpecialSKUList(): List<Long>? {
        return this.specialSKUList
                ?: ReadFileHelper<List<Long>>().parseRawJson(this@PaymentActivity, R.raw.special_sku,
                        object : TypeToken<List<Long>>() {}.type, null)
    }

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
            if (this.membersList.isNotEmpty() || this.pwbMembersList.isNotEmpty()) {
                startMembersFragment()
            } else {
                startCheckOut()
            }
            return
        }

        if (currentFragment is PaymentSelectMethodFragment) {
            startDeliveryOptions()
            return
        }

        if (currentFragment is PaymentMembersFragment) {
            this.membersList = listOf()
            this.pwbMembersList = listOf()
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

    private fun clearCachedCart() {
        preferenceManager.clearCartId()
        database.deleteAllCacheCartItem()
        Log.d("Order Success", "Cleared cached CartId and CartItem")
    }
}