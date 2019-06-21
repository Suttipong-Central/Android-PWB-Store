package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.fragment.DetailFragment
import cenergy.central.com.pwb_store.fragment.ProductExtensionFragment
import cenergy.central.com.pwb_store.fragment.ProductOverviewFragment
import cenergy.central.com.pwb_store.fragment.WebViewFragment
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyCompareView
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView

class ProductDetailActivity : BaseActivity(), ProductDetailListener, PowerBuyCompareView.OnClickListener,
        PowerBuyShoppingCartView.OnClickListener {

    // widget view
    private var progressDialog: ProgressDialog? = null
    lateinit var mToolbar: Toolbar
    lateinit var mBuyCompareView: PowerBuyCompareView
    lateinit var mBuyShoppingCartView: PowerBuyShoppingCartView
    lateinit var tvNotFound: TextView
    lateinit var containerGroupView: LinearLayout
    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView

    // data
    private val database = RealmController.getInstance()
    private var productSku: String? = null
    private var productId: String? = null
    private var isBarcode: Boolean = false
    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        handleChangeLanguage()

        // get intent
        val mIntent = intent
        val extras = mIntent.extras
        if (extras != null) {
            productSku = extras.getString(ARG_PRODUCT_SKU)
            productId = extras.getString(ARG_PRODUCT_ID)
            isBarcode = extras.getBoolean(ARG_IS_BARCODE)
        }

        bindView()

        retrieveProductDetail()
    }

    private fun retrieveProductDetail() {
        if (!isBarcode) {
            productSku?.let { retrieveProduct(it) }
        } else {
            productId?.let { retrieveProductFromBarcode(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCompareBadge()
        updateShoppingCartBadge()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_LANGUAGE) {
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }
        }
    }

    private fun bindView() {
        mToolbar = findViewById(R.id.toolbar)
        mBuyCompareView = mToolbar.findViewById(R.id.button_compare)
        mBuyShoppingCartView = mToolbar.findViewById(R.id.shopping_cart)
        tvNotFound = findViewById(R.id.tvNotFound)
        containerGroupView = findViewById(R.id.containerGroupView)

        val searchImageView = findViewById<ImageView>(R.id.img_search)

        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        searchImageView.setOnClickListener { v ->
            val intent = Intent(this@ProductDetailActivity, SearchActivity::class.java)
            ActivityCompat.startActivityForResult(this@ProductDetailActivity, intent, REQUEST_UPDATE_LANGUAGE,
                    ActivityOptionsCompat
                            .makeScaleUpAnimation(v, 0, 0, v.width, v.height)
                            .toBundle())
        }

        // setup badge
        mBuyCompareView.visibility = View.GONE
        mBuyShoppingCartView.setListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null && item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    // region language
    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        retrieveProductDetail()
    }

    override fun getSwitchButton(): LanguageButton? = languageButton
    // endregion

    override fun onNetworkStateChange(state: NetworkInfo.State) {
        super.onNetworkStateChange(state)
        // isConnected?
        if (currentState == NetworkInfo.State.CONNECTED && forceRefresh) {
            retrieveProductDetail()
        }
    }

    override fun getStateView(): NetworkStateView? = networkStateView
    // endregion

    // region product ProductDetailProtocol
    override fun getProduct(): Product? = product

    override fun addProductToCompare(product: Product?) {
//        product?.let { addToCompare(it) }
        showAlertDialog(getString(R.string.developing_system_compare))
    }

    override fun addProductToCart(product: Product?) {
        product?.let { startAddToCart(it, arrayListOf()) }
    }

    override fun addProductConfigToCart(product: Product?, listOptionsBody: ArrayList<OptionBody>) {
        product?.let { startAddToCart(it, listOptionsBody) }

    }

    override fun onDisplayAvailableStore(product: Product?) {
        product?.let { startAvailableStore(it) }
    }

    override fun onDisplayOverview(overview: String) {
        startWebView(overview)
    }

    override fun onDisplaySpecification(spec: String) {
        startWebView(spec)
    }
    // endregion

    // region {@link PowerBuyCompareView.OnClickListener}
    override fun onShoppingBagClick(view: View) {
        val intent = Intent(this, CompareActivity::class.java)
        ActivityCompat.startActivity(this, intent,
                ActivityOptionsCompat
                        .makeScaleUpAnimation(view, 0, 0, view.width, view.height)
                        .toBundle())
    }
    // endregion

    // region {@link PowerBuyShoppingCartView.OnClickListener}
    override fun onShoppingCartClick(view: View) {
        if (database.cacheCartItems.size > 0) {
            ShoppingCartActivity.startActivity(this, view, preferenceManager.cartId)
        } else {
            showAlertDialog("", resources.getString(R.string.not_have_products_in_cart))
        }
    }
    // endregion

    // region retrieve product
    private fun retrieveProductFromBarcode(barcode: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductFromBarcode(barcode, object : ApiResponseCallback<Product?> {
            override fun success(response: Product?) {
                handleGetProductSuccess(response)
            }

            override fun failure(error: APIError) {
                Log.e(TAG, "onResponse: " + error.errorMessage)
                runOnUiThread {
                    // dismiss loading dialog
                    dismissProgressDialog()
                    // show error message
                    DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                }
            }
        })
    }

    private fun retrieveProduct(sku: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductDetail(sku, object : ApiResponseCallback<Product?> {
            override fun success(response: Product?) {
                handleGetProductSuccess(response)
            }

            override fun failure(error: APIError) {
                Log.e(TAG, "onResponse: " + error.errorMessage)
                runOnUiThread {
                    // dismiss loading dialog
                    dismissProgressDialog()
                    // show error message
                    DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                }
            }
        })
    }

    private fun handleGetProductSuccess(response: Product?) {
        dismissProgressDialog()
        if (response != null) {
            tvNotFound.visibility = View.INVISIBLE
            containerGroupView.visibility = View.VISIBLE

            // setup product
            product = response
            startProductDetailFragment()
            checkDisableAddProductButton(product!!)
        } else {
            tvNotFound.visibility = View.VISIBLE
            containerGroupView.visibility = View.INVISIBLE
        }
    }
    // end region

    private fun startProductDetailFragment() {
        // setup
        supportFragmentManager.beginTransaction().replace(R.id.containerDetail,
                DetailFragment(),
                TAG_DETAIL_FRAGMENT).commit()
        supportFragmentManager.beginTransaction().replace(R.id.containerOverview,
                ProductOverviewFragment(),
                TAG_EXTENSION_FRAGMENT).commit()
        supportFragmentManager.beginTransaction().replace(R.id.containerExtension,
                ProductExtensionFragment(),
                TAG_OVERVIEW_FRAGMENT).commit()
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

    private fun updateCompareBadge() {
        val count = database.compareProducts.size
        mBuyCompareView.updateCartCount(count)
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok_alert)) { _, _ -> dismissProgressDialog() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = DialogUtils.createProgressDialog(this)
            progressDialog?.show()
        } else {
            progressDialog?.show()
        }
    }

    // region action compare product
