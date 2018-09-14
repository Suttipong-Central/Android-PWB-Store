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
import cenergy.central.com.pwb_store.fragment.interfaces.StorePickUpListener
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.listeners.CheckoutListener
import cenergy.central.com.pwb_store.manager.listeners.DeliveryOptionsListener
import cenergy.central.com.pwb_store.manager.listeners.MemberClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentBillingListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils

class PaymentActivity : AppCompatActivity(), CheckoutListener,
        MemberClickListener, PaymentBillingListener,
        DeliveryOptionsListener, PaymentProtocol, StorePickUpListener {

    var mToolbar: Toolbar? = null

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var shippingAddress: AddressInformation
    private lateinit var deliveryOption: DeliveryOption

    // data
    private val database = RealmController.with(this)
    private var cartId: String? = null
    private var cartItemList: List<CartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private var deliveryOptionsList: List<DeliveryOption> = listOf()
    private var mProgressDialog: ProgressDialog? = null
    private var currentFragment: Fragment? = null
    private var memberContact: String? = null
    private var stores: ArrayList<String> = arrayListOf()
    private var userInformation: UserInformation? = null

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, PaymentActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
//        showProgressDialog()
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
    override fun onClickedMember(customerId: String) {
        getT1CMember(customerId)
    }
    // endregion

    // region {@link PaymentBillingListener}
    override fun setShippingAddressInfo(shippingAddress: AddressInformation) {
        showProgressDialog()
        this.shippingAddress = shippingAddress
        cartId?.let { getDeliveryOptions(it) } // request delivery options
    }
    // endregion

    // region {@link DeliveryOptionsListener}
    override fun onSelectedOptionListener(deliveryOption: DeliveryOption) {
        this.deliveryOption = deliveryOption
        when (deliveryOption.methodCode) {
            "express", "standard" -> {
                showProgressDialog()
                showAlertCheckPayment("", resources.getString(R.string.confrim_oder), null)
            }
            "storepickup" -> {
                startStorePickupFragment()
            }
            "homedelivery" -> {
                startDeliveryHomeFragment()
            }
        }
    }
    // endregion

    override fun onBackPressed() {
        backPressed()
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
            HttpManagerMagento.getInstance().viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
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

    private fun showAlertCheckPayment(title: String, message: String, storeAddress: AddressInformation?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which -> createShippingInformation(storeAddress) }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, which ->
                    dialog.dismiss()
                    mProgressDialog?.dismiss()
                }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun createShippingInformation(storeAddress: AddressInformation?) {
        val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress.email, "", "")
        cartId?.let {
            HttpManagerMagento.getInstance().createShippingInformation(it, storeAddress?: shippingAddress, shippingAddress,
                    deliveryOption, subscribeCheckOut, object : ApiResponseCallback<ShippingInformationResponse> {
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
        HttpManagerMagento.getInstance().getOrderDeliveryOptions(cartId, shippingAddress,
                object : ApiResponseCallback<List<DeliveryOption>> {
                    override fun success(response: List<DeliveryOption>?) {
                        if (response != null) {
                            deliveryOptionsList = response
                            mProgressDialog?.dismiss()
                            startDeliveryOptions()
                        } else {
                            mProgressDialog?.dismiss()
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

        val email = userInformation?.user?.email ?: ""
        val staffId = userInformation?.user?.staffId ?: ""
        val storeId = userInformation?.user?.storeId?.toString() ?: ""

        HttpManagerMagento.getInstance().updateOder(cartId!!, email, staffId, storeId, object : ApiResponseCallback<String> {
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

        val cacheCartItems = arrayListOf<CacheCartItem>()
        cacheCartItems.addAll(RealmController.getInstance().cacheCartItems ?: arrayListOf())

        clearCachedCart()
        mProgressDialog?.dismiss()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentSuccessFragment.newInstance(orderId, cacheCartItems))
                .commit()
    }

    // region {@link PaymentProtocol}
    override fun getItems(): List<CartItem> {
        return this.cartItemList
    }

    override fun getMembers(): List<MemberResponse> {
        return this.membersList
    }

    override fun getDeliveryOptions(): List<DeliveryOption> {
        return this.deliveryOptionsList
    }

    override fun retrieveStores() {
        //TODO: get stores
        if (currentFragment is DeliveryStorePickUpFragment) {
            stores.add("store 1")
            stores.add("store 2")
            stores.add("store 3")
            stores.add("store 4")
            stores.add("store 5")

            (currentFragment as DeliveryStorePickUpFragment).updateStores(stores)
        }
    }
    // endregion

    // region {@link StorePickUpListener}
    override fun onUpdateStoreDetail(store: String) {
        //TODO: update storeDetail
        if (currentFragment is DeliveryStorePickUpFragment) {
            (currentFragment as DeliveryStorePickUpFragment).updateStoreDetail(store)
        }

    }

    override fun onSelectedStore(store: String) {
        val userInformation = database.userInformation
        userInformation?.let {
            if (userInformation.user != null && userInformation.stores != null && userInformation.stores!!.size > 0) {
                showProgressDialog()
                val staff = userInformation.user
                val storeStaff = userInformation.stores!![0]
                //Check this if change to use PWB login
                val province = database.getProvinceByNameEn(storeStaff?.province ?: "")
                val district = database.getDistrictByNameEn(storeStaff?.district ?: "")
                val subDistrict = database.getSubDistrictByNameEn(storeStaff?.subDistrict ?: "")
                val postCode = database.getPostcodeByCode(storeStaff?.postalCode)
                val storeAddress = AddressInformation.createAddress(
                        firstName = staff?.name ?: "", lastName = staff?.name ?: "", email = staff?.email ?: "",
                        contactNo = storeStaff?.number ?: "0000000000", homeNo = "",
                        homeBuilding = storeStaff?.building ?: "", homeSoi = storeStaff?.soi ?: "",
                        homeRoad = storeStaff?.road ?: "test", homePostalCode = storeStaff?.postalCode ?: "",
                        homePhone = storeStaff?.number ?: "", provinceId = province?.provinceId?.toString() ?: "",
                        provinceCode = province?.code ?: "", countryId = province?.countryId ?: "",
                        districtId = district?.districtId?.toString() ?: "",
                        subDistrictId = subDistrict?.subDistrictId?.toString() ?: "157",
                        postcodeId =  postCode?.id?.toString()?: "159", homeCity = province?.nameTh ?: "",
                        homeDistrict = district?.nameTh ?: "", homeSubDistrict = subDistrict?.nameTh ?: "บางรัก")
                showAlertCheckPayment("", resources.getString(R.string.confrim_oder), storeAddress)
            }
        }

    }
    // endregion

    private fun backPressed() {
        if (currentFragment is DeliveryStorePickUpFragment) {
            stores.clear() // clear stores
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