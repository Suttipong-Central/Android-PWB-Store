package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.PaymentCheckOutFragment
import cenergy.central.com.pwb_store.fragment.PaymentDescriptionFragment
import cenergy.central.com.pwb_store.fragment.PaymentMembersFragment
import cenergy.central.com.pwb_store.fragment.PaymentSuccessFragment
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.HttpMangerSiebel
import cenergy.central.com.pwb_store.manager.listeners.*
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.utils.DialogUtils

class PaymentActivity : AppCompatActivity(), CheckOutClickListener, PaymentClickListener,
        PaymentDescriptionListener, PaymentMembersListener, MembersClickListener {
    override fun getItemList(): List<CartItem> {
        return this.cartItemList
    }

    override fun getMembersList(): List<MemberResponse> {
        return this.membersList
    }

    private var cartItemList: List<CartItem> = listOf()
    private var membersList: List<MemberResponse> = listOf()
    private lateinit var preferenceManager: PreferenceManager
    private var mProgressDialog: ProgressDialog? = null

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, PaymentActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        showProgressDialog()
        initView()
        getItems()
        goToCheckOut()
    }

    override fun onCheckOutListener(contactNo: String?) {
        // skip?
        if (contactNo == null) {
            skipToDescription()
        } else {
            getCustomerT1C(contactNo)
        }
    }

    override fun onMembersClickList(customerId: String) {
        HttpMangerSiebel.getInstance().getT1CMember(customerId, object : ApiResponseCallback<Member>{
            override fun success(response: Member?) {
                if(response != null){
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction
                            .replace(R.id.container, PaymentDescriptionFragment.newInstance(response))
                            .commit()
                } else {
                    showAlertDialog("", resources.getString(R.string.some_thing_wrong), false)
                }
            }

            override fun failure(error: APIError) {
                showAlertDialog("", resources.getString(R.string.some_thing_wrong), false)
            }
        })
    }

    override fun onPaymentClickListener(orderId: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentSuccessFragment.newInstance(orderId))
                .commit()
    }

    private fun goToCheckOut() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentCheckOutFragment.newInstance())
                .commit()
    }

    private fun skipToDescription() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentDescriptionFragment.newInstance())
                .commit()
    }

    private fun initView() {
        val mToolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        preferenceManager = PreferenceManager(this)
        mToolbar.setNavigationOnClickListener { finish() }
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    private fun getItems() {
        preferenceManager.cartId?.let { cartId->
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

    private fun getCustomerT1C(mobile: String) {
        HttpMangerSiebel.getInstance().verifyMemberFromT1C(mobile, " ",object : ApiResponseCallback<List<MemberResponse>>{
            override fun success(response: List<MemberResponse>?) {
                if (response != null && response.isNotEmpty()){
                    membersList = response
                    startMembersFragment()
                } else {
                    showAlertDialog("", resources.getString(R.string.not_have_user), true)
                }
            }

            override fun failure(error: APIError) {
                showAlertDialog("", resources.getString(R.string.not_have_user), true)
            }
        })
    }

    private fun startMembersFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentMembersFragment.newInstance())
                .commit()
    }

    fun showAlertDialog(title: String, message: String, checkSkip: Boolean) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
        if(checkSkip){
            builder.setPositiveButton(android.R.string.ok) { dialog, which -> skipToDescription() }
            builder.setNegativeButton(android.R.string.cancel) { dialog, which -> goToCheckOut() }
        } else {
            builder.setPositiveButton(android.R.string.ok) { dialog, which -> goToCheckOut() }
        }
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }
}