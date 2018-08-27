package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.PaymentCheckOutFragment
import cenergy.central.com.pwb_store.fragment.PaymentDescriptionFragment
import cenergy.central.com.pwb_store.fragment.PaymentSuccessFragment
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.listeners.CheckOutClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentClickListener
import cenergy.central.com.pwb_store.manager.listeners.PaymentDescriptionListener
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.utils.DialogUtils

class PaymentActivity : AppCompatActivity(), CheckOutClickListener, PaymentClickListener, PaymentDescriptionListener {

    override fun getItemList(): List<CartItem> {
        return cartItemList
    }

    private var cartItemList: List<CartItem> = listOf()
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

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentCheckOutFragment.newInstance())
                .commit()
    }

    override fun onCheckOutListener(contactNo: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentDescriptionFragment.newInstance(contactNo))
                .commit()
    }

    override fun onPaymentClickListener(orderId: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, PaymentSuccessFragment.newInstance(orderId))
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
}