package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
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
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.extensions.checkItems
import cenergy.central.com.pwb_store.extensions.getInstallments
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.api.PromotionAPI
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.CartResponse
import cenergy.central.com.pwb_store.model.response.CartTotal
import cenergy.central.com.pwb_store.model.response.PromotionResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.*
import cenergy.central.com.pwb_store.view.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var warningCreditCardTv: PowerBuyTextView
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
    private var promotions: ArrayList<PromotionResponse> = arrayListOf()
    private var products: List<Product> = arrayListOf()

    // Firebase remote config
    private lateinit var fbRemoteConfig: FirebaseRemoteConfig
    private val analytics: Analytics? by lazy { Analytics(this) }

    companion object {
        const val RESULT_UPDATE_PRODUCT = 59000

        @JvmStatic
        fun startActivity(context: Context, view: View) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            ActivityCompat.startActivityForResult(
                (context as Activity), intent, REQUEST_UPDATE_LANGUAGE,
                ActivityOptionsCompat
                    .makeScaleUpAnimation(view, 0, 0, view.width, view.height)
                    .toBundle()
            )
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
        fbRemoteConfig = RemoteConfigUtils.initFirebaseRemoteConfig()

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
        warningCreditCardTv = findViewById(R.id.warningCreditCardTv)
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

        val isSupportCouponOn =
            fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_SUPPORT_COUPON_ON)
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
            cacheCartItems[0].branch != null && retailer != null
        ) {
            val item = cacheCartItems[0]
            cartDescriptionTextView.visibility = View.VISIBLE
            cartDescriptionTextView.text = getString(
                R.string.format_shopping_cart_ispu,
                retailer.extension!!.deliverlyPromiseIspu ?: ""
            )
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

    override fun getProductCompareView(): ProductCompareView? = null

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
        warningCreditCardTv.text = getString(R.string.warning_credit_care_on_top)
        couponCodeEdt.hint = getString(R.string.enter_promo_code_hint)
        discountTitle.text = getString(R.string.discount)
        promotionTitle.text = getString(R.string.promotion_code)
        totalTitle.text = getString(R.string.total_price)
    }

    private fun getCartItem() {
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).viewCart(
                cartId,
                object : ApiResponseCallback<Pair<CartResponse?, List<Product>>> {
                    override fun success(response: Pair<CartResponse?, List<Product>>?) {
                        runOnUiThread {
                            if (response?.first != null) {
                                cartResponse = response.first
                                products = response.second
                                updateCacheCartItem(cartResponse, response.second)
                                getCartTotal()
                            } else {
                                mProgressDialog?.dismiss()
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

    private fun updateCacheCartItem(cartResponse: CartResponse?, products: List<Product>) {
        cartResponse?.let {
            it.items.forEach { item ->
                val product = products.firstOrNull { p -> item.sku == p.sku }
                product?.let {
                    val cacheItem = database.getCacheCartItem(product.sku)
                    if (cacheItem != null) {
                        database.saveCartItem(CacheCartItem.updateCartItem(cacheItem, product))
                    } else {
                        database.saveCartItem(CacheCartItem.asCartItem(item, product))
                    }
                }
            }
        }
    }

    private fun getCartTotal() {
        preferenceManager.cartId?.let { cartId ->
            CartUtils(this).viewCartTotal(cartId, object : ApiResponseCallback<CartTotal> {
                override fun success(response: CartTotal?) {
                    runOnUiThread {
                        mProgressDialog?.dismiss()
                        if (response != null) {
                            retrievePromotion(response)
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

    private fun retrievePromotion(shoppingCartResponse: CartTotal) {
        if (cartResponse != null && cartResponse!!.items.isNotEmpty()) {
            // not retrieve promotion of freebie items
            val skuList =
                cartResponse!!.items.filter { it.price != null && it.price!! > 0.0 }.map { it.sku }
            val result = TextUtils.join(",", skuList)
            PromotionAPI.retrievePromotionBySKUs(
                this,
                result,
                object : ApiResponseCallback<List<PromotionResponse>> {
                    override fun success(response: List<PromotionResponse>?) {
                        if (response != null) {
                            promotions.clear()
                            promotions.addAll(response)
                            updateViewShoppingCart(shoppingCartResponse)
                        }
                    }

                    override fun failure(error: APIError) {
                        updateViewShoppingCart(shoppingCartResponse)
                    }
                })
        } else {
            updateViewShoppingCart(shoppingCartResponse)
        }
    }

    private fun updateViewShoppingCart(shoppingCartResponse: CartTotal) {
        if (cartResponse != null) {
            cartItemList = cartResponse!!.items

            val items = shoppingCartResponse.items ?: listOf()

            var total = shoppingCartResponse.totalPrice

            var discountPriceValue = 0.0
            val discount =
                shoppingCartResponse.totalSegment?.firstOrNull { it.code == TotalSegment.DISCOUNT_KEY }
            if (discount != null) {
                discountPriceValue = discount.value.toString().toStringDiscount()
            }

            val isSupportCouponOn =
                fbRemoteConfig.getBoolean(RemoteConfigUtils.CONFIG_KEY_SUPPORT_COUPON_ON)
            if (isSupportCouponOn) { // support coupon?
                val coupon =
                    shoppingCartResponse.totalSegment?.firstOrNull { it.code == TotalSegment.COUPON_KEY }
                if (coupon != null) {
                    val couponDiscount = TotalSegment.getCouponDiscount(coupon.value.toString())
                    val couponDiscountAmount = couponDiscount?.couponAmount.toStringDiscount()
                    val hasCoupon =
                        (couponDiscountAmount > 0 && !couponDiscount?.couponCode.isNullOrEmpty())
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

            if (promotions.size > 1) {
                warningCreditCardTv.visibility =
                    if (isErrorPromotion() || isErrorInstallmentPlans()) View.VISIBLE else View.GONE
            } else {
                warningCreditCardTv.visibility = View.GONE
            }

            shoppingCartAdapter.setAdapter(
                products,
                promotions,
                items.checkItems(cartItemList)
            ) // update items in shopping cart

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

    private fun isErrorPromotion(): Boolean {
        if (promotions.isNullOrEmpty()) return false
        val promotionsPerSku = hashMapOf<String, ArrayList<Long>>()
        promotions.map {
            val promotionIds = it.extension?.creditCardPromotions?.mapTo(arrayListOf(), { ccp ->
                ccp.id
            })
            promotionsPerSku[it.sku] = promotionIds ?: arrayListOf()
        }
        val promotionIds = arrayListOf<Long>()
        promotionsPerSku.values.forEach {
            promotionIds.addAll(it)
        }
        val groupIds = promotionIds.groupBy { it }
        val sum = promotionsPerSku.values.map { it.size }.sumBy { it }
        return if (sum > 0) {
            promotionsPerSku.filter { it.value.size != groupIds.keys.size }.isNotEmpty()
        } else {
            false // all product not have cc-on-top
        }
    }

    private fun isErrorInstallmentPlans(): Boolean {
        val installmentPerSku = hashMapOf<String, ArrayList<Int>>()
        val periodPerSku = hashMapOf<String, ArrayList<Int>>()
        promotions.map { item ->
            products.firstOrNull { it.sku == item.sku }?.let {
                val bankIds = it.getInstallments().mapTo(arrayListOf(), { ism -> ism.bankId })
                installmentPerSku[it.sku] = bankIds
                val plans = arrayListOf<Int>()
                it.getInstallments().forEach { i ->
                    plans.addAll(i.installments.map { plan -> plan.period })
                }
                periodPerSku[it.sku] = plans
            }
        }
        val installmentIds = arrayListOf<Int>()
        installmentPerSku.values.forEach {
            installmentIds.addAll(it)
        }
        val period = arrayListOf<Int>()
        periodPerSku.values.forEach {
            period.addAll(it)
        }
        val groupBankIds = installmentIds.groupBy { it }
        val groupPeriod = period.groupBy { it }
        val sum = installmentPerSku.values.map { it.size }.sumBy { it }
        return if (sum > 0) {
            installmentPerSku.filter { it.value.size != groupBankIds.keys.size }.isNotEmpty()
                    && periodPerSku.filter { it.value.size != groupPeriod.keys.size }.isNotEmpty()
        } else {
            false // all product not have installment plan
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
                CartUtils(this).addCoupon(
                    cartId,
                    promotionCode,
                    object : ApiResponseCallback<Boolean> {
                        override fun success(response: Boolean?) {
                            if (response != null) {
                                refreshShoppingCart()
                                Toast.makeText(
                                    this@ShoppingCartActivity,
                                    getString(R.string.used_promo_code, promotionCode),
                                    Toast.LENGTH_SHORT
                                ).show()
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
            HttpManagerMagento.getInstance(this)
                .deleteItem(cartId, itemId, object : ApiResponseCallback<Boolean> {
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
            HttpManagerMagento.getInstance(this)
                .updateItem(cartId, itemId, qty, branch, isChatAndShop,
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
        return String.format(
            Locale.getDefault(), "%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()
            ).format(java.lang.Double.parseDouble(price))
        )
    }

    private fun getDisplayDiscount(unit: String, price: String): String {
        return String.format(
            Locale.getDefault(), "-%s %s", unit, NumberFormat.getInstance(
                Locale.getDefault()
            ).format(java.lang.Double.parseDouble(price))
        )
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
        showCommonDialog(
            null,
            getString(R.string.title_clear_cart),
            DialogInterface.OnClickListener { dialog, which ->
                clearCart() // clear item cart
                dialog?.dismiss()
                finish()
            })
    }

    private fun hideKeyBoard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            inputManager.hideSoftInputFromInputMethod(currentFocus!!.windowToken, 0)
        }
    }
}