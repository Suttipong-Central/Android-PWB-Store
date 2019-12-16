package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.dialogs.ShareBottomSheetDialogFragment
import cenergy.central.com.pwb_store.fragment.DetailFragment
import cenergy.central.com.pwb_store.fragment.ProductExtensionFragment
import cenergy.central.com.pwb_store.fragment.ProductOverviewFragment
import cenergy.central.com.pwb_store.fragment.WebViewFragment
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.*
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyCompareView
import cenergy.central.com.pwb_store.view.PowerBuyShoppingCartView

class ProductDetailActivity : BaseActivity(), ProductDetailListener, PowerBuyCompareView.OnClickListener,
        PowerBuyShoppingCartView.OnClickListener {

    private val analytics: Analytics? by lazy { Analytics(this) }
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
    private var productJdaSku: String? = null
    private var product: Product? = null
    private var childProductList: ArrayList<Product> = arrayListOf()

    companion object {
        private val TAG = ProductDetailActivity::class.java.simpleName

        const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID" // barcode
        const val ARG_PRODUCT_SKU = "ARG_PRODUCT_SKU"
        const val ARG_PRODUCT_JDA_SKU = "ARG_PRODUCT_JDA_SKU"

        private const val TAG_DETAIL_FRAGMENT = "fragment_detail"
        private const val TAG_OVERVIEW_FRAGMENT = "fragment_overview"
        private const val TAG_EXTENSION_FRAGMENT = "fragment_extension"

        fun startActivityBySku(context: Context, sku: String) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(ARG_PRODUCT_SKU, sku)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }

        fun startActivityByJDA(context: Context, jdaSku: String) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(ARG_PRODUCT_JDA_SKU, jdaSku)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        handleChangeLanguage()

        // get intent extra
        intent.extras?.let {
            productSku = it.getString(ARG_PRODUCT_SKU, null)
            productId = it.getString(ARG_PRODUCT_ID, null)
            productJdaSku = it.getString(ARG_PRODUCT_JDA_SKU, null)
        }

        bindView()
        retrieveProductDetail()
    }

    override fun onResume() {
        super.onResume()
        // analytics
        analytics?.trackScreen(Screen.PRODUCT_DETAIL)

        updateCompareBadge()
        updateShoppingCartBadge()
    }

    private fun retrieveProductDetail() {
        if (productSku != null) {
            retrieveProduct(productSku!!)
            return
        }

        if (productId != null) { // is barcode?
            retrieveProductByBarcode(productId!!)
            return
        }

        if (productJdaSku != null) { // is from offline store cds/rbs?
            retrieveProductByProductJda(productJdaSku!!)
            return
        }
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

    override fun getChildProduct(): ArrayList<Product> = childProductList

    override fun addProductToCompare(product: Product?) {
//        product?.let { addToCompare(it) }
        showAlertDialog(getString(R.string.developing_system_compare))
    }

    override fun addProductToCart(product: Product?) {
        product?.let { startAddToCart(it) }
    }

    override fun addProduct1HrsToCart(product: Product?) {
        product?.let { startPaymentBy2Hr(it) }
    }

    private fun startPaymentBy2Hr(product: Product) {
        PaymentActivity.startSelectStorePickup(this, product)
    }

    override fun onDisplayAvailableStore(product: Product?) {
        product?.let {
            startAvailableStore(it)
        }
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
            ShoppingCartActivity.startActivity(this, view)
        } else {
            showAlertDialog("", resources.getString(R.string.not_have_products_in_cart))
        }
    }
    // endregion

    // region {@link ShareButtonClickListener}
    override fun onShareButtonClickListener() {
        if (product != null && product!!.urlKey.isNotBlank()) {
            val shareText = "${product!!.name} ${Constants.WEB_HOST_NAME}/" +
                    "${preferenceManager.getDefaultLanguage()}/${product!!.urlKey}"
            val shareBottomSheetFragment = ShareBottomSheetDialogFragment.newInstance(shareText)
            shareBottomSheetFragment.show(supportFragmentManager, ShareBottomSheetDialogFragment.TAG)
        } else {
            showAlertDialog(getString(R.string.some_thing_wrong))
        }
    }
    // endregion

    // region retrieve product
    private fun retrieveProductByBarcode(barcode: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductByBarcode(barcode,
                object : ApiResponseCallback<Product?> {
                    override fun success(response: Product?) {
                        handleGetProductSuccess(response)
                    }

                    override fun failure(error: APIError) {
                        dismissProgressDialog()
                        DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                    }
                })
    }

    private fun retrieveProductByProductJda(jdaSku: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductByProductJda(jdaSku,
                object : ApiResponseCallback<Product?> {
                    override fun success(response: Product?) {
                        handleGetProductSuccess(response)
                    }

                    override fun failure(error: APIError) {
                        dismissProgressDialog()
                        DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                    }
                })
    }

    private fun retrieveProduct(sku: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductDetail(sku, object : ApiResponseCallback<Product?> {
            override fun success(response: Product?) {
                runOnUiThread {
                    handleGetProductSuccess(response)
                }
            }

            override fun failure(error: APIError) {
                runOnUiThread {
                    dismissProgressDialog()
                    DialogHelper(this@ProductDetailActivity).showErrorDialog(error)
                }
            }
        })
    }

    private fun handleGetProductSuccess(response: Product?) {
        if (response != null) {
            if (response.typeId == "configurable") {
                checkProductConfig(response)
            } else {
                checkHDLOption(response)
            }
        } else {
            dismissProgressDialog()
            tvNotFound.visibility = View.VISIBLE
            containerGroupView.visibility = View.INVISIBLE
        }
    }

    private fun checkProductConfig(product: Product) {
        if (product.typeId == "configurable") {
            val productLinks = product.extension?.productConfigLinks ?: listOf()
            val result = TextUtils.join(",", productLinks)
            val filterGroupsList = java.util.ArrayList<FilterGroups>()
            filterGroupsList.add(FilterGroups.createFilterGroups("entity_id", result, "in"))
            val sortOrders = java.util.ArrayList<SortOrder>()

            ProductListAPI.retrieveProducts(this, productLinks.size, 1,
                    filterGroupsList, sortOrders, object : ApiResponseCallback<ProductResponse> {
                override fun success(response: ProductResponse?) {
                    runOnUiThread {
                        if (response != null) {
                            childProductList = response.products
                            checkHDLOption(product)
                        }
                    }
                }

                override fun failure(error: APIError) {
                    runOnUiThread {
                        Log.d("ProductConfigChild", "${error.errorCode} ${error.errorMessage}")
                        checkHDLOption(product)
                    }
                }
            })
        }
    }

    private fun checkHDLOption(product: Product) {
        HttpManagerMagento.getInstance(this).getDeliveryInformation(product.sku,
                object : ApiResponseCallback<List<DeliveryInfo>> {
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
        this@ProductDetailActivity.productSku = product.sku
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
    private fun startAddToCart(product: Product) {
        this.product = product

        analytics?.trackAddToCart(product.sku, "normal") // tracking event

        showProgressDialog()
        CartUtils(this).addProductToCart(product, object : AddProductToCartCallback {
            override fun onSuccessfully() {
                updateShoppingCartBadge()
                dismissProgressDialog()
            }

            override fun forceClearCart() {
                showClearCartDialog()
                dismissProgressDialog()
            }

            override fun onFailure(messageError: String) {
                showCommonDialog(messageError)
                dismissProgressDialog()
            }

            override fun onFailure(dialog: Dialog) {
                if (!isFinishing) {
                    dialog.show()
                }
                dismissProgressDialog()
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

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }

    private fun dismissProgressDialog() {
        if (!isFinishing && progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }
}
