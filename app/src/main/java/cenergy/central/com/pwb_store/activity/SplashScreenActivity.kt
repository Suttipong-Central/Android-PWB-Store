package cenergy.central.com.pwb_store.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.CartListener
import cenergy.central.com.pwb_store.utils.CartUtils
import cenergy.central.com.pwb_store.utils.showCommonDialog

class SplashScreenActivity : AppCompatActivity() {
    val preferenceManager by lazy { PreferenceManager(this) }
    private lateinit var database: RealmController

    companion object {
        private const val PATH_SHOPPING_CART = "/shopping-cart"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = RealmController.getInstance()

        if (database.userToken != null) {
            checkIntent(intent)
        } else {
            forceLogin()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { checkIntent(it) }
    }

    private fun start() {
        // start main page
        val intent = Intent(this, MainActivity::class.java)
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat.makeBasic().toBundle())
        finish()
    }

    private fun forceLogin() {
        // start login page
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun startProductDetailBySku(sku: String) {
        ProductDetailActivity.startActivityBySku(this, sku)
        finish()
    }

    private fun startProductDetailByJda(jdaSku: String) {
        ProductDetailActivity.startActivityByJDA(this, jdaSku)
        finish()
    }

    private fun startShoppingCart(cartId: String) {
        ShoppingCartActivity.startActivity(this, cartId)
        finish()
    }

    private fun createCart() {
        CartUtils(this).createCart(object : CartListener {
            override fun onCartCreated(cartId: String) {
                startShoppingCart(cartId)
            }

            override fun onFailure(messageError: String) {
                showCommonDialog(messageError)
            }
        })
    }

    //TODO: Improve this function
    private fun checkIntent(intent: Intent) {
        val action = intent.action
        val data = intent.dataString

        if (Intent.ACTION_VIEW != action || data == null) {
            start()
            return
        }

        // set default AppLanguage
        if (intent.data != null && intent.data!!.pathSegments.isNotEmpty()) {
            intent.data!!.pathSegments.forEach {
                if (it == "th" || it == "en") {
                    preferenceManager.setDefaultLanguage(AppLanguage.fromString(it))
                }
            }
        }

        if (data.contains(PATH_SHOPPING_CART)) {
            // open shopping cart
            preferenceManager.cartId?.let {
                startShoppingCart(it)
            } ?: run {
                createCart()
            }
        } else {
            // open pdp
            if (BuildConfig.FLAVOR.contentEquals("pwb")) { // is flavor PWB
                val sku = intent.data?.getQueryParameter("sku")
                if (!sku.isNullOrEmpty()) {
                    startProductDetailBySku(sku)
                }
            } else {
                // cds, rbs
                val jda = intent.data?.getQueryParameter("jda_sku")
                if (!jda.isNullOrEmpty()) {
                    startProductDetailByJda(jda)
                }
            }
        }
    }
}
