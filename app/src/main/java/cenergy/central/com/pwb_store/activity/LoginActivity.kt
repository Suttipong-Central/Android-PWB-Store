package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.util.Log
import android.widget.Toast

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.LoginFragment
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.CartBody
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class LoginActivity : BaseActivity() {

    // widget view
    private lateinit var networkStateView: NetworkStateView
    private lateinit var languageButton: LanguageButton
    private var mProgressDialog: ProgressDialog? = null

    @Subscribe
    fun onEvent(loginSuccessBus: LoginSuccessBus) {
        if (loginSuccessBus.isSuccess) {
//            val intent = Intent(this, MainActivity::class.java)
//            ActivityCompat.startActivity(this, intent,
//                    ActivityOptionsCompat
//                            .makeBasic()
//                            .toBundle())
//            finish()
            startMockup("241983")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        languageButton = findViewById(R.id.switch_language_button)
        handleChangeLanguage()
        initView()
    }

    private fun initView() {
        networkStateView = findViewById(R.id.network_state_View)
        //Load Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commit()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    // region language button
    override fun getSwitchButton(): LanguageButton? {
        return languageButton
    }

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        // recreate view
        initView()
    }
    // endregion

    // region network state
    override fun getStateView(): NetworkStateView? = networkStateView
    // end region

    // TODO: TDB - For instigate "Guest Cart"
    private fun startMockup(sku: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductDetail(sku, object : ApiResponseCallback<Product?> {
            override fun success(response: Product?) {
                if (response != null) {
                    retrieveCart(response)
                } else {
                    Log.e("Mock Product", "error: response null")
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    // dismiss loading dialog
                    mProgressDialog?.dismiss()
                    // show error message
                    DialogHelper(this@LoginActivity).showErrorDialog(error)
                }
            }
        })
    }
    private fun retrieveCart(product: Product) {
        HttpManagerMagento.getInstance(this).getCart(object : ApiResponseCallback<String?> {
            override fun success(response: String?) {
                if (response != null) {
                    preferenceManager.setCartId(response)
                    addProductToCart(response, product)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@LoginActivity).showErrorDialog(error)
            }
        })
    }

    private fun addProductToCart(quoteId: String, product: Product) {
        val cartItemBody = CartItemBody(CartBody(quoteId, product.sku, 1)) // default add qty 1
        HttpManagerMagento.getInstance(this).addProductToCart(quoteId, cartItemBody, object : ApiResponseCallback<CartItem> {
            override fun success(response: CartItem?) {
                if (response != null) {
                    saveCartItem(quoteId, response, product)
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                DialogHelper(this@LoginActivity).showErrorDialog(error)
            }
        })
    }

    private fun saveCartItem(quoteId: String, cartItem: CartItem, product: Product) {
        RealmController.getInstance().saveCartItem(CacheCartItem.asCartItem(cartItem!!, product), object : DatabaseListener {
            override fun onSuccessfully() {
                Toast.makeText(this@LoginActivity, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
                ShoppingCartActivity.startActivity(this@LoginActivity, quoteId)
                mProgressDialog?.dismiss()
            }

            override fun onFailure(error: Throwable) {
                mProgressDialog?.dismiss()
            }
        })
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }
    // end region

}