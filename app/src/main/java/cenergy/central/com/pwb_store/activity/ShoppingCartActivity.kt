package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.extensions.toStringDiscount
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Branch
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.response.CartResponse
import cenergy.central.com.pwb_store.model.response.CartTotalResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.CartUtils
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.*
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
    private lateinit var searchImageView: ImageView
    private lateinit var layoutDiscountPrice: LinearLayout
    private lateinit var discountPrice: PowerBuyTextView
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var title: PowerBuyTextView
    private lateinit var tvT1: PowerBuyTextView
    private lateinit var cartItemList: List<CartItem>
    private var mProgressDialog: ProgressDialog? = null
    // data
    private var shoppingCartAdapter = ShoppingCartAdapter(this, false)
    private var cartId: String = ""
    private var unit: String = ""
    private val database = RealmController.getInstance()
    private var hasChangingData: Boolean = false
    private var checkoutType: CheckoutType = CheckoutType.NORMAL
    private var branch: Branch? = null
    private var cartResponse: CartResponse? = null

    companion object {
        private const val CART_ID = "CART_ID"
        const val RESULT_UPDATE_PRODUCT = 59000

        @JvmStatic
        fun startActivity(context: Context, view: View, cartId: String?) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            intent.putExtra(CART_ID, cartId)
            ActivityCompat.startActivityForResult((context as Activity), intent, REQUEST_UPDATE_LANGUAGE,
                    ActivityOptionsCompat
                            .makeScaleUpAnimation(view, 0, 0, view.width, view.height)
                            .toBundle())
        }

        @JvmStatic
        fun startActivity(context: Context, cartId: String?) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            intent.putExtra(CART_ID, cartId)
            (context as Activity).startActivityForResult(intent, REQUEST_UPDATE_LANGUAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        languageButton = findViewById(R.id.switch_language_button)
        networkStateView = findViewById(R.id.networkStateView)
        handleChangeLanguage()

        cartId = intent.getStringExtra(CART_ID)
        initView()
        setUpToolbar()
        showProgressDialog()
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = shoppingCartAdapter

        getCartItem()
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
        discountPrice = findViewById(R.id.txt_discount_shopping_cart)
        totalPrice = findViewById(R.id.txt_total_price_shopping_cart)
        tvT1 = findViewById(R.id.txt_t1_shopping_cart)
        title = findViewById(R.id.txt_header_shopping_cart)
        backToShopButton = findViewById(R.id.back_to_shop)

        // setup payment button
        paymentButton = findViewById(R.id.payment)
        paymentButton.setImageDrawable(R.drawable.ic_check)

        updateTitle(0) // default title

        forceUpdateView()

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
            storeNameTextView.text = item.branch?.storeName?: ""
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
    override fun onDeleteItem(itemId: Long) {
        deleteItem(itemId)
    }

    override fun onUpdateItem(itemId: Long, qty: Int) {
        updateItem(itemId, qty)
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
        findViewById<TextView>(R.id.label_total_text_view).setText(R.string.total_price)
    }

    private fun getCartItem() {
        CartUtils(this).viewCart(cartId, object : ApiResponseCallback<CartResponse> {
            override fun success(response: CartResponse?) {
                if (response != null) {
                    cartResponse = response
                    getCartTotal()
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", resources.getString(R.string.cannot_get_cart_item))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                displayError(error)
            }
        })
    }

    private fun getCartTotal(){
        CartUtils(this).viewCartTotal(cartId, object : ApiResponseCallback<CartTotalResponse>{
            override fun success(response: CartTotalResponse?) {
                mProgressDialog?.dismiss()
                if (response != null) {
                    updateViewShoppingCart(response)
                } else {
                    showAlertDialog("", resources.getString(R.string.cannot_get_cart_item))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                displayError(error)
            }

        })
    }

    private fun updateViewShoppingCart(shoppingCartResponse: CartTotalResponse) {
        if (cartResponse != null){
            cartItemList = cartResponse!!.items
            shoppingCartAdapter.cartItemList = cartItemList

            updateTitle(shoppingCartResponse.qty)
            val total = shoppingCartResponse.totalPrice
            val t1Points = (total - (total % 50)) / 50
            val discount = shoppingCartResponse.discountPrice.toStringDiscount()
            if (discount > 0) {
                layoutDiscountPrice.visibility = View.VISIBLE
                discountPrice.text = getDisplayDiscount(unit, discount.toString())
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
                PaymentActivity.startCheckout(this, checkoutType == CheckoutType.ISPU)
            }
        } else {
            paymentButton.setButtonDisable(true)
        }
    }

    private fun deleteItem(itemId: Long) {
        hasChangingData = true
        showProgressDialog()
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
                    DialogHelper(this@ShoppingCartActivity).showErrorDialog(error)
                }

            })
        }
    }

    private fun updateItem(itemId: Long, qty: Int) {
        hasChangingData = true
        showProgressDialog()
        preferenceManager.cartId?.let { cartId ->
            HttpManagerMagento.getInstance(this).updateItem(cartId, itemId, qty, branch,
                    object : ApiResponseCallback<CartItem> {
                        override fun success(response: CartItem?) {
                            saveCartItemInLocal(response)
                            getCartItem()
                        }

                        override fun failure(error: APIError) {
                            mProgressDialog?.dismiss()
                            showAlertDialog("", getString(R.string.exceeds_maximum))
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

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }


    private fun displayError(error: APIError) {
        if (error.errorCode == null) {
            showAlertDialog("", getString(R.string.not_connected_network))
        } else {
            when (error.errorCode) {
                "408", "404" -> {
                    showAlertDialog("", getString(R.string.server_not_found))
                }
                APIError.INTERNAL_SERVER_ERROR.toString() -> {
                    clearCart()
                    finish()
                }
                else -> showAlertDialog("", getString(R.string.some_thing_wrong))
            }
        }
    }
}