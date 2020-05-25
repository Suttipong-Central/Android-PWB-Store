package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.extensions.checkItems
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.TotalSegment
import cenergy.central.com.pwb_store.model.response.CartResponse
import cenergy.central.com.pwb_store.model.response.CartTotalResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.*
import cenergy.central.com.pwb_store.view.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import java.text.NumberFormat
import java.util.*

class ShoppingCartActivity : BaseActivity(), ShoppingCartAdapter.ShoppingCartListener {

    private lateinit var languageButton: LanguageButton
    private lateinit var networkStateView: NetworkStateView
    private lateinit var mToolbar: Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var backToShopButton: PowerBuyBackButton
    private lateinit var paymentButton: PowerBuyIconButton
    private lateinit var couponBtn: PowerBuyIconButton
    private lateinit var searchImageView: ImageView
    private lateinit var layoutDiscountPrice: LinearLayout
    private lateinit var layoutPromotionPrice: LinearLayout
    private lateinit var discountTitle: PowerBuyTextView
    private lateinit var discountPrice: PowerBuyTextView
    private lateinit var promotionTitle: PowerBuyTextView
    private lateinit var promotionPrice: PowerBuyTextView
    private lateinit var totalTitle: PowerBuyTextView
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var title: PowerBuyTextView
    private lateinit var tvT1: PowerBuyTextView
    private lateinit var couponCodeEdt: PowerBuyEditText
    private lateinit var cartItemList: List<CartItem>
    private var mProgressDialog: ProgressDialog? = null
    // data
    private var shoppingCartAdapter = ShoppingCartAdapter(this, false)
    private var unit: String = ""
    private val database = RealmController.getInstance()
    private var hasChangingData: Boolean = false
    private var checkoutType: CheckoutType = CheckoutType.NORMAL
    private var branch: Branch? = null
    private var cartResponse: CartResponse? = null
    private var isCouponAdded = false
    private var promotionCode = ""

    // Firebase remote config
    private lateinit var fbRemoteConfig: FirebaseRemoteConfig
    private var cacheExpiration: Long = 3600 // 1 hour in seconds.
    private val analytics: Analytics? by lazy { Analytics(this) }

    companion object {
        private const val CART_ID = "CART_ID"
        const val RESULT_UPDATE_PRODUCT = 59000

        @JvmStatic
        fun startActivity(context: Context, view: View) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            ActivityCompat.startActivityForResult((context as Activity), intent, REQUEST_UPDATE_LANGUAGE,
                    ActivityOptionsCompat
                            .makeScaleUpAnimation(view, 0, 0, view.width, view.height)
                            .toBundle())
        }

        @JvmStatic
        fun startActivity(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        // setup remote config
        setupRemoteConfig()

        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        handleChangeLanguage()
        initView()
        setUpToolbar()
        showProgressDialog()
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = shoppingCartAdapter

        getCartItem()
    }

    override fun onResume() {
        super.onResume()
        analytics?.trackScreen(Screen.SHOPPING_CART)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_LANGUAGE) {
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mToolbar.setNavigationOnClickListener { finishShippingCart() }
        searchImageView.visibility = View.GONE
    }

    private fun finishShippingCart() {
        if (hasChangingData) {
            setResult(RESULT_UPDATE_PRODUCT)
        }
        finish()
    }

    private fun initView() {
        unit = Contextor.getInstance().context.getString(R.string.baht)
        mToolbar = findViewById(R.id.toolbar)
        searchImageView = findViewById(R.id.search_button)
        recycler = findViewById(R.id.recycler_view_shopping_cart)
        layoutDiscountPrice = findViewById(R.id.layout_discount_shopping_cart)
        layoutPromotionPrice = findViewById(R.id.layout_promotion_shopping_cart)
        couponCodeEdt = findViewById(R.id.couponCodeEdt)
        discountTitle = findViewById(R.id.label_discount_text_view)
        discountPrice = findViewById(R.id.txt_discount_shopping_cart)
        promotionTitle = findViewById(R.id.label_promotion_text_view)
        promotionPrice = findViewById(R.id.txt_promotion_shopping_cart)
        totalTitle = findViewById(R.id.label_total_text_view)
        totalPrice = findViewById(R.id.txt_total_price_shopping_cart)
        tvT1 = findViewById(R.id.txt_t1_shopping_cart)
        title = findViewById(R.id.txt_header_shopping_cart)
        backToShopButton = findViewById(R.id.back_to_shop)
        couponBtn = findViewById(R.id.couponBtn)
        couponBtn.setText(getString(R.string.add_coupon))

        val isSupportCouponOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_SUPPORT_COUPON_ON)
        if (isSupportCouponOn) { // support coupon?
            couponBtn.visibility = View.VISIBLE
            couponCodeEdt.visibility = View.VISIBLE
        }

