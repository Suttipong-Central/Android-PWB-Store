package cenergy.central.com.pwb_store.activity

import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.CompareProtocol
import cenergy.central.com.pwb_store.adapter.interfaces.CompareItemListener
import cenergy.central.com.pwb_store.extensions.isProductInStock
import cenergy.central.com.pwb_store.fragment.CompareFragment
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.api.CompareAPI
import cenergy.central.com.pwb_store.manager.bus.event.CompareDeleteBus
import cenergy.central.com.pwb_store.manager.bus.event.CompareDetailBus
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.CompareProduct
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView
import kotlinx.android.synthetic.main.activity_compare.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CompareActivity : BaseActivity(), CompareItemListener, PowerBuyShoppingCartView.OnClickListener, CompareProtocol {

    private lateinit var mToolbar: Toolbar
    private lateinit var mBuyShoppingCartView: PowerBuyShoppingCartView
    private val database = RealmController.getInstance()
    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView
    private var compareProducts: List<CompareProduct> = arrayListOf()
    private var compareProductDetailList: List<CompareProductResponse> = arrayListOf()

    @Subscribe
    fun onEvent(compareDetailBus: CompareDetailBus) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra(ProductDetailActivity.ARG_PRODUCT_SKU, compareDetailBus.compareProduct.sku)
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(compareDetailBus.view, 0, 0, compareDetailBus.view.width, compareDetailBus.view.height)
                        .toBundle())
    }

    @Subscribe
    fun onEvent(compareDeleteBus: CompareDeleteBus) {
        database.deleteCompareProduct(compareDeleteBus.compareProduct.sku)
        retrieveCompareProduct()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        handleChangeLanguage()
        initView()
        retrieveCompareProduct()
    }

    override fun getCompateProducts(): List<CompareProduct> = compareProducts
    override fun getCompareProductDetailList() = compareProductDetailList

    private fun retrieveCompareProduct() {
        showProgressDialog()
        CompareAPI().retrieveCompareProduct(this, getSKUs(), object : ApiResponseCallback<List<CompareProductResponse>> {
            override fun success(response: List<CompareProductResponse>?) {
                compareProductDetailList = response ?: arrayListOf()
                startCompareFragment()
                dismissProgressDialog()
            }

            override fun failure(error: APIError) {
                startCompareFragment()
                dismissProgressDialog()
            }
        })
    }

    private fun getSKUs(): String {
        // update compare product list
        compareProducts = database.compareProducts

        val productSKUs = arrayListOf<String>()
        compareProducts.forEach {
            productSKUs.add(it.sku)
        }
        return productSKUs.joinToString(",")
    }

    private fun startCompareFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, CompareFragment.newInstance(), CompareFragment.tag)
                .commit()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        mBuyShoppingCartView = findViewById(R.id.shopping_cart_compare)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
        mBuyShoppingCartView.setListener(this)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        updateShoppingCartBadge()
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    // region language
    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        retrieveCompareProduct()
    }

    override fun getSwitchButton(): LanguageButton? = languageButton
    // endregion

    // region network
    override fun onNetworkStateChange(state: NetworkInfo.State) {
        super.onNetworkStateChange(state)
        // isConnected?
        if (currentState == NetworkInfo.State.CONNECTED && forceRefresh) {
            retrieveCompareProduct()
        }
    }

    override fun getStateView(): NetworkStateView? = networkStateView
    // endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_UPDATE_LANGUAGE) {
            // check language
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }

            if (resultCode == ShoppingCartActivity.RESULT_UPDATE_PRODUCT) {
                retrieveCompareProduct()
            }
        }
    }

    override fun onShoppingCartClick(view: View) {
        if (database.cacheCartItems!!.size > 0) {
            ShoppingCartActivity.startActivity(this, preferenceManager.cartId)
        } else {
            showAlertDialog("", resources.getString(R.string.not_have_products_in_cart))
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) { _, _ ->
                    dismissProgressDialog()
                }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showOKAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) { _, _ ->
                    clearAllCompareProducts()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
        builder.show()
    }

    private fun showProgressDialog() {
        loadingProgressBar?.visibility = View.VISIBLE
    }

    // region {@link {Implement CompareItemListener}
    override fun onClickAddToCart(compareProduct: CompareProduct) {
        showProgressDialog()
        val cartId = preferenceManager.cartId
        if (cartId != null) {
            addProductToCart(cartId, compareProduct)
        } else {
            retrieveCart(compareProduct)
        }
    }

    override fun onClearAllProductCompare() {
        if (getSKUs().isNotEmpty()){
            showOKAlertDialog(getString(R.string.text_comfirm_clear_all))
        }
    }
    // endregion

    private fun clearAllCompareProducts(){
        database.deleteAllCompareProduct()
        val fragment = supportFragmentManager.findFragmentByTag(CompareFragment.tag)
        if (fragment != null) {
            fragment as CompareFragment
            fragment.compareProducts = arrayListOf()
            fragment.compareProductDetailList = arrayListOf()
            fragment.updateCompareList()
        }
    }

    private fun retrieveCart(compareProduct: CompareProduct) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getCart(object : ApiResponseCallback<String?> {
            override fun success(response: String?) {
                if (response != null) {
                    preferenceManager.setCartId(response)
                    addProductToCart(response, compareProduct)
                }
            }

            override fun failure(error: APIError) {
                showAlertDialog("", error.errorUserMessage)
            }
        })
    }

    private fun addProductToCart(cartId: String, compareProduct: CompareProduct) {
        val cartItemBody = CartItemBody.create(cartId, compareProduct.sku, arrayListOf())
        HttpManagerMagento.getInstance(this).addProductToCart(cartId, cartItemBody, object : ApiResponseCallback<CartItem> {
            override fun success(response: CartItem?) {
                runOnUiThread {
                    saveCartItem(response, compareProduct)
                }
            }

            override fun failure(error: APIError) {
                dismissProgressDialog()
                if (error.errorCode == APIError.INTERNAL_SERVER_ERROR.toString()) {
                    showClearCartDialog()
                } else {
                    DialogHelper(this@CompareActivity).showErrorDialog(error)
                }
            }
        })
    }

    private fun saveCartItem(cartItem: CartItem?, compareProduct: CompareProduct) {
        cartItem ?: return
        database.saveCartItem(CacheCartItem.asCartItem(cartItem, compareProduct), object : DatabaseListener {
            override fun onSuccessfully() {
                dismissProgressDialog()
                updateShoppingCartBadge()
                Toast.makeText(this@CompareActivity, getString(R.string.added_to_cart),
                        Toast.LENGTH_SHORT).show()

                updateCompareList(compareProduct)
            }

            override fun onFailure(error: Throwable) {
                showAlertDialog("", error.message ?: getString(R.string.some_thing_wrong))
            }
        })
    }

    private fun updateCompareList(compareProduct: CompareProduct) {
        // is no have in stock? force update
        if (!this@CompareActivity.isProductInStock(compareProduct)) {
            val fragment = supportFragmentManager.findFragmentByTag(CompareFragment.tag)
            if (fragment != null) {
                (fragment as CompareFragment).updateCompareList()
            }
        }
    }

    private fun updateShoppingCartBadge() {
        var count = 0
        val items = database.cacheCartItems
        for (item in items!!) {
            if (item.qty != null) {
                count += item.qty!!
            }
        }
        mBuyShoppingCartView.setBadgeCart(count)
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }

    private fun showClearCartDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setCancelable(false)
                .setMessage(getString(R.string.title_clear_cart))
                .setPositiveButton(getString(R.string.ok_alert)) { dialog, _ ->
                    clearCart() // clear item cart
                    updateShoppingCartBadge() // update ui
                    dialog.dismiss()
                }
        builder.show()
    }

    private fun dismissProgressDialog() {
        loadingProgressBar?.visibility = View.INVISIBLE
    }
}