//    private fun addToCompare(product: Product) {
//        showProgressDialog()
//        val count = database.compareProducts.size
//        Log.d(TAG, "" + count)
//        if (count >= 4) {
//            dismissProgressDialog()
//            showAlertDialog(getString(R.string.alert_count))
//        } else {
//            val compareProduct = database.getCompareProduct(product.sku)
//            if (compareProduct != null) {
//                dismissProgressDialog()
//                showAlertDialog(getString(R.string.alert_compare)
//                        + "" + compareProduct.name + "" + getString(R.string.alert_compare_yes))
//            } else {
//                // store compare product to database
//                saveCompareProduct(product)
//            }
//        }
//    }

//    private fun saveCompareProduct(product: Product) {
//        database.saveCompareProduct(product, object : DatabaseListener {
//            override fun onSuccessfully() {
//                dismissProgressDialog()
//                updateCompareBadge()
//                Toast.makeText(this@ProductDetailActivity, "Generate compare complete.", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onFailure(error: Throwable) {
//                dismissProgressDialog()
//                Log.d(TAG, "" + error.message)
//            }
//        })
//    }
    // end region

    // region action add product to cart
    private fun startAddToCart(product: Product, listOptionsBody: ArrayList<OptionBody>) {
        this.product = product
        showProgressDialog()
        val cartId = preferenceManager.cartId
        if (cartId != null) {
            Log.d("ProductDetail", "has cart id")
            addProductToCart(cartId, product, listOptionsBody)
        } else {
            Log.d("ProductDetail", "new cart id")
            retrieveCart(product, listOptionsBody)
        }
    }

    private fun retrieveCart(product: Product, listOptionsBody: ArrayList<OptionBody>) {
        HttpManagerMagento.getInstance(this).getCart(object : ApiResponseCallback<String?> {
            override fun success(response: String?) {
                if (response != null) {
                    preferenceManager.setCartId(response)
                    addProductToCart(response, product, listOptionsBody)
                }
            }

            override fun failure(error: APIError) {
                dismissProgressDialog()
                DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
            }
        })
    }

    private fun addProductToCart(cartId: String, product: Product, listOptionsBody: ArrayList<OptionBody>) {
        val cartItemBody = CartItemBody.create(cartId, product.sku, listOptionsBody)
        HttpManagerMagento.getInstance(this).addProductToCart(cartId, cartItemBody, object : ApiResponseCallback<CartItem> {
            override fun success(response: CartItem?) {
                runOnUiThread {
                    saveCartItem(response, product)
                    checkDisableAddProductButton(product)
                    dismissProgressDialog()
                }
            }

            override fun failure(error: APIError) {
                dismissProgressDialog()
                if (error.errorCode == APIError.INTERNAL_SERVER_ERROR.toString()) {
                    showClearCartDialog()
                } else {
                    DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                }
            }
        })
    }

    private fun saveCartItem(cartItem: CartItem?, product: Product) {
        database.saveCartItem(CacheCartItem.asCartItem(cartItem!!, product), object : DatabaseListener {
            override fun onSuccessfully() {
                updateShoppingCartBadge()
                Toast.makeText(this@ProductDetailActivity, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(error: Throwable) {
                dismissProgressDialog()
                showAlertDialog("", "" + error.message)
            }
        })
    }
    // endregion

    private fun startAvailableStore(product: Product) {
        Log.d(TAG, "sku" + product.id)
        val intent = Intent(this, AvailableStoreActivity::class.java)
        intent.putExtra(AvailableStoreActivity.ARG_SKU, product.sku)
        startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
    }

    private fun startWebView(content: String) {
        val intent = Intent(this@ProductDetailActivity, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.ARG_WEB_URL, content)
        intent.putExtra(WebViewActivity.ARG_MODE, WebViewFragment.MODE_HTML)
        intent.putExtra(WebViewActivity.ARG_TITLE, "Web")
        startActivity(intent)
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

    fun checkDisableAddProductButton(product: Product) {
        runOnUiThread {
            val productInCart = database.getCacheCartItemBySKU(product.sku)
            if (productInCart != null && productInCart.qty!! >= product.extension?.stokeItem?.qty!!) {
                val fragment = supportFragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT)
                if (fragment != null && fragment is DetailFragment) {
                    fragment.disableAddToCartButton()
                }
            }
        }
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }

    private fun dismissProgressDialog() {
        if (!isFinishing && progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    companion object {
        private val TAG = ProductDetailActivity::class.java.simpleName

        const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"
        const val ARG_PRODUCT_SKU = "ARG_PRODUCT_SKU"
        const val ARG_IS_BARCODE = "ARG_IS_BARCODE"

        private const val TAG_DETAIL_FRAGMENT = "fragment_detail"
        private const val TAG_OVERVIEW_FRAGMENT = "fragment_overview"
        private const val TAG_EXTENSION_FRAGMENT = "fragment_extension"
    }
}
