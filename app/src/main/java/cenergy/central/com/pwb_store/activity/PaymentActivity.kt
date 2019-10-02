package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
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
import cenergy.central.com.pwb_store.BuildConfig
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
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.api.BranchApi
import cenergy.central.com.pwb_store.manager.api.HomeDeliveryApi
import cenergy.central.com.pwb_store.manager.api.OrderApi
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.DeliveryType.*
import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.AddProductToCartCallback
import cenergy.central.com.pwb_store.utils.CartUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.showCommonDialog
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import com.google.gson.reflect.TypeToken

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
    private var cartItemList: List<CartItem> = listOf()
    private lateinit var cartTotal: CartTotalResponse
    private var membersList: List<MemberResponse> = listOf()
    private var pwbMembersList: List<PwbMember> = listOf()
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
    private var paymentMethods = listOf<PaymentMethod>()
    private val paymentMethod = PaymentMethod("e_ordering", "Pay at store")
    private var shippingSlot: ShippingSlot? = null
    // data product 2h
    private var product2h: Product? = null
    private var checkoutType: CheckoutType = CheckoutType.NORMAL

    companion object {
        private const val EXTRA_PRODUCT_2H = "extra_product_2h"
        private const val EXTRA_CHECK_OUT_ISPU = "extra_check_out_ispu"

        fun startCheckout(context: Context, isISPU: Boolean = false) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_CHECK_OUT_ISPU, isISPU)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }

        fun startSelectStore(context: Context, product: Product) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_2H, product)
            intent.putExtra(EXTRA_CHECK_OUT_ISPU, true)
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

        // is start select store with product 2h?
        if (intent.hasExtra(EXTRA_PRODUCT_2H)) {
            this.product2h = intent.getParcelableExtra(EXTRA_PRODUCT_2H)
            this.checkoutType = CheckoutType.ISPU
            checkoutWithProduct2hr(this.product2h!!)
        } else {
            val isISPU = intent.getBooleanExtra(EXTRA_CHECK_OUT_ISPU, false)
            this.checkoutType = if (isISPU) CheckoutType.ISPU else CheckoutType.NORMAL
            standardCheckout()
        }
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

    override fun setBillingAddressWithIspu(billingAddress: AddressInformation) {
        this.shippingAddress = billingAddress // sent only address box 1
        createOrderWithIspu()
    }
    // endregion

    // region {@link DeliveryOptionsListener}
    override fun onSelectedOptionListener(deliveryOption: DeliveryOption) {
        this.deliveryOption = deliveryOption
        deliveryType = DeliveryType.fromString(deliveryOption.methodCode)
        when (deliveryType) {
            EXPRESS, STANDARD -> {
                showProgressDialog()
                val subscribeCheckOut = SubscribeCheckOut(shippingAddress!!.email,
                        null, null, null)
                handleCreateShippingInformation(false, subscribeCheckOut)
            }
            STORE_PICK_UP -> {
                showProgressDialog()
                getStoresDelivery(deliveryOption) // default page
            }
            HOME -> {
                this.enableShippingSlot = deliveryOption.extension.shippingSlots
                startDeliveryHomeFragment()
            }
        }
    }

    private fun standardCheckout() {
        cacheCartItems = database.cacheCartItems
        paymentMethods = cacheCartItems.getPaymentType(this)
        startCheckOut() // default page
        getCartItems()
    }

    private fun checkoutWithProduct2hr(product: Product) {
        showProgressDialog()
        if (database.provinces != null && database.provinces.isNotEmpty()) {
            BranchApi().getBranchesISPU(this, product.sku,
                    object : ApiResponseCallback<List<BranchResponse>> {
                        override fun success(response: List<BranchResponse>?) {
                            if (response != null) {
                                branches.clear()
                                branches.addAll(response)
                                val fragment = DeliveryStorePickUpFragment.newInstance(true)
                                startFragment(fragment)
                            }

                            mProgressDialog?.dismiss()
                        }

                        override fun failure(error: APIError) {
                            showCommonDialog(null, getString(R.string.some_thing_wrong),
                                    DialogInterface.OnClickListener { dialog, which ->
                                        dialog?.dismiss()
                                        finish()
                                    })
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
    override fun onPaymentTypeClickListener(paymentMethod: PaymentMethod) {
        showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethod)
    }
    // endregion

    // region {@link HomeDeliveryListener}
    override fun onPaymentClickListener(shippingSlot: ShippingSlot, date: Int, month: Int, year: Int, shippingDate: String) {
        if (shippingAddress == null) return

        showProgressDialog()
        this.shippingSlot = shippingSlot // set shipping slot
        val subscribeCheckOut = SubscribeCheckOut(shippingAddress!!.email,
                shippingDate, shippingSlot.slotExtension.daySlotId.toString(),
                shippingSlot.getTimeDescription())

        handleCreateShippingInformation(false, subscribeCheckOut)
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

    private fun startSuccessfullyFragment(orderId: String) {
        languageButton.visibility = View.VISIBLE
        val cacheCart = ArrayList<CacheCartItem>()
        cacheCart.addAll(cacheCartItems)
        startFragment(PaymentSuccessFragment.newInstance(orderId, cacheCart))
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

    private fun startSelectMethod() {
        val fragment = PaymentSelectMethodFragment.newInstance()
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

    private fun getCartItems() {
        cacheCartItems[0].cartId?.let { cartId ->
            CartUtils(this).viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
                override fun success(response: List<CartItem>?) {
                    if (response != null) {
                        cartItemList = response
                        getItemTotal(cartId)
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    DialogHelper(this@PaymentActivity).showErrorDialog(error)
                }
            })
        }
    }

    fun getItemTotal(cartId: String){
        CartUtils(this).viewCartTotal(cartId, object : ApiResponseCallback<CartTotalResponse>{
            override fun success(response: CartTotalResponse?) {
                mProgressDialog?.dismiss()
                if (response != null) {
                    cartTotal = response
                } else {
                    showAlertDialog("", resources.getString(R.string.cannot_get_cart_item))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@PaymentActivity).showErrorDialog(error)
            }
        })
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
                .setPositiveButton(getString(R.string.ok_alert)) { dialog, _ ->
                    dialog.dismiss()
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

    private fun handleCreateShippingInformation(isClickAndCollect: Boolean, subscribeCheckOut: SubscribeCheckOut) {
        if (cartId != null && shippingAddress != null) {
            when (val type = DeliveryType.fromString(this.deliveryOption.methodCode)) {
                STORE_PICK_UP -> createShippingInforWithClickAndCollect(type, subscribeCheckOut)
                else -> createShippingInfor(type, subscribeCheckOut)
            }
        }
    }

    private fun createShippingInfor(type: DeliveryType?, subscribeCheckOut: SubscribeCheckOut) {
        type ?: return // type null?
        HttpManagerMagento.getInstance(this).createShippingInformation(cartId!!, shippingAddress!!,
                billingAddress ?: shippingAddress!!, subscribeCheckOut,
                deliveryOption, object : ApiResponseCallback<ShippingInformationResponse> {
            override fun success(response: ShippingInformationResponse?) {
                mProgressDialog?.dismiss()
                if (response != null) {
                    handleShippingInforSuccess(type, response.paymentMethods)
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

    private fun createShippingInforWithClickAndCollect(type: DeliveryType?, subscribeCheckOut: SubscribeCheckOut) {
        if (branch == null || type == null) {
            return
        }
        val storeAddress = AddressInformation.createBranchAddress(branch!!)
        HttpManagerMagento(this, true)
                .createShippingInformation(cartId!!, storeAddress, billingAddress
                        ?: shippingAddress!!, subscribeCheckOut, deliveryOption, // if shipping at store, BillingAddress is ShippingAddress
                object : ApiResponseCallback<ShippingInformationResponse> {
                    override fun success(response: ShippingInformationResponse?) {
                        mProgressDialog?.dismiss()
                        if (response != null) {
                            handleShippingInforSuccess(type, response.paymentMethods)
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

    private fun handleShippingInforSuccess(type: DeliveryType, paymentMethods: ArrayList<PaymentMethod>) {
        if (type == HOME) {
            createBookingHomeDelivery(paymentMethods)
            return
        }

        // TODO: remove function verify user chatandshop
        if (isUserChatAndShop()) {
            selectPaymentTypes(paymentMethods)
        } else {
            showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethod)
        }
    }

    private fun createBookingHomeDelivery(paymentMethods: ArrayList<PaymentMethod>) {
        //TODO-HDL:  Change to new api
        showProgressDialog()
        shippingSlot?.let {
            HomeDeliveryApi().createBookingSlot(this, cartId!!, it, object : ApiResponseCallback<ShippingSlot> {
                override fun success(response: ShippingSlot?) {
                    // TODO: remove function verify user chatandshop
                    if (isUserChatAndShop()) {
                        selectPaymentTypes(paymentMethods)
                    } else {
                        showAlertCheckPayment("", resources.getString(R.string.confirm_oder), paymentMethod)
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    DialogHelper(this@PaymentActivity).showErrorDialog(error)
                }
            })
        }
    }

    private fun selectPaymentTypes(paymentMethodsFromAPI: ArrayList<PaymentMethod>) {
        if (this.paymentMethods.isEmpty()) {
            if (paymentMethodsFromAPI.isEmpty()) {
                showFinishActivityDialog("", getString(R.string.not_found_payment_methods))
            } else {
                this.paymentMethods = paymentMethodsFromAPI
                startSelectMethod()
            }
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
                if (!isFinishing) {
                    mProgressDialog?.dismiss()
                    if (error.errorCode == null) {
                        showAlertDialog("", getString(R.string.not_connected_network))
                    } else {
                        showAlertDialogCheckSkip("", resources.getString(R.string.not_have_user), true)
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
                                this@PaymentActivity.shippingSlot = null // clear shipping slot
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

    private fun getStoresDelivery(deliveryOption: DeliveryOption) {
        this.branches = arrayListOf() // clear branch list
        when (BuildConfig.FLAVOR) {
            "cds" -> {
                handlePickupLocationList(deliveryOption.extension.pickupLocations)
            }
            else -> loadBranches()
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
                    if (response != null && userInformation != null) {
                        val branch = response.firstOrNull { it.branch.storeId == userInformation!!.store?.storeId.toString() }
                        if (branch != null) {
                            branches.add(branch)
                            response.sortedWith(compareBy { it.branch.storeId.toInt() }).forEach {
                                if (it.branch.storeId != userInformation!!.store?.storeId.toString()) branches.add(it)
                            }
                        } else {
                            response.sortedWith(compareBy { it.branch.storeId.toInt() }).forEach {
                                branches.add(it)
                            }
                        }
                        startStorePickupFragment()
                    } else {
                        showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                    }
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    DialogHelper(this@PaymentActivity).showErrorDialog(error)
                }
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
        val retailerId = userInformation?.store?.retailerId ?: ""

        val addressInfo = if (billingAddress == null) shippingAddress else billingAddress
        OrderApi().updateOrder(this, cartId!!, staffId, retailerId, paymentMethod, email,
                addressInfo!!, object : ApiResponseCallback<String> {
            override fun success(response: String?) {
                runOnUiThread {
                    if (response != null) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        mToolbar?.setNavigationOnClickListener(null)
                        if (response == HttpManagerMagento.OPEN_ORDER_CREATED_PAGE) {
                            startCreatedOrderFragment()
                            mProgressDialog?.dismiss()
                        } else {
                            getOrder(response)
                        }
                    } else {
                        mProgressDialog?.dismiss()
                        showAlertDialog("", resources.getString(R.string.some_thing_wrong))
                    }
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    mProgressDialog?.dismiss()
                    DialogHelper(this@PaymentActivity).showErrorDialog(error)
                }
            }
        })
    }

    fun getOrder(orderId: String) {
        startSuccessfullyFragment(orderId)
        mProgressDialog?.dismiss()
    }

    // region {@link PaymentProtocol}
    override fun getItems(): List<CartItem> = this.cartItemList

    override fun getCartTotalResponse(): CartTotalResponse = this.cartTotal

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

    override fun getBranches(): ArrayList<BranchResponse> = this.branches

    override fun getSelectedBranch(): Branch? = this.branch

    override fun getPaymentMethods(): List<PaymentMethod> = this.paymentMethods

    override fun getCheckType(): CheckoutType = this.checkoutType
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
                val storePickup = StorePickup(branch.storeId.toInt())
                val subscribeCheckOut = SubscribeCheckOut(shippingAddress!!.email, null,
                        null, null, storePickup)
                // store shipping this case can be anything
                handleCreateShippingInformation(true, subscribeCheckOut)
            }
        }
    }

    override fun addProduct2hToCart(branchResponse: BranchResponse) {
        //TODO: Add product with store to cart
        product2h?.let {
            CartUtils(this).addProduct2hToCart(it, branchResponse, object : AddProductToCartCallback {
                override fun onSuccessfully() {
                    ShoppingCartActivity.startActivity(this@PaymentActivity, database.cacheCartItems[0].cartId)
                }

                override fun forceClearCart() {
                    //TODO: show dialog for clear cart
                }

                override fun onFailure(messageError: String) {
                    showCommonDialog(messageError)
                }

                override fun onFailure(dialog: Dialog) {
                    dialog.show()
                }
            })
        }
    }
    // endregion

    private fun createOrderWithIspu() {
        val cacheCartItems = database.cacheCartItems
        val item = cacheCartItems.find { it.branch != null }
        if (item != null) {
            this.branch = item.branch
            this.deliveryOption = DeliveryOption.getStorePickupIspu() // create DeliveryOption Store Pickup ISPU

            val storePickup = StorePickup(this.branch!!.storeId.toInt())
            val subscribeCheckOut = SubscribeCheckOut(shippingAddress!!.email, null,
                    null, null, storePickup)
            createShippingInforWithClickAndCollect(STORE_PICK_UP_ISPU, subscribeCheckOut)
        }
    }

    private fun getSpecialSKUList(): List<Long>? {
        return this.specialSKUList
                ?: ReadFileHelper<List<Long>>().parseRawJson(this@PaymentActivity, R.raw.special_sku,
                        object : TypeToken<List<Long>>() {}.type, null)
    }

    private fun backPressed() {
        if (currentFragment is DeliveryStorePickUpFragment) {
            // is state of checkout with product 2h?
            if (product2h != null) {
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

enum class CheckoutType {
    NORMAL, ISPU
}