        // setup payment button
        paymentButton = findViewById(R.id.payment)
        paymentButton.setImageDrawable(R.drawable.ic_check)

        updateTitle(0) // default title

        forceUpdateView()
        backToShopButton.setOnClickListener {
            finishShippingCart()
        }

        // setup store name
        storeNameTextView.setOnClickListener {
            PaymentActivity.startEditStorePickup(this)
            finish()
        }

        backToShopButton.setOnClickListener {
            finishShippingCart()
        }
    }

    private fun update2hDelivery() {
        // is Checkout ISPU?
        val cacheCartItems = database.cacheCartItems
        val retailer = cartResponse?.extension?.retailer
        if (cacheCartItems != null && cacheCartItems.isNotEmpty() &&
                cacheCartItems[0].branch != null && retailer != null) {
            val item = cacheCartItems[0]
            cartDescriptionTextView.visibility = View.VISIBLE
            cartDescriptionTextView.text = getString(R.string.format_shopping_cart_ispu,
                    retailer.extension!!.deliverlyPromiseIspu ?: "")
            // store name use from cache follow website
            storeNameTextView.text = item.branch?.storeName ?: ""
            this.checkoutType = CheckoutType.ISPU
            this.branch = item.branch
        } else {
            checkoutType = CheckoutType.NORMAL
            cartDescriptionTextView.visibility = View.GONE
            this.branch = null
        }
    }

    private fun updateTitle(count: Int) {
        title.text = getString(R.string.format_header_cart_items, count.toString())
        update2hDelivery()
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.createProgressDialog(this)
            mProgressDialog?.show()
        } else {
            mProgressDialog?.show()
        }
    }

    //region {@link implement ShoppingCartAdapter.ShoppingCartListener }
    override fun onDeleteItem(itemId: Long, sku: String) {
        deleteItem(itemId, sku)
    }

    override fun onUpdateItem(itemId: Long, qty: Int, isChatAndShop: Boolean) {
        updateItem(itemId, qty, isChatAndShop)
    }
    //end region

    //region {@link implement LanguageButton.LanguageListener  }
    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        refreshShoppingCart()
    }

    override fun getSwitchButton(): LanguageButton? = languageButton
    //end region

    // region state network
    override fun getStateView(): NetworkStateView? = networkStateView

    override fun onNetworkStateChange(state: NetworkInfo.State) {
        super.onNetworkStateChange(state)
        if (currentState == NetworkInfo.State.CONNECTED && forceRefresh) {
            refreshShoppingCart()
        }
    }
    // end region


    private fun refreshShoppingCart() {
        showProgressDialog()
        forceUpdateView()
        getCartItem()
    }

    private fun forceUpdateView() {
        update2hDelivery()

        backToShopButton.setText(getString(R.string.shopping))
        paymentButton.setText(getString(R.string.check_out))

        // update text label
        couponCodeEdt.hint = getString(R.string.enter_promo_code_hint)
        discountTitle.text = getString(R.string.discount)
        promotionTitle.text = getString(R.string.promotion_code)
        totalTitle.text = getString(R.string.total_price)
    }

    private fun getCartItem() {
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).viewCart(cartId, object : ApiResponseCallback<CartResponse> {
                override fun success(response: CartResponse?) {
                    if (response != null) {
                        cartResponse = response
                        getCartTotal()
                    } else {
                        mProgressDialog?.dismiss()
                        showCommonDialog(resources.getString(R.string.cannot_get_cart_item))
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    displayError(error)
                }
            })
        }
    }

    private fun getCartTotal() {
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).viewCartTotal(cartId, object : ApiResponseCallback<CartTotalResponse> {
                override fun success(response: CartTotalResponse?) {
                    runOnUiThread {
                        mProgressDialog?.dismiss()
                        if (response != null) {
                            updateViewShoppingCart(response)
                        } else {
                            showCommonDialog(resources.getString(R.string.cannot_get_cart_item))
                        }
                    }
                }

                override fun failure(error: APIError) {
                    runOnUiThread {
                        mProgressDialog?.dismiss()
                        displayError(error)
                    }
                }
            })
        }
    }

    private fun updateViewShoppingCart(shoppingCartResponse: CartTotalResponse) {
        if (cartResponse != null) {
            cartItemList = cartResponse!!.items

            val items = shoppingCartResponse.items ?: listOf()

            var total = shoppingCartResponse.totalPrice

            var discountPriceValue = 0.0
            val discount = shoppingCartResponse.totalSegment?.firstOrNull { it.code == TotalSegment.DISCOUNT_KEY }
            if (discount != null) {
                discountPriceValue = discount.value.toStringDiscount()
            }

            val isSupportCouponOn = fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_SUPPORT_COUPON_ON)
            if (isSupportCouponOn) { // support coupon?
                val coupon = shoppingCartResponse.totalSegment?.firstOrNull { it.code == TotalSegment.COUPON_KEY }
                if (coupon != null) {
                    val couponDiscount = TotalSegment.getCouponDiscount(coupon.value)
                    val couponDiscountAmount = couponDiscount?.couponAmount.toStringDiscount()
                    val hasCoupon = (couponDiscountAmount > 0 && !couponDiscount?.couponCode.isNullOrEmpty())
                    discountPriceValue -= couponDiscountAmount
                    total -= couponDiscountAmount
                    promotionPrice.text = getDisplayDiscount(unit, couponDiscountAmount.toString())
                    couponBtn.setText(getString(if (hasCoupon) R.string.cancel_coupon else R.string.add_coupon))
                    couponCodeEdt.isEnabled = !hasCoupon
                    isCouponAdded = hasCoupon
                    layoutPromotionPrice.visibility = if (hasCoupon) View.VISIBLE else View.GONE
                    handleCoupon()
                } else {
                    layoutPromotionPrice.visibility = View.GONE
                    couponBtn.setText(getString(R.string.add_coupon))
                    couponCodeEdt.isEnabled = true
                    isCouponAdded = false
                    handleCoupon()
                }
                promotionCode = shoppingCartResponse.couponCode
                couponCodeEdt.setText(promotionCode)
            }
            shoppingCartAdapter.shoppingCartItem = items.checkItems(cartItemList) // update items in shopping cart

            updateTitle(shoppingCartResponse.qty)
            val t1Points = (total - (total % 50)) / 50
            if (discountPriceValue > 0) {
                layoutDiscountPrice.visibility = View.VISIBLE
                discountPrice.text = getDisplayDiscount(unit, discountPriceValue.toString())
                total -= discountPriceValue
            } else {
                layoutDiscountPrice.visibility = View.GONE
            }
            totalPrice.text = getDisplayPrice(unit, total.toString())
            tvT1.text = resources.getString(R.string.t1_points, t1Points.toInt())
            checkCanClickPayment()
        }
    }

    private fun checkCanClickPayment() {
        if (cartItemList.isNotEmpty()) {
            paymentButton.setButtonDisable(false)
            paymentButton.setOnClickListener {
                analytics?.trackStartCheckout() // tracking event
                PaymentActivity.startCheckout(this, checkoutType == CheckoutType.ISPU)
            }
        } else {
            paymentButton.setButtonDisable(true)
        }
    }

    private fun handleCoupon() {
        if (!isCouponAdded) {
            couponBtn.setOnClickListener {
                hideKeyBoard()
                addCoupon()
            }
        } else {
            couponBtn.setOnClickListener {
                deleteCoupon()
            }
        }
    }

    private fun addCoupon() {
        showProgressDialog()
        promotionCode = couponCodeEdt.text.toString()
        if (promotionCode.isNotEmpty()) {
            preferenceManager.cartId?.let { cartId ->
                CartUtils(this).addCoupon(cartId, promotionCode, object : ApiResponseCallback<Boolean> {
                    override fun success(response: Boolean?) {
                        if (response != null) {
                            refreshShoppingCart()
                            Toast.makeText(this@ShoppingCartActivity, getString(R.string.used_promo_code, promotionCode), Toast.LENGTH_SHORT).show()
                        } else {
                            showCommonDialog(R.string.some_thing_wrong)
                        }
                    }

                    override fun failure(error: APIError) {
                        mProgressDialog?.dismiss()
                        showCommonDialog(getString(R.string.invalid_promo_code, promotionCode))
                    }
                })
            }
        } else {
            mProgressDialog?.dismiss()
            showCommonDialog(R.string.enter_promo_code)
        }
    }

    private fun deleteCoupon() {
        showProgressDialog()
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).deleteCoupon(cartId, object : ApiResponseCallback<Boolean> {
                override fun success(response: Boolean?) {
                    refreshShoppingCart()
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    showCommonDialog(getString(R.string.please_try_again))
                }
            })
        }
    }

    private fun deleteItem(itemId: Long, sku: String) {
        hasChangingData = true
        showProgressDialog()
        analytics?.trackRemoveItemFromCart(sku)
        preferenceManager.cartId?.let { cartId ->
            HttpManagerMagento.getInstance(this).deleteItem(cartId, itemId, object : ApiResponseCallback<Boolean> {
                override fun success(response: Boolean?) {
                    if (response == true) {
                        deleteItemInLocal(itemId)
                        getCartItem()
                    } else {
                        Log.d("DeleteItem", "delete fail.")
                        mProgressDialog?.dismiss()
                    }
                }

                override fun failure(error: APIError) {
                    mProgressDialog?.dismiss()
                    showCommonDialog(getString(R.string.please_try_again))
                }
            })
        }
    }

    private fun updateItem(itemId: Long, qty: Int, isChatAndShop: Boolean) {
        hasChangingData = true
        showProgressDialog()
        preferenceManager.cartId?.let { cartId ->
            HttpManagerMagento.getInstance(this).updateItem(cartId, itemId, qty, branch, isChatAndShop,
                    object : ApiResponseCallback<CartItem> {
                        override fun success(response: CartItem?) {
                            saveCartItemInLocal(response)
                            getCartItem()
                        }

                        override fun failure(error: APIError) {
                            mProgressDialog?.dismiss()
                            showCommonDialog(getString(R.string.exceeds_maximum))
                            getCartItem()
                        }
                    })
        }
    }

    private fun deleteItemInLocal(itemId: Long) {
        database.deleteCartItem(itemId)
    }

    private fun saveCartItemInLocal(cartItem: CartItem?) {
        if (cartItem != null) {
            val cacheCartItem = database.getCacheCartItem(cartItem.id)
            cacheCartItem.updateItem(cartItem)
            database.saveCartItem(cacheCartItem)
        } else {
            Log.d("On updateItem", "CartItem null.")
        }
    }

    private fun getDisplayPrice(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun getDisplayDiscount(unit: String, price: String): String {
        return String.format(Locale.getDefault(), "-%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()).format(java.lang.Double.parseDouble(price)))
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }

    private fun displayError(error: APIError) {
        if (error.errorCode == null) {
            showCommonDialog(getString(R.string.not_connected_network))
        } else {
            when (error.errorCode) {
                APIError.REQUEST_TIMEOUT.toString() -> {
                    showCommonDialog(getString(R.string.server_not_found))
                }
                APIError.NOT_FOUND.toString() -> {
                    if (error.errorParameter != null && error.errorParameter.fieldName == "cartId") {
                        showClearCartDialog()
                    } else {
                        showCommonDialog(error.errorMessage)
                    }
                }
                APIError.INTERNAL_SERVER_ERROR.toString() -> {
                    showClearCartDialog()
                }
                else -> showCommonDialog(getString(R.string.some_thing_wrong))
            }
        }
    }

    private fun showClearCartDialog() {
        showCommonDialog(null, getString(R.string.title_clear_cart), DialogInterface.OnClickListener { dialog, which ->
            clearCart() // clear item cart
            dialog?.dismiss()
            finish()
        })
    }

    private fun setupRemoteConfig() {
        fbRemoteConfig = FirebaseRemoteConfig.getInstance()

        if (!BuildConfig.IS_PRODUCTION) { // is Production?
            cacheExpiration = 0
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .build()
        fbRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        fbRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun hideKeyBoard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus!!.windowToken, 0)
        }
    }
}