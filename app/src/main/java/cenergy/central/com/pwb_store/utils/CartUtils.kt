package cenergy.central.com.pwb_store.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.model.body.OptionBody
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController

class CartUtils(private val context: Context) {
    private val prefManager: PreferenceManager = PreferenceManager(context)
    private var callback: AddProductToCartCallback? = null
    private var branchResponse: BranchResponse? = null
    private var options: ArrayList<OptionBody> = arrayListOf()

    fun addProductToCart(product: Product,
                         options: ArrayList<OptionBody> = arrayListOf(),
                         callback: AddProductToCartCallback) {
        this.callback = callback
        val cartId = prefManager.cartId
        this.options = options
        this.branchResponse = null // force no have branch

        if (cartId != null) {
            requestAddToCart(cartId, product)
        } else {
            retrieveCart(product)
        }
    }

    fun addProduct2hToCart(product: Product, branchResponse: BranchResponse,
                           callback: AddProductToCartCallback) {
        this.branchResponse = branchResponse
        this.callback = callback
        val cartId = prefManager.cartId

        if (cartId != null) {
            requestAddToCart(cartId, product)
        } else {
            retrieveCart(product)
        }
    }

    private fun retrieveCart(product: Product) {
        val prefManager = PreferenceManager(context)
        HttpManagerMagento.getInstance(context).getCart(object : ApiResponseCallback<String?> {
            override fun success(response: String?) {
                if (response != null) {
                    prefManager.setCartId(response)

                    // next step add to card
                    requestAddToCart(response, product)
                }
            }

            override fun failure(error: APIError) {
                val dialog = DialogHelper(context).errorDialog(error)
                callback?.onFailure(dialog)
            }
        })
    }

    private fun requestAddToCart(cartId: String, product: Product) {
        val cartItemBody = if (branchResponse != null) {
            // ispu
            CartItemBody.create(cartId, product, branchResponse!!)
        } else{
            // normal
            CartItemBody.create(cartId, product.sku, options)
        }
        HttpManagerMagento.getInstance(context).addProductToCart(cartId, cartItemBody,
                object : ApiResponseCallback<CartItem> {
                    override fun success(response: CartItem?) {
                        response?.let { saveCartItem(context, it, product, branchResponse) }
                    }

                    override fun failure(error: APIError) {
                        if (error.errorCode == APIError.INTERNAL_SERVER_ERROR.toString()) {
                            callback?.forceClearCart()
                        } else {
                            callback?.onFailure(error.errorMessage)
                        }
                    }
                })
    }

    private fun saveCartItem(context: Context, cartItem: CartItem, product: Product,
                             branchResponse: BranchResponse? = null) {
        // create cacheCartItem
        val cacheCartItem = if (branchResponse != null) {
            CacheCartItem.asCartItem(cartItem, product, branchResponse)
        } else {
            CacheCartItem.asCartItem(cartItem, product)
        }

        RealmController.getInstance().saveCartItem(cacheCartItem,
                object : DatabaseListener {
                    override fun onSuccessfully() {
                        Toast.makeText(context, context.getString(R.string.added_to_cart),
                                Toast.LENGTH_SHORT).show()
                        callback?.onSuccessfully()
                    }

                    override fun onFailure(error: Throwable) {
                        error.message?.let { callback?.onFailure(it) }
                    }
                })
    }
}

interface AddProductToCartCallback {
    fun onSuccessfully()
    fun forceClearCart()
    fun onFailure(messageError: String)
    fun onFailure(dialog: Dialog)
}