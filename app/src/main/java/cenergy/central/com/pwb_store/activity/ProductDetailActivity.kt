package cenergy.central.com.pwb_store.activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.AddProductToCartCallback
import cenergy.central.com.pwb_store.utils.CartUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.utils.showCommonDialog
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyCompareView
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView

class ProductDetailActivity : BaseActivity(), ProductDetailListener, PowerBuyCompareView.OnClickListener,
        PowerBuyShoppingCartView.OnClickListener {

    // widget view
    private var progressDialog: ProgressDialog? = null
    private lateinit var mToolbar: Toolbar
    private lateinit var mBuyCompareView: PowerBuyCompareView
    private lateinit var mBuyShoppingCartView: PowerBuyShoppingCartView
    private lateinit var tvNotFound: TextView
    private lateinit var containerGroupView: ConstraintLayout
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

        // get startPayment
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
            // check language
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }
        }
//        if (resultCode == ShoppingCartActivity.RESULT_UPDATE_PRODUCT) {
//            product?.let { checkDisableAddProductButton(it) }
//        }
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

    override fun addProduct1HrsToCart(product: Product?) {
        product?.let { startPaymentBy2Hr(it) }
    }

    private fun startPaymentBy2Hr(product: Product) {
        PaymentActivity.startSelectStorePickup(this, product)
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

    // region {@link ShareButtonClickListener}
    override fun onShareButtonClickListener() {
        if (product != null && product!!.urlKey.isNotBlank()) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "${product!!.name} ${Constants.WEB_HOST_NAME}/${preferenceManager.getDefaultLanguage()}/${product!!.urlKey}")
            intent.type = "text/plain"
            startActivity(intent)
        } else {
            showAlertDialog(getString(R.string.some_thing_wrong))
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
        if (response != null) {
            checkHDLOption(response)
        } else {
            dismissProgressDialog()
            tvNotFound.visibility = View.VISIBLE
            containerGroupView.visibility = View.INVISIBLE
        }
    }

    private fun checkHDLOption(product: Product) {
        HttpManagerMagento.getInstance(this).getDeliveryInformation(product.sku, object : ApiResponseCallback<List<DeliveryInfo>> {
            override fun success(response: List<DeliveryInfo>?) {
                product.isHDL = response?.firstOrNull { it.shippingMethod == "pwb_hdl" } != null
                dismissProgressDialog()
                startProductDetailFragment(product)
            }

            override fun failure(error: APIError) {
                dismissProgressDialog()
                startProductDetailFragment(product)
            }
        })
    }
    // end region

    private fun startProductDetailFragment(product: Product) {
        // set product
        this.product = product

        // setup
        supportFragmentManager.beginTransaction().replace(R.id.containerDetail,
                DetailFragment(), TAG_DETAIL_FRAGMENT).commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.containerOverview,
                ProductOverviewFragment(), TAG_OVERVIEW_FRAGMENT).commitAllowingStateLoss()
        supportFragmentManager.beginTransaction().replace(R.id.containerExtension,
                ProductExtensionFragment(), TAG_EXTENSION_FRAGMENT).commitAllowingStateLoss()

        tvNotFound.visibility = View.INVISIBLE
        containerGroupView.visibility = View.VISIBLE
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
    private fun startAddToCart(product: Product, options: ArrayList<OptionBody>) {
        this.product = product
        showProgressDialog()
        CartUtils(this).addProductToCart(product, options, object : AddProductToCartCallback{
            override fun onSuccessfully() {
                updateShoppingCartBadge()
                dismissProgressDialog()
            }

            override fun forceClearCart() {
                showClearCartDialog()
            }

            override fun onFailure(messageError: String) {
                showCommonDialog(messageError)
            }

            override fun onFailure(dialog: Dialog) {
                if (!isFinishing) { dialog.show() }
            }
        })
    }
    // endregion

    private fun startAvailableStore(product: Product) {
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

//    private fun checkDisableAddProductButton(product: Product) {
//        disableAddToCartButton(!isProductInStock(product))
//    }

//    private fun disableAddToCartButton(disable: Boolean = true) {
//        val fragment = supportFragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT)
//        if (fragment != null && fragment is DetailFragment) {
//            fragment.disableAddToCartButton(disable)
//        }
//    }

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
