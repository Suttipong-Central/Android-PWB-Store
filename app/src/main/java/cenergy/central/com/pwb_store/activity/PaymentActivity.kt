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
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.listeners.*
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils

class PaymentActivity : AppCompatActivity(), CheckOutClickListener,
        MemberClickListener, PaymentBillingListener,
        DeliveryOptionsListener, PaymentProtocol {

    var mToolbar: Toolbar? = null

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var shippingAddress: AddressInformation
    private lateinit var deliveryOption: DeliveryOption
    private var cartId: String? = null
    private var cartItemList: List<CartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private var deliveryOptionsList: List<DeliveryOption> = listOf()
    private var mProgressDialog: ProgressDialog? = null
    private var currentFragment: Fragment? = null
    private var memberContact: String? = null


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
        initView()
        getCartItems()
        startCheckOut()
    }

    // region {@link CheckOutClickListener}
    override fun onCheckOutListener(contactNo: String?) {
        // skip?
        if (contactNo == null) {
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
        when (deliveryOption.methodCode) {
            "express", "standard" -> {
                showProgressDialog()
                this.deliveryOption = deliveryOption
                showAlertCheckPayment("", resources.getString(R.string.confrim_oder))
            }
            "storepickup" -> {
                Toast.makeText(this@PaymentActivity, "Store Pickup", Toast.LENGTH_SHORT).show()
            }
            "homedelivery" -> {
                startHomeDelivery()
            }
        }
    }
    // endregion

    override fun onBackPressed() {
        backPressed()
    }

    private fun startHomeDelivery() {
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

    private fun showAlertCheckPayment(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.ok_alert)) { dialog, which -> createShippingInformation() }
                .setNegativeButton(resources.getString(R.string.cancel_alert)) { dialog, which ->
                    dialog.dismiss()
                    mProgressDialog?.dismiss()
                }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun createShippingInformation() {
        val subscribeCheckOut = SubscribeCheckOut.createSubscribe(shippingAddress.email, "", "")
        if (cartId != null) {
            HttpManagerMagento.getInstance().createShippingInformation(cartId!!, shippingAddress, shippingAddress,
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
        HttpManagerMagento.getInstance().updateOder(cartId!!, object : ApiResponseCallback<String> {
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
    // endregion


    private fun backPressed() {
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
            finish()
            return
        }
    }
}