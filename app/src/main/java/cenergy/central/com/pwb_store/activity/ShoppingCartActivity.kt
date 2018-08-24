package cenergy.central.com.pwb_store.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.adapter.ShoppingCartAdapter
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.DialogUtils
import cenergy.central.com.pwb_store.view.PowerBuyTextView

class ShoppingCartActivity : AppCompatActivity(), ShoppingCartAdapter.ShoppingCartListener {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var mToolbar: Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var backToShopButton: CardView
    private lateinit var paymentButton: CardView
    private lateinit var searchImageView: ImageView
    private lateinit var totalPrice: PowerBuyTextView
    private lateinit var title: PowerBuyTextView
    private var mProgressDialog: ProgressDialog? = null
    var shoppingCartAdapter = ShoppingCartAdapter(this)
    private var cartId : String = ""
    companion object {
        private const val CART_ID = "CART_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        cartId = intent.getStringExtra(CART_ID)
        initView()
        setUpToolbar()
        showProgressDialog()
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = shoppingCartAdapter

        getCartItem()
    }

    private fun setUpToolbar() {
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
        searchImageView.setOnClickListener { v ->
            val intent = Intent(this, SearchActivity::class.java)
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeScaleUpAnimation(v, 0, 0, v.width, v.height)
                            .toBundle())
        }
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        searchImageView = findViewById(R.id.search_button)
        recycler = findViewById(R.id.recycler_view_shopping_cart)
        totalPrice = findViewById(R.id.txt_total_price_shopping_cart)
        title = findViewById(R.id.txt_header_shopping_cart)
        backToShopButton = findViewById(R.id.back_to_shop)
        paymentButton = findViewById(R.id.payment)
        preferenceManager = PreferenceManager(this)

        updateTitle(0) // default title

        backToShopButton.setOnClickListener { finish() }
        paymentButton.setOnClickListener {
            val intent = PaymentActivity.intent(this)
            ActivityCompat.startActivity(this, intent, ActivityOptionsCompat
                    .makeScaleUpAnimation(paymentButton, 0, 0, paymentButton.width, paymentButton.height)
                    .toBundle())
        }
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
    //end region

    private fun getCartItem() {
        HttpManagerMagento.getInstance().viewCart(cartId, object : ApiResponseCallback<List<CartItem>> {
            override fun success(response: List<CartItem>?) {
                if (response != null) {
                    shoppingCartAdapter.cartItemList = response

                    var sum = 0
                    for (item in response) {
                        sum += item.qty ?: 0
                    }
                    updateTitle(sum)

                    var total = 0.0
                    response.forEach {
                        total += it.qty!! * it.price!!
                    }
                    totalPrice.text = total.toString()
                    if (mProgressDialog != null) {
                        mProgressDialog?.dismiss()
                    }
                }
            }

            override fun failure(error: APIError) {

            }
        })
    }

    private fun deleteItem(cartId: String, itemId: Long) {
        showProgressDialog()
        HttpManagerMagento.getInstance().deleteItem(cartId, itemId, object : ApiResponseCallback<Boolean> {
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

    private fun deleteItemInLocal(itemId: Long) {
        RealmController.with(this).deleteCartItem(itemId)
    }
}