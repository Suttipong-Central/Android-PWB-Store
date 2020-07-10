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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.activity.GalleryActivity.Companion.RESULT_IMAGE_SELECTED
import cenergy.central.com.pwb_store.activity.interfaces.ProductDetailListener
import cenergy.central.com.pwb_store.adapter.BadgeListener
import cenergy.central.com.pwb_store.dialogs.ShareBottomSheetDialogFragment
import cenergy.central.com.pwb_store.fragment.*
import cenergy.central.com.pwb_store.fragment.ProductExtensionFragment.Companion.TAB_PROMOTION_FREEBIE
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.OfflinePriceItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.*
import cenergy.central.com.pwb_store.view.*
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : BaseActivity(), ProductDetailListener, BadgeListener,
        PowerBuyShoppingCartView.OnClickListener, ProductCompareView.ProductCompareViewListener {

    private val analytics: Analytics? by lazy { Analytics(this) }

    // widget view
    private var progressDialog: ProgressDialog? = null
    private lateinit var mToolbar: Toolbar
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
    private var offlinePriceItem: OfflinePriceItem? = null
    private var deliveryInfoList = arrayListOf<DeliveryInfo>()

    // promotion tab
    private var badgesSelects: ArrayList<String> = arrayListOf()
    private var freebieSKUs: ArrayList<String> = arrayListOf()
    private var freeItems: ArrayList<Product> = arrayListOf()

    companion object {
        const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID" // barcode
        const val ARG_PRODUCT_SKU = "ARG_PRODUCT_SKU"
        const val ARG_PRODUCT_JDA_SKU = "ARG_PRODUCT_JDA_SKU"
        const val ARG_PRICE_PER_STORE = "ARG_PRICE_PER_STORE"
        const val ARG_UPDATE_IMAGE_SELECTED = "ARG_UPDATE_IMAGE_SELECTED"

        private const val TAG = "ProductDetailActivity"
        private const val TAG_DETAIL_FRAGMENT = "fragment_detail"
        private const val TAG_OVERVIEW_FRAGMENT = "fragment_overview"
        private const val TAG_EXTENSION_FRAGMENT = "fragment_extension"

        const val REQUEST_UPDATE_IMAGE_SELECTED = 4020

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

        fun startActivity(context: Context, sku: String, offlinePriceItem: OfflinePriceItem?) {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(ARG_PRODUCT_SKU, sku)
            intent.putExtra(ARG_PRICE_PER_STORE, offlinePriceItem)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)

        // get intent extra
        intent.extras?.let {
            productSku = it.getString(ARG_PRODUCT_SKU, null)
            productId = it.getString(ARG_PRODUCT_ID, null)
            productJdaSku = it.getString(ARG_PRODUCT_JDA_SKU, null)
            offlinePriceItem = it.getParcelable(ARG_PRICE_PER_STORE)
        }

        handleChangeLanguage()
        bindView()
        retrieveProductDetail()
        observeCompareProducts()
    }

    override fun onResume() {
        super.onResume()
        // analytics
        analytics?.trackScreen(Screen.PRODUCT_DETAIL)
        updateShoppingCartBadge()
    }

    override fun getProductCompareView(): ProductCompareView? {
        productCompareView?.addProductCompareViewListener(this)
        return productCompareView
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
        // check compare product
        checkCompareProduct()

        if (requestCode == REQUEST_UPDATE_IMAGE_SELECTED && resultCode == RESULT_IMAGE_SELECTED){
            val fragment = supportFragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT)
            val imageSelectedIndex = data?.extras?.getInt(ARG_UPDATE_IMAGE_SELECTED) ?: 0
            (fragment as DetailFragment).updateImageSelected(imageSelectedIndex)
        }

        if (requestCode == REQUEST_UPDATE_LANGUAGE) {
            // check language
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_PRODUCT_SKU, productSku)
        outState.putParcelable(ARG_PRICE_PER_STORE, offlinePriceItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        productSku = savedInstanceState.getString(ARG_PRODUCT_SKU)
        offlinePriceItem = savedInstanceState.getParcelable(ARG_PRICE_PER_STORE)
    }

    private fun bindView() {
        mToolbar = findViewById(R.id.toolbar)
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
        clearDeliveryInfo()
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

    override fun addProductToCompare(product: Product?, isCompare: Boolean) {
        product ?: return
        if (isCompare) {
            addToCompare(product)
        } else {
            removeFromCompare(product)
        }
    }

    override fun getChildProduct(): ArrayList<Product> = childProductList

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

    // region {@link PowerBuyShoppingCartView.OnClickListener}
    override fun onShoppingCartClick(view: View) {
        if (database.cacheCartItems.size > 0) {
            ShoppingCartActivity.startActivity(this, view)
        } else {
            showCommonDialog(resources.getString(R.string.not_have_products_in_cart))
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
            showCommonDialog(getString(R.string.some_thing_wrong))
        }
    }
    // endregion

    // region {@link ProductCompareView.ProductCompareViewListener}
    override fun resetCompareProducts() {
        database.deleteAllCompareProduct()
        unCheckCompareProduct()
    }

    override fun openComparePage() {
        CompareActivity.startCompareActivity(this, productCompareView)
    }
    // endregion

    override fun getDeliveryInfoList(): List<DeliveryInfo> = this.deliveryInfoList

    override fun setDeliveryInfoList(deliveryInfos: List<DeliveryInfo>) {
        this.deliveryInfoList.clear()
        this.deliveryInfoList.addAll(deliveryInfos)
    }

    override fun setFreebieSKUs(freebieSKUs: ArrayList<String>) {
        this.freebieSKUs = freebieSKUs
    }

    override fun setBadgeSelects(badgeSelects: ArrayList<String>) {
        this.badgesSelects = badgeSelects
    }

    override fun setFreeItems(freeItems: ArrayList<Product>) {
        this.freeItems = freeItems
    }

    override fun getBadgeSelects(): ArrayList<String> = this.badgesSelects

    override fun getFreeItems(): ArrayList<Product> = this.freeItems

    override fun getFreebieSKUs(): ArrayList<String> = this.freebieSKUs

    // region badgeListener
    override fun onBadgeSelectedListener(position: Int) {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_EXTENSION_FRAGMENT)
        val childFragment = (fragment as ProductExtensionFragment).childFragmentManager.findFragmentByTag(TAB_PROMOTION_FREEBIE)
        childFragment?.let {
            (childFragment as ProductPromotionFragment).badgeSelected(position)
        }
    }

    private fun clearDeliveryInfo() {
        this.deliveryInfoList.clear()
    }

    // region retrieve product
    private fun retrieveProductByBarcode(barcode: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductByBarcode(barcode,
                object : ApiResponseCallback<Product?> {
                    override fun success(response: Product?) {
                        runOnUiThread {
                            handleGetProductSuccess(response)
                        }
                    }

                    override fun failure(error: APIError) {
                        runOnUiThread {
                            dismissProgressDialog()
                            showCommonAPIErrorDialog(error)
                        }
                    }
                })
    }

    private fun retrieveProductByProductJda(jdaSku: String) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).getProductByProductJda(jdaSku,
                object : ApiResponseCallback<Product?> {
                    override fun success(response: Product?) {
                        runOnUiThread {
                            handleGetProductSuccess(response)
                        }
                    }

                    override fun failure(error: APIError) {
                        runOnUiThread {
                            dismissProgressDialog()
                            showCommonAPIErrorDialog(error)
                        }
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
                    showCommonAPIErrorDialog(error)
                }
            }
        })
    }

    private fun handleGetProductSuccess(response: Product?) {
        if (response != null) {
            if (response.typeId == "configurable") {
                checkProductConfig(response)
            } else {
                startProductDetailFragment(response)
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
                            startProductDetailFragment(product)
                        }
                    }
                }

                override fun failure(error: APIError) {
                    runOnUiThread {
                        Log.d("ProductConfigChild", "${error.errorCode} ${error.errorMessage}")
                        startProductDetailFragment(product)
                    }
                }
            })
        }
    }
    // end region

    private fun startProductDetailFragment(product: Product) {
        dismissProgressDialog()
        // set product
        this@ProductDetailActivity.productSku = product.sku
        if (!isChatAndShop()) {
            if (offlinePriceItem != null){
                product.price = offlinePriceItem!!.price
                if (offlinePriceItem!!.specialPrice > 0) {
                    product.specialPrice = offlinePriceItem!!.specialPrice
                    product.specialFromDate = null
                    product.specialToDate = null
                    if (offlinePriceItem!!.specialFromDate != null) {
                        product.specialFromDate = offlinePriceItem!!.specialFromDate
                    }
                    if (offlinePriceItem!!.specialToDate != null) {
                        product.specialToDate = offlinePriceItem!!.specialToDate
                    }
                } else {
                    product.specialPrice = 0.0
                    product.specialFromDate = null
                    product.specialToDate = null
                }
            } else {
                // this case is don't have offline price will display online normal price only
                product.specialPrice = 0.0
                product.specialFromDate = null
                product.specialToDate = null
            }
        }
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
    private fun addToCompare(product: Product) {
        showProgressDialog()
        val count = database.compareProducts.size
        if (count >= 4) {
            dismissProgressDialog()
            unCheckCompareProduct()
            showAlertDialog(getString(R.string.alert_count))
        } else {
            val compareProduct = database.getCompareProduct(product.sku)
            // is added?
            if (compareProduct != null) {
                dismissProgressDialog()
                showAlertDialog(getString(R.string.format_alert_compare, compareProduct.name))
            } else {
                // store compare product to database
                saveCompareProduct(product)
            }
        }
    }

    private fun saveCompareProduct(product: Product) {
        database.saveCompareProduct(product, object : DatabaseListener {
            override fun onSuccessfully() {
                dismissProgressDialog()
                Toast.makeText(this@ProductDetailActivity, "${getString(R.string.added_to_compare)}.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(error: Throwable) {
                dismissProgressDialog()
                Log.d(TAG, "" + error.message)
            }
        })
    }

    private fun removeFromCompare(product: Product) {
        showProgressDialog()
        val count = database.compareProducts.size
        if (count >= 1) {
            database.deleteCompareProduct(product.sku)
        }
        dismissProgressDialog()
    }
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
                if (!isFinishing && !isDestroyed) {
                    dialog.show()
                }
                dismissProgressDialog()
            }
        })
    }
    // endregion

    private fun startAvailableStore(product: Product) {

        analytics?.trackViewStoreStock(product.sku)

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

    private fun isChatAndShop(): Boolean {
        return database.userInformation.user?.userLevel == 3L
    }

    private fun checkCompareProduct() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT)
        (fragment as DetailFragment).updateCompareCheckBox()
    }

    private fun unCheckCompareProduct() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT)
        (fragment as DetailFragment).unCheckCompareCheckBox()
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }

    private fun dismissProgressDialog() {
        if (!isFinishing && !isDestroyed && progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }
}
