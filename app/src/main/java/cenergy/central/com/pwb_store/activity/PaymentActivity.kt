package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.PaymentProtocol
import cenergy.central.com.pwb_store.dialogs.T1MemberDialogFragment
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentItemClickListener
import cenergy.central.com.pwb_store.dialogs.interfaces.PaymentT1Listener
import cenergy.central.com.pwb_store.extensions.checkItemsBy
import cenergy.central.com.pwb_store.extensions.getPaymentType
import cenergy.central.com.pwb_store.extensions.isBankAndCounterServiceType
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.fragment.interfaces.DeliveryHomeListener
import cenergy.central.com.pwb_store.fragment.interfaces.PaymentTransferListener
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.helpers.ReadFileHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerHDL
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.api.BranchApi
import cenergy.central.com.pwb_store.manager.api.HomeDeliveryApi
import cenergy.central.com.pwb_store.manager.api.OrderApi
import cenergy.central.com.pwb_store.manager.api.PaymentApi
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.DeliveryType.*
import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.body.PaymentInfoBody
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.*
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.reflect.TypeToken

class PaymentActivity : BaseActivity(), CheckoutListener,
        MemberClickListener, PaymentBillingListener, DeliveryOptionsListener,
        PaymentProtocol, StorePickUpListener, DeliveryHomeListener, PaymentItemClickListener,
        PaymentT1Listener, PaymentTransferListener {

    var mToolbar: Toolbar? = null

    private var shippingAddress: AddressInformation? = null
    private var billingAddress: AddressInformation? = null
    private lateinit var deliveryOption: DeliveryOption
    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView

    // data
    private val database = RealmController.getInstance()
    private var cartId: String? = null
    private var shoppingCartItem: List<ShoppingCartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private var eOrderingMembers: List<EOrderingMember> = listOf()
    private var membersHDL: List<HDLCustomerInfos> = listOf()
    private var deliveryOptionsList: List<DeliveryOption> = listOf()
    private var mProgressDialog: ProgressDialog? = null
    private var currentFragment: Fragment? = null
    private var memberContact: String? = null
    private var branches: ArrayList<BranchResponse> = arrayListOf()
    private var branch: Branch? = null
    private var userInformation: UserInformation? = null
    private var deliveryType: DeliveryType? = null
    private var enableShippingSlot: ArrayList<ShippingSlot> = arrayListOf()
    private var specialSKUList: List<Long>? = null
    private var cacheCartItems = listOf<CacheCartItem>()
    private var paymentMethods = arrayListOf<PaymentMethod>()
    private var paymentMethod = PaymentMethod("e_ordering", "Pay Here")
    private var theOneCardNo: String = ""
    private var shippingSlot: ShippingSlot? = null
    private var discountPrice = 0.0
    private var promotionDiscount = 0.0
    private var totalPrice = 0.0
    private var paymentAgents = listOf<PaymentAgent>()

    // data product 2h
    private var product2h: Product? = null
    private var isEditStorePickup: Boolean = false
    private var checkoutType: CheckoutType = CheckoutType.NORMAL

    // Firebase
    private val analytics by lazy { Analytics(this) }
    private lateinit var fbRemoteConfig: FirebaseRemoteConfig
    private var cacheExpiration: Long = 3600 // 1 hour in seconds.

    companion object {
        private const val TAG = "PaymentActivity"
        private const val EXTRA_PRODUCT_2H = "extra_product_2h"
        private const val EXTRA_CHECK_OUT_ISPU = "extra_check_out_ispu"
        private const val EXTRA_EDIT_STORE_PICKUP = "extra_edit_store_pickup"

        fun startCheckout(context: Context, isISPU: Boolean = false) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_CHECK_OUT_ISPU, isISPU)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }

        fun startSelectStorePickup(context: Context, product: Product) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_2H, product)
            intent.putExtra(EXTRA_CHECK_OUT_ISPU, true)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }

        fun startEditStorePickup(context: Context) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_CHECK_OUT_ISPU, true)
            intent.putExtra(EXTRA_EDIT_STORE_PICKUP, true)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        // setup remote config
        setupRemoteConfig()

        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        languageButton.visibility = View.INVISIBLE
        handleChangeLanguage() // update language

        initView()

        showProgressDialog()
        cartId = preferenceManager.cartId
        userInformation = database.userInformation
        specialSKUList = getSpecialSKUList()

        // is start select store with product 2h?
        if (intent.hasExtra(EXTRA_PRODUCT_2H)) {
            this.product2h = intent.getParcelableExtra(EXTRA_PRODUCT_2H)
            this.checkoutType = CheckoutType.ISPU
            checkoutWithProduct2hr(this.product2h!!)
        } else {
            val isISPU = intent.getBooleanExtra(EXTRA_CHECK_OUT_ISPU, false)
            isEditStorePickup = intent.getBooleanExtra(EXTRA_EDIT_STORE_PICKUP, false)
            this.checkoutType = if (isISPU) CheckoutType.ISPU else CheckoutType.NORMAL
            if (!isEditStorePickup) {
                fetchEorderingConfig()
            } else {
                onEditStorePickup()
            }
        }
    }

    private fun onEditStorePickup() {
        mProgressDialog?.dismiss()
        val fragment = DeliveryStorePickUpFragment.newInstance(is2hrProduct = true,
                editStorePickup = true)
        startFragment(fragment)
    }

    override fun getSwitchButton(): LanguageButton? = languageButton

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        if (currentFragment is PaymentSuccessFragment) {
            (currentFragment as PaymentSuccessFragment).retrieveOrder() // update content
        }
        if (currentFragment is PaymentCreatedOrder) {
            (currentFragment as PaymentCreatedOrder).updateView() // update content
        }
    }

    override fun getStateView(): NetworkStateView? = networkStateView

    // region {@link CheckOutClickListener}
    override fun startCheckout(contactNo: String?) {
        // skip?
        if (contactNo == null) {
            if (currentState == NetworkInfo.State.CONNECTED) {
                membersList = listOf() // clear membersList
                analytics.trackCustomerSource(CustomerSource.NEW_USER)
                startBilling()
            } else {
                showCommonDialog(getString(R.string.not_connected_network))
            }
        } else {
            memberContact = contactNo
            startRetrieveMemberContact(contactNo)
        }
    }
    // endregion

    //region MemberClickListener
    override fun onClickedPwbMember(pwbMemberIndex: Int) {
        startBilling(pwbMemberIndex)
    }

    override fun onClickedT1CMember(member: MemberResponse) {
        theOneCardNo = member.cards[0].cardNo
        getT1CMember(member.customerId)
    }

    override fun onClickedHDLMember(position: Int) {
        startBilling(membersHDL[position])
    }
    // endregion

    // region {@link PaymentBillingListener}
    override fun saveAddressInformation(shippingAddress: AddressInformation, billingAddress: AddressInformation?, t1cNumber: String) {
        showProgressDialog()
        this.shippingAddress = shippingAddress
        this.billingAddress = billingAddress
        this.theOneCardNo = t1cNumber
        cartId?.let { getDeliveryOptions(it) } // request delivery options
    }

    override fun setBillingAddressWithIspu(billingAddress: AddressInformation, t1cNumber: String) {
        this.shippingAddress = billingAddress // sent only address box 1
        this.theOneCardNo = t1cNumber
        createOrderWithIspu()
    }
    // endregion

    // region {@link PaymentT1Listener}
    override fun onChangingT1Member(mobile: String) {
        Log.d(TAG, mobile)
        showProgressDialog()
        getMembersT1CChanged(mobile)
    }

    override fun onSelectedT1Member(the1Member: MemberResponse) {
        if (currentFragment is PaymentBillingFragment) {
            (currentFragment as PaymentBillingFragment).updateT1MemberInput(the1Member)
            //update t1 card no.
            theOneCardNo = the1Member.cards[0].cardNo
        }
    }
    // endregion

    // region {@link DeliveryOptionsListener}
    override fun onSelectedOptionListener(deliveryOption: DeliveryOption) {
        this.deliveryOption = deliveryOption // set delivery option
        deliveryType = DeliveryType.fromString(deliveryOption.methodCode)
        analytics.trackSelectDelivery(deliveryOption.methodCode)
        when (deliveryType) {
            EXPRESS, STANDARD -> {
                showProgressDialog()
                val addressInfoExtBody = AddressInfoExtensionBody(checkout = shippingAddress!!.email)
                handleCreateShippingInformation(deliveryOption, addressInfoExtBody)
            }
            STORE_PICK_UP -> {
                showProgressDialog()
                getStoresDelivery(deliveryOption) // default page
            }
            HOME -> {
                this.enableShippingSlot = deliveryOption.extension.shippingSlots
                startDeliveryHomeFragment()
            }
            else -> {
                /* do noting*/
            }
        }
    }
    // endregion

    // region {@link PaymentTransfersFragment.PaymentTransferListener}
    override fun startPayNow(payerName: String, payerEmail: String, agentCode: String,
                             agentChannelCode: String, mobileNumber: String) {

        showAlertConfirmPayment(paymentMethod, payerName, payerEmail, agentCode, agentChannelCode, mobileNumber)
    }
    // endregion

    private fun standardCheckout() {
        cacheCartItems = database.cacheCartItems
        paymentMethods = cacheCartItems.getPaymentType(this)
        startCheckOut() // default page
        getItemTotal()
    }

    private fun checkoutWithProduct2hr(product: Product) {
        showProgressDialog()
        if (database.provinces != null && database.provinces.isNotEmpty()) {
            BranchApi().getBranchesISPU(this, product.sku,
                    object : ApiResponseCallback<List<BranchResponse>> {
                        override fun success(response: List<BranchResponse>?) {
                            runOnUiThread {
                                if (response != null) {
                                    branches.clear()
                                    branches.addAll(response)
                                    val fragment = DeliveryStorePickUpFragment.newInstance(true)
                                    startFragment(fragment)
                                }
                                mProgressDialog?.dismiss()
                            }
                        }

                        override fun failure(error: APIError) {
                            runOnUiThread {
                                showCommonDialog(null, getString(R.string.some_thing_wrong),
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            dialog?.dismiss()
                                            finish()
                                        })
                            }
                        }
                    })
        } else {
            loadProvinceData()
        }
    }

    /*
    * If no have provinces data must force download province data
    * because in Branch detail page have to display about address
    * */
    private fun loadProvinceData() {
        showProgressDialog()

        HttpManagerMagento.getInstance(this).getProvinces(true,
                object : ApiResponseCallback<List<Province>> {
                    override fun success(response: List<Province>?) {
                        // JUST FOR OPEN STORE PICK PAGE WITH PRODUCT 2H
                        product2h?.let { checkoutWithProduct2hr(it) }
                    }

                    override fun failure(error: APIError) {
                        showCommonDialog(null, getString(R.string.some_thing_wrong),
                                DialogInterface.OnClickListener { dialog, which ->
                                    dialog?.dismiss()
                                    finish()
                                })
                        mProgressDialog?.dismiss()
                    }
                })
    }

    // region {@link PaymentTypesClickListener}
    override fun onClickedItem(paymentMethod: PaymentMethod) {
        this.paymentMethod = paymentMethod
        if (paymentMethod.isBankAndCounterServiceType()) {
            // open bank/counter service options
            retrievePaymentInformation()
        } else {
            showAlertConfirmPayment(paymentMethod)
        }
    }
    // endregion

    // region {@link HomeDeliveryListener}
    override fun onPaymentClickListener(shippingSlot: ShippingSlot, date: Int, month: Int, year: Int, shippingDate: String) {
        if (shippingAddress == null) return

        showProgressDialog()
        this.shippingSlot = shippingSlot // set shipping slot
        val subscribeCheckOut = AddressInfoExtensionBody(
                checkout = shippingAddress!!.email,
                shippingDate = shippingDate,
                shippingSlotInDay = shippingSlot.slotExtension?.daySlotId.toString(),
                shippingSlotDescription = shippingSlot.getTimeDescription())

        handleCreateShippingInformation(this.deliveryOption, subscribeCheckOut)
    }
    // endregion

    override fun onBackPressed() {
        backPressed()
    }

    private fun startCreatedOrderFragment() {
        languageButton.visibility = View.VISIBLE
        val cacheCart = ArrayList<CacheCartItem>()
        cacheCart.addAll(cacheCartItems)
        startFragment(PaymentCreatedOrder.newInstance())
        clearCachedCart() // clear cache item
    }

    private fun startSuccessfullyFragment(orderId: String, urlRedirect: String) {
        languageButton.visibility = View.VISIBLE
        val cacheCart = ArrayList<CacheCartItem>()
        cacheCart.addAll(cacheCartItems)
        startFragment(PaymentSuccessFragment.newInstance(orderId, cacheCart, urlRedirect))
        clearCachedCart() // clear cache item
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

    private fun startBilling(pwbMemberIndex: Int) {
        val fragment = PaymentBillingFragment.newInstance(pwbMemberIndex)
        startFragment(fragment)
    }

    private fun startSelectPaymentMethod() {
        val fragment = PaymentSelectMethodFragment.newInstance(deliveryOption.methodCode)
        startFragment(fragment)
    }

    private fun startBilling(member: HDLCustomerInfos?) {
        val fragment = if (member != null) PaymentBillingFragment.newInstance(member) else
            PaymentBillingFragment.newInstance()
        startFragment(fragment)
    }

    private fun startBilling(member: Member?) {
        val fragment = if (member != null) PaymentBillingFragment.newInstance(member) else
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
                .commitAllowingStateLoss()
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

    private fun dismissProgressDialog() {
        mProgressDialog?.dismiss()
    }

    private fun getItemTotal() {
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).viewCartTotal(cartId, object : ApiResponseCallback<CartTotalResponse> {
                override fun success(response: CartTotalResponse?) {
                    runOnUiThread {
                        if (response != null) {
                            handleGetCartItemsSuccess(response)
                        } else {
                            showCommonDialog(getString(R.string.cannot_get_cart_item))
                        }
                        mProgressDialog?.dismiss()
                    }
                }

                override fun failure(error: APIError) {
                    runOnUiThread {
                        mProgressDialog?.dismiss()
                        showCommonAPIErrorDialog(error)
                    }
                }
            })
        }
    }

    private fun handleGetCartItemsSuccess(cartTotal: CartTotalResponse) {
        this.shoppingCartItem = (cartTotal.items ?: arrayListOf()).checkItemsBy(cacheCartItems)
        this.totalPrice = cartTotal.totalPrice
        val discount = cartTotal.totalSegment?.firstOrNull { it.code == TotalSegment.DISCOUNT_KEY }
        if (discount != null) {
            this.discountPrice = discount.value.toStringDiscount()
        }
        val coupon = cartTotal.totalSegment?.firstOrNull { it.code == TotalSegment.COUPON_KEY }
        if (coupon != null) {
            val couponDiscount = TotalSegment.getCouponDiscount(coupon.value)
            this.promotionDiscount = couponDiscount?.couponAmount.toStringDiscount()
            this.discountPrice -= promotionDiscount
            this.totalPrice -= promotionDiscount
        }
        if (discountPrice > 0) {
            this.totalPrice -= discountPrice
        }

        // check retrieving eodering customer information
        val isEorderingMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_EORDERING_MEMBER_ON)
        val isT1CMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_T1C_MEMBER_ON)
        val isHDLMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_HDL_MEMBER_ON)

        if (!isEorderingMemberOn && !isT1CMemberOn && !isHDLMemberOn) { // all have no enable
            startBilling()
        } else {
            startCheckOut() // default page
        }
    }

    fun showAlertDialogCheckSkip(message: String, checkSkip: Boolean) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
        if (checkSkip) {
            builder.setPositiveButton(android.R.string.ok) { _, _ -> startBilling() }
            builder.setNegativeButton(android.R.string.cancel) { _, _ -> startCheckOut() }
        } else {
            builder.setPositiveButton(android.R.string.ok) { _, _ -> startCheckOut() }
        }
        builder.show()
    }

    private fun showAlertConfirmPayment(paymentMethod: PaymentMethod, payerName: String = "",
                                        payerEmail: String = "", agentCode: String = "",
                                        agentChannelCode: String = "", mobileNumber: String = "") {

        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(getString(R.string.confirm_oder))
                .setPositiveButton(resources.getString(R.string.ok_alert)) { _, _ ->
                    // tracking payment method
                    analytics.trackSelectPayment(paymentMethod.code)
                    val bodyRequest = createPaymentBodyRequest(paymentMethod, payerName, payerEmail,
                            agentCode, agentChannelCode, mobileNumber)
                    updateOrder(bodyRequest)
                }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, _ ->
                    dialog.dismiss()
                    mProgressDialog?.dismiss()
                }
                .setCancelable(false)
        builder.show()
    }

    private fun handleCreateShippingInformation(deliveryOption: DeliveryOption, addressInfoExtensionBody: AddressInfoExtensionBody) {
        if (cartId != null && shippingAddress != null) {
            when (val type = DeliveryType.fromString(deliveryOption.methodCode)) {
                STORE_PICK_UP -> createShippingInforWithClickAndCollect(type, addressInfoExtensionBody)
                else -> createShippingInfor(type, addressInfoExtensionBody)
            }
        }
    }

    private fun createShippingInfor(type: DeliveryType?, addressInfoExtensionBody: AddressInfoExtensionBody) {
        type ?: return // type null?
        HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, shippingAddress!!,
                billingAddress ?: shippingAddress!!, addressInfoExtensionBody,
                deliveryOption, object : ApiResponseCallback<ShippingInformationResponse> {
            override fun success(response: ShippingInformationResponse?) {
                if (response != null) {
                    handleShippingInforSuccess(type, response.paymentMethods)
                } else {
                    showCommonDialog(resources.getString(R.string.some_thing_wrong))
                    mProgressDialog?.dismiss()
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                showCommonAPIErrorDialog(error)
            }
        })
    }

    private fun createShippingInforWithClickAndCollect(type: DeliveryType?, addressInfoExtensionBody: AddressInfoExtensionBody) {
        if (branch == null || type == null) {
            return
        }
        val storeAddress = AddressInformation.createBranchAddress(branch!!)
        HttpManagerMagento(this, true)
                .createShippingInformation(cartId!!, storeAddress, billingAddress
                        ?: shippingAddress!!, addressInfoExtensionBody, deliveryOption, // if shipping at store, BillingAddress is ShippingAddress
                        object : ApiResponseCallback<ShippingInformationResponse> {
                            override fun success(response: ShippingInformationResponse?) {
                                if (response != null) {
                                    handleShippingInforSuccess(type, response.paymentMethods)
                                } else {
                                    showCommonDialog(resources.getString(R.string.some_thing_wrong))
                                    mProgressDialog?.dismiss()
                                }
                            }

                            override fun failure(error: APIError) {
                                mProgressDialog?.dismiss()
                                showCommonAPIErrorDialog(error)
                            }
                        })
    }

    private fun handleShippingInforSuccess(type: DeliveryType, paymentMethods: ArrayList<PaymentMethod>) {
        if (type == HOME) {
            createBookingHomeDelivery(paymentMethods)
            return
        }

        displayEorderingPayment(paymentMethods)
    }

    private fun createBookingHomeDelivery(paymentMethods: ArrayList<PaymentMethod>) {
        showProgressDialog()
        shippingSlot?.let {
            HomeDeliveryApi().createBookingSlot(this, cartId!!, it, object : ApiResponseCallback<ShippingSlot> {
                override fun success(response: ShippingSlot?) {
                    displayEorderingPayment(paymentMethods)
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    showCommonAPIErrorDialog(error)
                }
            })
        }
    }

    private fun displayEorderingPayment(paymentMethods: ArrayList<PaymentMethod>) {
        if (fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_PAYMENT_OPTIONS_ON)) { // payment on?
            selectPaymentTypes(paymentMethods)
        } else {
            showAlertConfirmPayment(paymentMethod)
        }

        mProgressDialog?.dismiss()
    }

    private fun selectPaymentTypes(paymentMethodsFromAPI: ArrayList<PaymentMethod>) {
        if (paymentMethodsFromAPI.isEmpty()) {
            showCommonDialog(getString(R.string.not_found_payment_methods))
        } else {
            this.paymentMethods = filterPaymentMethods(paymentMethodsFromAPI)
            val isEorderingPaymentOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_EORDERING_PAYMENT_ON)
            if (isEorderingPaymentOn && this.paymentMethods.firstOrNull { it.code == PaymentMethod.E_ORDERING } == null) {
                this.paymentMethods.add(PaymentMethod(title = getString(R.string.pay_here), code = PaymentMethod.E_ORDERING))
            }
            startSelectPaymentMethod()
        }
    }

    private fun filterPaymentMethods(methods: ArrayList<PaymentMethod>): ArrayList<PaymentMethod> {
        val supportedPaymentMethods = fbRemoteConfig.getString(RemoteConfigUtils.CONFIG_KEY_SUPPORTED_PAYMENT_METHODS)
        Log.d(TAG, supportedPaymentMethods)
        val result = methods.filter {
            supportedPaymentMethods.contains(it.code, true)
        }
        return ArrayList(result)
    }

    private fun startRetrieveMemberContact(memberContact: String) {
        val isEorderingMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_EORDERING_MEMBER_ON)
        val isT1CMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_T1C_MEMBER_ON)
        val isHDLMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_HDL_MEMBER_ON)

        when {
            isEorderingMemberOn -> getEorderingMember(memberContact)
            isHDLMemberOn -> getHDLMember(memberContact)
            isT1CMemberOn -> getMembersT1C(memberContact)
        }
    }

    private fun getEorderingMember(mobile: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getPWBCustomer(mobile,
                object : ApiResponseCallback<List<EOrderingMember>> {
                    override fun success(response: List<EOrderingMember>?) {
                        runOnUiThread {
                            if (response != null && response.isNotEmpty()) { // it can be null
                                this@PaymentActivity.eOrderingMembers = response
                                analytics.trackCustomerSource(CustomerSource.BU)
                                mProgressDialog?.dismiss()
                                startMembersFragment()
                            } else {
                                getHDLMember(mobile)
                            }
                        }
                    }

                    override fun failure(error: APIError) {
                        runOnUiThread {
                            getHDLMember(mobile)
                            Log.d("Payment", error.errorCode ?: "")
                        }
                    }
                })
    }

    private fun getHDLMember(number: String) {
        val isHDLMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_HDL_MEMBER_ON)
        if (!isHDLMemberOn) { // hdl on?
            getMembersT1C(number)
            return
        }

        showProgressDialog()
        HttpManagerHDL.getInstance(this).getHDLCustomer(number, object : ApiResponseCallback<HDLMemberResponse> {
            override fun success(response: HDLMemberResponse?) {
                if (response?.customerInfos != null) {
                    this@PaymentActivity.membersHDL = response.customerInfos ?: listOf()
                    analytics.trackCustomerSource(CustomerSource.HDL)
                    mProgressDialog?.dismiss()
                    startMembersFragment()
                } else {
                    getMembersT1C(number)
                    Log.d("Payment", "not found HDL customer")
                }
            }

            override fun failure(error: APIError) {
                getMembersT1C(number)
                Log.d("Payment", error.errorCode ?: "")
            }
        })
    }

    private fun getMembersT1C(mobile: String) {
        val isT1CMemberOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_T1C_MEMBER_ON)
        if (!isT1CMemberOn) { // t1c on?
            showAlertDialogCheckSkip(resources.getString(R.string.not_have_user), true)
            mProgressDialog?.dismiss()
            return
        }

        showProgressDialog()
        HttpMangerSiebel.getInstance(this).verifyMemberFromT1C(mobile, " ",
                object : ApiResponseCallback<List<MemberResponse>> {
                    override fun success(response: List<MemberResponse>?) {
                        mProgressDialog?.dismiss()
                        // is PaymentCheckOutFragment?
                        if (currentFragment is PaymentCheckOutFragment) {
                            if (response != null && response.isNotEmpty()) {
                                this@PaymentActivity.membersList = response
                                analytics.trackCustomerSource(CustomerSource.T1)
                                startMembersFragment()
                            } else {
                                showAlertDialogCheckSkip(resources.getString(R.string.not_have_user), true)
                            }
                        }

                    }

                    override fun failure(error: APIError) {
                        if (!isFinishing) {
                            mProgressDialog?.dismiss()

                            if (error.errorCode == null) {
                                showCommonDialog(resources.getString(R.string.some_thing_wrong))
                            } else {
                                showAlertDialogCheckSkip(resources.getString(R.string.not_have_user), true)
                            }
                        }
                    }
                })
    }

    private fun getMembersT1CChanged(mobile: String) {
        HttpMangerSiebel.getInstance(this).verifyMemberFromT1C(mobile, " ",
                object : ApiResponseCallback<List<MemberResponse>> {
                    override fun success(response: List<MemberResponse>?) {
                        mProgressDialog?.dismiss()
                        if (response != null && response.isNotEmpty()) {
                            this@PaymentActivity.membersList = response
                            Log.d(TAG, "${response.size}")
                            T1MemberDialogFragment.newInstance().show(supportFragmentManager,
                                    T1MemberDialogFragment.TAG_FRAGMENT)
                        } else {
                            showCommonDialog(getString(R.string.not_found_data))
                        }
                    }

                    override fun failure(error: APIError) {
                        if (!isFinishing) {
                            mProgressDialog?.dismiss()
                            if (error.errorCode == null) {
                                showCommonDialog(getString(R.string.not_connected_network))
                            } else {
                                showCommonDialog(getString(R.string.not_found_data))
                            }
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
                    response.cardNo = theOneCardNo
                    startBilling(response)
                } else {
                    showAlertDialogCheckSkip(resources.getString(R.string.some_thing_wrong), false)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                if (error.errorCode == null) {
                    showCommonDialog(getString(R.string.not_connected_network))
                } else {
                    showAlertDialogCheckSkip(resources.getString(R.string.some_thing_wrong), false)
                }
            }
        })
    }

    private fun getDeliveryOptions(cartId: String) {
        shippingAddress?.let {
            HttpManagerMagento.getInstance(this).getOrderDeliveryOptions(cartId, it,
                    object : ApiResponseCallback<List<DeliveryOption>> {
                        override fun success(response: List<DeliveryOption>?) {
                            if (response != null) {
                                this@PaymentActivity.shippingSlot = null // clear shipping slot
                                deliveryOptionsList = filterDeliveryOptions(response)
                                startDeliveryOptions()
                            } else {
                                showCommonDialog(getString(R.string.some_thing_wrong))
                            }
                            mProgressDialog?.dismiss()
                        }

                        override fun failure(error: APIError) {
                            mProgressDialog?.dismiss()
                            showCommonAPIErrorDialog(error)
                        }
                    })
        }
    }

    private fun filterDeliveryOptions(response: List<DeliveryOption>): List<DeliveryOption> {
        val supportedDeliveryOptions = fbRemoteConfig.getString(RemoteConfigUtils.CONFIG_KEY_SUPPORTED_DELIVERY_METHODS)
        return response.filter {
            supportedDeliveryOptions.contains(it.methodCode, true)
        }
    }

    private fun getStoresDelivery(deliveryOption: DeliveryOption) {
        this.branches = arrayListOf() // clear branch list
        when (BuildConfig.FLAVOR) {
            "pwb" -> {
                loadBranches()
            }
            else -> {
                handlePickupLocationList(deliveryOption.extension.pickupLocations)
            }
        }
    }

    /**
     * FOR CDS using pickup locations (we cast it to branch model)
     */
    private fun handlePickupLocationList(pickupLocations: List<PickupLocation>) {
        pickupLocations.mapTo(branches) { BranchResponse(branch = it.asBranch()) } // add pickup location to branch
        startStorePickupFragment()

        // TODO: handle sort with staff's storeID

        mProgressDialog?.dismiss()
    }

    private fun loadBranches() {
        BranchApi().getBranchesSTS(this, object : ApiResponseCallback<List<BranchResponse>> {
            override fun success(response: List<BranchResponse>?) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    if (response != null) {
                        // Now Display all store and sort by storeId
                        response.sortedWith(compareBy { it.branch.storeId.toInt() }).forEach {
                            branches.add(it)
                        }
                        startStorePickupFragment()
                    } else {
                        showCommonDialog(getString(R.string.some_thing_wrong))
                    }
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    showCommonAPIErrorDialog(error)
                }
            }
        })
    }


    /**
     * @param payerName
     * @param agentCode
     * @param agentChannelCode
     * @param mobileNumber
     * Must set if payment type p2c2p_123(bank transfer and counter service)
     * */
    private fun createPaymentBodyRequest(paymentMethod: PaymentMethod,
                                         payerName: String = "",
                                         payerEmail: String = "",
                                         agentCode: String = "",
                                         agentChannelCode: String = "",
                                         mobileNumber: String = ""): PaymentInfoBody {
        val email = shippingAddress?.email ?: ""
        val staffId = userInformation?.user?.staffId ?: ""
        val retailerId = userInformation?.store?.retailerId ?: ""
        val addressInfo = if (billingAddress == null) shippingAddress else billingAddress

        return when (paymentMethod.code) {
            PaymentMethod.BANK_AND_COUNTER_SERVICE -> {
                PaymentInfoBody.createPaymentInfoBody(cartId = cartId!!,
                        staffId = staffId, retailerId = retailerId, email = email,
                        payerName = payerName, customerEmail = payerEmail, agentCode = agentCode,
                        agentChannelCode = agentChannelCode, mobileNumber = mobileNumber,
                        billingAddress = addressInfo!!, paymentMethod = paymentMethod,
                        theOneCardNo = theOneCardNo)
            }
            PaymentMethod.FULL_PAYMENT, PaymentMethod.INSTALLMENT -> {
                PaymentInfoBody.createPaymentInfoBody(cartId = cartId!!,
                        staffId = staffId, retailerId = retailerId, email = email,
                        customerEmail = email, billingAddress = addressInfo!!,
                        paymentMethod = paymentMethod, theOneCardNo = theOneCardNo)
            }
            else -> {
                // Standard
                PaymentInfoBody.createPaymentInfoBody(cartId = cartId!!,
                        staffId = staffId, retailerId = retailerId, email = email,
                        billingAddress = addressInfo!!,
                        paymentMethod = paymentMethod, theOneCardNo = theOneCardNo)
            }
        }
    }

    private fun updateOrder(bodyRequest: PaymentInfoBody) {
        if (cartId == null) {
            return
        }
        showProgressDialog()
        val retailerId = userInformation?.store?.retailerId ?: ""

        OrderApi().updateOrder(this, cartId!!, bodyRequest, object : OrderApi.CreateOderCallback {
            override fun onSuccess(oderId: String?) {
                analytics.trackOrderSuccess(paymentMethod.code, deliveryOption.methodCode, retailerId)
                runOnUiThread {
                    if (oderId != null) {
                        hideBackButton()
                        if (oderId == HttpManagerMagento.OPEN_ORDER_CREATED_PAGE) {
                            startCreatedOrderFragment()
                            mProgressDialog?.dismiss()
                        } else {
                            getOrder(oderId)
                        }
                    } else {
                        mProgressDialog?.dismiss()
                        showCommonDialog(getString(R.string.some_thing_wrong))
                    }
                }
            }

            override fun onSuccessAndRedirect(orderId: String?, url: String) {
                analytics.trackOrderSuccess(paymentMethod.code, deliveryOption.methodCode, retailerId)
                runOnUiThread {
                    if (orderId != null) {
                        hideBackButton()
                        getOrder(orderId, url)
                    } else {
                        mProgressDialog?.dismiss()
                        showCommonDialog(getString(R.string.some_thing_wrong))
                    }
                }
            }

            override fun onFailure(error: APIError) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    showCommonAPIErrorDialog(error)
                }
            }
        })
    }

    fun getOrder(orderId: String, urlRedirect: String = "") {
        startSuccessfullyFragment(orderId, urlRedirect)
        mProgressDialog?.dismiss()
    }

    // region {@link PaymentProtocol}
    override fun getItems(): List<ShoppingCartItem> = this.shoppingCartItem

    override fun getDiscount(): Double = this.discountPrice

    override fun getPromotionDiscount(): Double = this.promotionDiscount

    override fun getTotalPrice(): Double = this.totalPrice

    override fun getHDLMembers(): List<HDLCustomerInfos> = this.membersHDL

    override fun getPWBMembers(): List<EOrderingMember> = this.eOrderingMembers

    override fun getPWBMemberByIndex(index: Int): EOrderingMember? {
        return if (eOrderingMembers.isNotEmpty()) {
            this.eOrderingMembers[index]
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

    override fun getBranches(): ArrayList<BranchResponse> = this.branches

    override fun getSelectedBranch(): Branch? = this.branch

    override fun getPaymentMethods(): List<PaymentMethod> = this.paymentMethods

    override fun getT1CardNumber(): String = this.theOneCardNo

    override fun getCheckType(): CheckoutType = this.checkoutType

    override fun getPaymentAgents(): List<PaymentAgent> = this.paymentAgents
    // endregion

    // region {@link StorePickUpListener}
    override fun onUpdateStoreDetail(branch: BranchResponse) {
        if (currentFragment is DeliveryStorePickUpFragment) {
            (currentFragment as DeliveryStorePickUpFragment).updateStoreDetail(branch)
        }
    }

    override fun onSelectedStore(branch: Branch) {
        this.branch = branch
        userInformation?.let { userInformation ->
            if (userInformation.user != null && userInformation.store != null) {
                showProgressDialog()
                // TODO: Refactor checking app flavor
                if (BuildConfig.FLAVOR == "pwb") {
                    val storePickup = StorePickup(branch.storeId)
                    val subscribeCheckOut = AddressInfoExtensionBody(
                            checkout = shippingAddress!!.email,
                            storePickup = storePickup)
                    handleCreateShippingInformation(this.deliveryOption, subscribeCheckOut)
                } else { // cds, rbs?
                    val pickerInfo = PickerInfo(
                            firstName = shippingAddress!!.firstname,
                            lastName = shippingAddress!!.lastname,
                            email = shippingAddress!!.email,
                            telephone = shippingAddress!!.telephone)
                    val subscribeCheckOut = AddressInfoExtensionBody(
                            checkout = shippingAddress!!.email,
                            pickupLocationId = branch.storeId,
                            pickerInfo = pickerInfo)
                    handleCreateShippingInformation(this.deliveryOption, subscribeCheckOut)
                }
            }
        }
    }

    override fun addProduct2hToCart(branchResponse: BranchResponse) {
        showProgressDialog()
        product2h?.let {
            analytics.trackAddToCart(it.sku, "1h_pickup")

            CartUtils(this).addProduct2hToCart(it, branchResponse, branches,
                    object : AddProductToCartCallback {
                        override fun onSuccessfully() {
                            mProgressDialog?.dismiss()
                            ShoppingCartActivity.startActivity(this@PaymentActivity)
                            finish()
                        }

                        override fun forceClearCart() {
                            //TODO: show dialog for clear cart
                            mProgressDialog?.dismiss()
                        }

                        override fun onFailure(messageError: String) {
                            showCommonDialog(messageError)
                            mProgressDialog?.dismiss()
                        }

                        override fun onFailure(dialog: Dialog) {
                            dialog.show()
                            mProgressDialog?.dismiss()
                        }
                    })
        }
    }

    // State Prduct 2h edit store pickup
    override fun onProduct2hEditStorePickup(branchResponse: BranchResponse) {
        showProgressDialog()
        CartUtils(this).editStore2hPickup(branchResponse, object : EditStorePickupCallback {
            override fun onSuccessfully() {
                mProgressDialog?.dismiss()
                ShoppingCartActivity.startActivity(this@PaymentActivity)
                finish()
            }

            override fun onFailure(messageError: String) {
                mProgressDialog?.dismiss()
                showCommonDialog(messageError)
            }
        })
    }
    // endregion

    private fun setupRemoteConfig() {
        fbRemoteConfig = FirebaseRemoteConfig.getInstance()

        if (!BuildConfig.IS_PRODUCTION) { // is Production?
            cacheExpiration = 0
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .build()
        fbRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        fbRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun fetchEorderingConfig() {
        showProgressDialog()
        fbRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "remote config -> fetch Successful")
            } else {
                Log.i(TAG, "remote config -> fetch Fail")
            }
            standardCheckout()
        }
    }

    private fun createOrderWithIspu() {
        val cacheCartItems = database.cacheCartItems
        val item = cacheCartItems.find { it.branch != null }
        if (item != null) {
            this.branch = item.branch
            this.deliveryOption = DeliveryOption.getStorePickupIspu() // create DeliveryOption Store Pickup ISPU

            val storePickup = StorePickup(this.branch!!.storeId)
            val subscribeCheckOut = AddressInfoExtensionBody(checkout = shippingAddress!!.email,
                    storePickup = storePickup)
            createShippingInforWithClickAndCollect(STORE_PICK_UP_ISPU, subscribeCheckOut)
        }
    }

    private fun getSpecialSKUList(): List<Long>? {
        return this.specialSKUList
                ?: ReadFileHelper<List<Long>>().parseRawJson(this@PaymentActivity, R.raw.special_sku,
                        object : TypeToken<List<Long>>() {}.type, null)
    }

    // Get Bank Channels and Counter
    private fun retrievePaymentInformation() {
        cartId ?: return
        showProgressDialog()
        PaymentApi().retrievePaymentInformation(this, cartId!!,
                object : ApiResponseCallback<List<PaymentAgent>> {
                    override fun success(response: List<PaymentAgent>?) {
                        if (response != null) {
                            this@PaymentActivity.paymentAgents = response
                        }
                        startFragment(PaymentTransfersFragment())
                        dismissProgressDialog()
                    }

                    override fun failure(error: APIError) {
                        dismissProgressDialog()
                    }
                })
    }

    private fun backPressed() {
        if (currentFragment is PaymentTransfersFragment) {
            startSelectPaymentMethod()
            return
        }

        if (currentFragment is DeliveryStorePickUpFragment) {
            // is state of checkout with product 2h?
            if (product2h != null || isEditStorePickup) {
                finish()
            } else {
                startDeliveryOptions()
            }
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
            resetData()
            if (this.membersList.isNotEmpty() || this.eOrderingMembers.isNotEmpty()) {
                startMembersFragment()
            } else {
                if (this.memberContact.isNullOrEmpty()) {
                    finish()
                    return
                }
                startCheckOut()
            }
            return
        }

        if (currentFragment is PaymentSelectMethodFragment) {
            if (checkoutType == CheckoutType.ISPU) {
                startBilling()
            } else {
                startDeliveryOptions()
            }
            return
        }

        if (currentFragment is PaymentMembersFragment) {
            this.membersList = listOf()
            this.eOrderingMembers = listOf()
            startCheckOut()
            return
        }

        if (currentFragment is PaymentCheckOutFragment) {
            hideKeyboard()
            finish()
            return
        }
    }

    private fun resetData() {
        this.shippingAddress = null
        this.billingAddress = null
        this.theOneCardNo = ""
    }

    private fun hideBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mToolbar?.setNavigationOnClickListener(null)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputManager.hideSoftInputFromWindow(it.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(it.windowToken, 0)
        }
    }

    private fun clearCachedCart() {
        preferenceManager.clearCartId()
        database.deleteAllCacheCartItem()
        Log.d("Order Success", "Cleared cached CartId and CartItem")
    }
}

enum class CheckoutType {
    NORMAL, ISPU
}