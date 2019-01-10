package cenergy.central.com.pwb_store.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.Contextor
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import cenergy.central.com.pwb_store.view.PowerBuyTextView
import kotlinx.android.synthetic.main.activity_shopping_cart.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class ShoppingCartActivity : BaseActivity(), ShoppingCartAdapter.ShoppingCartListener, LanguageButton.LanguageListener {

    private lateinit var languageButton: LanguageButton
    private lateinit var mToolbar: Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var backToShopButton: CardView
    private lateinit var paymentButton: CardView
    private lateinit var searchImageView: ImageView
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

    companion object {
        private const val CART_ID = "CART_ID"

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
        if (requestCode == BaseActivity.REQUEST_UPDATE_LANGUAGE) {
            if (getSwitchButton() != null) {
                getSwitchButton()!!.setDefaultLanguage(preferenceManager.getDefaultLanguage())
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
        searchImageView.visibility = View.GONE
    }

    private fun initView() {
        unit = Contextor.getInstance().context.getString(R.string.baht)
        mToolbar = findViewById(R.id.toolbar)
        searchImageView = findViewById(R.id.search_button)
        recycler = findViewById(R.id.recycler_view_shopping_cart)
        totalPrice = findViewById(R.id.txt_total_price_shopping_cart)
        tvT1 = findViewById(R.id.txt_t1_shopping_cart)
        title = findViewById(R.id.txt_header_shopping_cart)
        backToShopButton = findViewById(R.id.back_to_shop)
        paymentButton = findViewById(R.id.payment)

        updateTitle(0) // default title

        backToShopButton.setOnClickListener { finish() }
    }

    private fun updateTitle(count: Int) {
        title.text = getString(R.string.format_header_cart_items, count.toString())
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
    override fun onDeleteItem(cartId: String, itemId: Long) {
        deleteItem(cartId, itemId)
    }

    override fun onUpdateItem(cartId: String, itemId: Long, qty: Int) {
        updateItem(cartId, itemId, qty)
    }
    //end region

    //region {@link implement LanguageButton.LanguageListener  }
    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        showProgressDialog()
        updateView()
        getCartItem()
    }

    override fun getSwitchButton(): LanguageButton? = languageButton
    //end region

    override fun getStateView(): NetworkStateView? = networkStateView

    private fun updateView() {
        // update shit! button using card view =[]='
        backToShopButton.findViewById<TextView>(R.id.back_title_text_view).setText(R.string.shopping)
        paymentButton.findViewById<TextView>(R.id.title_text_view).setText(R.string.payment_header)

        // update text label
        findViewById<TextView>(R.id.label_total_text_view).setText(R.string.total_price)
    }

    private fun getCartItem() {
        HttpManagerMagento.getInstance(this).viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
            override fun success(response: List<CartItem>?) {
                if (response != null) {
                    updateViewShoppingCart(response)
                    mProgressDialog?.dismiss()
                } else {
                    mProgressDialog?.dismiss()
                    showAlertDialog("", resources.getString(R.string.cannot_get_cart_item))
                }
            }

            override fun failure(error: APIError) {
                mProgressDialog?.dismiss()
                if (error.errorCode == APIError.INTERNAL_SERVER_ERROR.toString()) {
                    clearCart()
                    finish()
                } else {
                    showAlertDialog(resources.getString(R.string.cannot_get_cart_item),
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                finish()
                            })
                }
            }
        })
    }

    private fun updateViewShoppingCart(response: List<CartItem>) {
        cartItemList = response
        shoppingCartAdapter.cartItemList = cartItemList

        var sum = 0
        for (item in cartItemList) {
            sum += item.qty ?: 0
        }
        updateTitle(sum)

        var total = 0
        cartItemList.forEach {cartItem ->
            val item = database.getCacheCartItem(cartItem.id)
            if (item != null) {
                total += cartItem.qty!! * cartItem.price!!.roundToInt()
            }
        }
        val vat = (total * 0.07).roundToInt()
        val t1Points = ((total + vat) - ((total + vat) % 50)) / 50
        totalPrice.text = getDisplayPrice(unit, (total + vat).toString())
        tvT1.text = resources.getString(R.string.t1_points, t1Points)
        checkCanClickPayment()
    }

    private fun checkCanClickPayment() {
        if (cartItemList.isNotEmpty()) {
            paymentButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.powerBuyPurple))
            paymentButton.setOnClickListener {
                PaymentActivity.intent(this)
            }
        } else {
            paymentButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.hintColor))
            paymentButton.isEnabled = false
        }
    }

    private fun deleteItem(cartId: String, itemId: Long) {
        showProgressDialog()
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
                Log.d("DeleteItem", "error ${error.errorMessage}")
                mProgressDialog?.dismiss()
            }

        })
    }

    private fun updateItem(cartId: String, itemId: Long, qty: Int) {
        showProgressDialog()
        HttpManagerMagento.getInstance(this).updateItem(cartId, itemId, qty, object : ApiResponseCallback<CartItem> {
            override fun success(response: CartItem?) {
                saveCartItemInLocal(response)
                getCartItem()
            }

            override fun failure(error: APIError) {
                Log.d("UpdateItem", "update fail.")
                mProgressDialog?.dismiss()
            }
        })
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

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { dialog, which -> dialog.dismiss() }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.show()
    }

    private fun showAlertDialog(message: String, onClick: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(message)
                .setPositiveButton(R.string.ok, onClick)

        builder.show()
    }

    private fun clearCart() {
        database.deleteAllCacheCartItem()
        preferenceManager.clearCartId()
    }
}