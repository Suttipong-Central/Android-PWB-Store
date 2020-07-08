package cenergy.central.com.pwb_store.utils

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.helpers.DialogHelper
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.manager.api.ProductListAPI
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import io.realm.RealmList
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

interface AddProductToCartCallback {
    fun onSuccessfully()
    fun forceClearCart()
    fun onFailure(messageError: String)
    fun onFailure(dialog: Dialog)
}

interface EditStorePickupCallback {
    fun onSuccessfully()
    fun onFailure(messageError: String)
}

interface CartListener {
    fun onCartCreated(cartId: String)
    fun onFailure(messageError: String)
}

class CartUtils(private val context: Context) {
    private val prefManager: PreferenceManager = PreferenceManager(context)
    private var callback: AddProductToCartCallback? = null
    private var branchResponse: BranchResponse? = null
    private var branchResponses: List<BranchResponse>? = null

    // data
    private val db = RealmController.getInstance()
    private var cacheCartItems = db.cacheCartItems

    fun createCart(listener: CartListener) {
        HttpManagerMagento.getInstance(context).getCart(object : ApiResponseCallback<String?> {
            override fun success(response: String?) {
                if (response != null) {
                    prefManager.setCartId(response)
                    listener.onCartCreated(response)
                } else {
                    listener.onFailure(context.getString(R.string.not_found_data))
                }
            }

            override fun failure(error: APIError) {
                listener.onFailure(error.errorMessage ?: defaultErrorMessage)
            }
        })
    }

    fun addProductToCart(product: Product, callback: AddProductToCartCallback) {
        this.callback = callback
        val cartId = prefManager.cartId
        this.branchResponse = null // force no have branch
        this.branchResponses = null
        if (cartId != null) {
            requestAddToCart(cartId, product)
        } else {
            retrieveCart(product)
        }
    }

    fun addProduct2hToCart(product: Product, branchResponse: BranchResponse,
                           branchResponses: List<BranchResponse>,
                           callback: AddProductToCartCallback) {
        this.branchResponse = branchResponse
        this.branchResponses = branchResponses
        this.callback = callback

        val cartId = prefManager.cartId
        if (cartId != null) {
            requestAddToCart(cartId, product)
        } else {
            retrieveCart(product)
        }
    }

    private fun retrieveCart(product: Product) {
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
        // is chat and shop user
        val retailerId = if (db.userInformation.user?.userLevel == 3L) {
            null // is chat and shop user retailerId must be null
        } else {
            db?.userInformation?.store?.storeId?.toInt()
        }

        // is empty cart?
        if (cacheCartItems == null || cacheCartItems.isEmpty()) {
            val cartItemBody = if (branchResponse != null) {
                CartItemBody.create(cartId, product, branchResponse!!, retailerId)  // ispu
            } else {
                CartItemBody.create(cartId, product, retailerId) // normal
            }
            requestAddToCart(cartId, product, cartItemBody)
        } else {
            // is product ispu
            if (branchResponse != null) {
                if (cacheCartItems.hasProduct2h()) {
                    val body = CartItemBody.create(cartId, product, branchResponse!!, retailerId)  // ispu
                    requestAddToCart(cartId, product, body)
                } else {
                    clearCartAndRecreateCart(product)
                }
            } else {
                // product normal
                if (cacheCartItems.hasProduct2h()) {
                    clearCartAndRecreateCart(product)
                } else {
                    val body = CartItemBody.create(cartId, product, retailerId)  // normal
                    requestAddToCart(cartId, product, body)
                }
            }
        }
    }

    private fun requestAddToCart(cartId: String, product: Product, cartItemBody: CartItemBody) {
        HttpManagerMagento.getInstance(context).addProductToCart(cartId, cartItemBody,
                object : ApiResponseCallback<CartItem> {
                    override fun success(response: CartItem?) {
                        // is product 2h
                        if (branchResponse != null) {
                            response?.let { setProduct2hShippingAddress(cartId, it, product, branchResponse!!) }
                        } else {
                            // product normal
                            response?.let { saveCartItem(it, product, branchResponse) }
                        }
                    }

                    override fun failure(error: APIError) {
                        when (error.errorCode) {
                            APIError.NOT_FOUND.toString() -> {
                                if (error.errorParameter != null && error.errorParameter.fieldName == "cartId") {
                                    callback?.forceClearCart()
                                } else {
                                    callback?.onFailure(error.errorMessage ?: defaultErrorMessage)
                                }
                            }
                            APIError.INTERNAL_SERVER_ERROR.toString() -> {
                                callback?.forceClearCart()
                            }
                            else -> callback?.onFailure(error.errorMessage ?: defaultErrorMessage)
                        }
                    }
                })
    }

    private fun setProduct2hShippingAddress(cartId: String, cartItem: CartItem, product: Product,
                                            branchResponse: BranchResponse) {
        val storeAddress = AddressInformation.createBranchAddress(branchResponse.branch)
        val storePickup = StorePickup(branchResponse.branch.storeId)
        val subscribeCheckOut = AddressInfoExtensionBody(storePickup = storePickup)

        HttpManagerMagento.getInstance(context).setProduct2hShippingInformation(cartId,
                storeAddress, subscribeCheckOut, DeliveryOption.getStorePickupIspu(),
                object : ApiResponseCallback<ShippingInformationResponse> {
                    override fun success(response: ShippingInformationResponse?) {
                        saveCartItem(cartItem, product, branchResponse)
                    }

                    override fun failure(error: APIError) {
                        callback?.onFailure(error.errorMessage ?: defaultErrorMessage)
                    }
                })
    }

    private fun saveCartItem(cartItem: CartItem, product: Product,
                             branchResponse: BranchResponse? = null) {
        // create cacheCartItem
        if (branchResponse != null) {
            val cacheCartItem = CacheCartItem.asCartItem(cartItem, product, branchResponse)
            // Save Store Pick up
            val stores = RealmList<Branch>()
            this.branchResponses?.mapTo(stores, { it.branch })
            RealmController.getInstance().saveCartItem(cacheCartItem, StorePickupList(product.sku, stores),
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
        } else {
            val cacheCartItem = CacheCartItem.asCartItem(cartItem, product)
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

    fun addCoupon(cartId: String, couponCode: String, callback: ApiResponseCallback<Boolean>) {
        HttpManagerMagento(context).cartService.addCoupon(cartId, couponCode).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.body() != null && response.body() == true) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun deleteCoupon(cartId: String, callback: ApiResponseCallback<Boolean>) {
        HttpManagerMagento(context).cartService.deleteCoupon(cartId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.body() != null && response.body() == true) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun viewCart(cartId: String, callback: ApiResponseCallback<Pair<CartResponse?, List<Product>>>) {
        HttpManagerMagento(context).cartService.viewCart(requestLanguage(context), cartId).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.body() != null && response.isSuccessful) {

                    // force load product
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        val skuList = cartResponse.items.map { it.sku }
                        val result = TextUtils.join(",", skuList)
                        val filterGroupsList = java.util.ArrayList<FilterGroups>()
                        filterGroupsList.add(FilterGroups.createFilterGroups("sku", result, "in"))

                        ProductListAPI.retrieveProducts(context, skuList.size, 1,
                                filterGroupsList, arrayListOf(),
                                object : ApiResponseCallback<ProductResponse> {
                                    override fun success(response: ProductResponse?) {
                                        val products = response?.products ?: arrayListOf()
                                        callback.success(Pair(cartResponse, products))
                                    }

                                    override fun failure(error: APIError) {
                                        callback.failure(APIErrorUtils.parseError(response))
                                    }
                                })

                    } else {
                        callback.success(Pair(cartResponse, listOf()))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun viewCartTotal(cartId: String, callback: ApiResponseCallback<PaymentCartTotal>) {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments("/rest/${requestLanguage(context)}/V1/guest-carts/$cartId/totals")
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .build()

        HttpManagerMagento(context).defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()
                    val cartTotalResponse = PaymentCartTotal()
                    try {
                        val dataObject = JSONObject(data?.string())
                        if (dataObject.has("subtotal_incl_tax")) {
                            cartTotalResponse.totalPrice = dataObject.getDouble("subtotal_incl_tax")
                        }
                        if (dataObject.has("base_discount_amount")) {
                            cartTotalResponse.discountPrice = dataObject.getDouble("base_discount_amount")
                        }
                        if (dataObject.has("items_qty")) {
                            cartTotalResponse.qty = dataObject.getInt("items_qty")
                        }
                        if (dataObject.has("coupon_code")) {
                            cartTotalResponse.couponCode = dataObject.getString("coupon_code")
                        }
                        if (dataObject.has("items")) {
                            val shoppingCartItems = ArrayList<ShoppingCartItem>()
                            val itemsArray = dataObject.getJSONArray("items")
                            for (i in 0 until itemsArray.length()) {
                                var id: Long = 0
                                if (itemsArray.getJSONObject(i).has("item_id")) {
                                    id = itemsArray.getJSONObject(i).getLong("item_id")
                                }
                                var qty = 0
                                if (itemsArray.getJSONObject(i).has("qty")) {
                                    qty = itemsArray.getJSONObject(i).getInt("qty")
                                }
                                var sku = ""
                                if (itemsArray.getJSONObject(i).has("sku")) {
                                    sku = itemsArray.getJSONObject(i).getString("sku")
                                }
                                var price = 0.0
                                if (itemsArray.getJSONObject(i).has("price_incl_tax")) {
                                    price = itemsArray.getJSONObject(i).getDouble("price_incl_tax")
                                }
                                var name = ""
                                if (itemsArray.getJSONObject(i).has("name")) {
                                    name = itemsArray.getJSONObject(i).getString("name")
                                }
                                val extension = ItemExtension()
                                if (itemsArray.getJSONObject(i).has("extension_attributes")) {
                                    val extensionObject = itemsArray.getJSONObject(i).getJSONObject("extension_attributes")
                                    if (extensionObject.has("amasty_promo")) {
                                        val freeItemImage = FreeItemImage()
                                        val amastyPromoObject = extensionObject.getJSONObject("amasty_promo")
                                        if (amastyPromoObject.has("image_alt")) {
                                            freeItemImage.alt = amastyPromoObject.getString("image_alt")
                                        }
                                        if (amastyPromoObject.has("image_src")) {
                                            freeItemImage.src = amastyPromoObject.getString("image_src")
                                        }
                                        if (amastyPromoObject.has("image_width")) {
                                            freeItemImage.width = amastyPromoObject.getString("image_width")
                                        }
                                        if (amastyPromoObject.has("image_height")) {
                                            freeItemImage.height = amastyPromoObject.getString("image_height")
                                        }
                                        extension.amastyPromo = freeItemImage
                                    }
                                }
                                var discount = 0.0
                                if (itemsArray.getJSONObject(i).has("discount_amount")) {
                                    discount = itemsArray.getJSONObject(i).getDouble("discount_amount")
                                }
                                shoppingCartItems.add(ShoppingCartItem(id, qty, sku, price, name, extension, discount))
                            }
                            cartTotalResponse.items = shoppingCartItems
                        }
                        if (dataObject.has("total_segments")) {
                            val totalSegment = arrayListOf<TotalSegment>()
                            val totalSegmentArray = dataObject.getJSONArray("total_segments")
                            for (i in 0 until totalSegmentArray.length()) {
                                val segment = TotalSegment()
                                val segmentObject = totalSegmentArray.getJSONObject(i)
                                when (segmentObject.getString("code")) {
                                    "shipping" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                    "discount" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                    "coupon" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                    "amasty_coupon_amount" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                    "t1c" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                    "credit_card_on_top" -> {
                                        segment.code = segmentObject.getString("code")
                                        segment.title = segmentObject.getString("title")
                                        segment.value = segmentObject.getString("value")
                                        totalSegment.add(segment)
                                    }
                                }
                            }
                            cartTotalResponse.totalSegment = totalSegment
                        }
                        callback.success(cartTotalResponse)
                    } catch (e: Exception) {
                        callback.failure(e.getResultError())
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
                response?.close()
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.failure(e.getResultError())
            }
        })
    }

    private fun clearCartAndRecreateCart(product: Product) {
        RealmController.getInstance().deleteAllCacheCartItem(object : DatabaseListener {
            override fun onSuccessfully() {
                prefManager.clearCartId()
                cacheCartItems = db.cacheCartItems
                retrieveCart(product)
            }

            override fun onFailure(error: Throwable) {
                // TODO: handle on create cart
                Log.d("CartUtils", error.message ?: defaultErrorMessage)
                callback?.onFailure(error.localizedMessage ?: defaultErrorMessage)
            }
        })
    }

    private fun List<CacheCartItem>.hasProduct2h(): Boolean {
        if (cacheCartItems == null || cacheCartItems.isEmpty()) {
            return false
        }

        val item = cacheCartItems.find { it.branch != null }
        return item != null
    }

    fun editStore2hPickup(branchResponse: BranchResponse, callback: EditStorePickupCallback) {
        val storeAddress = AddressInformation.createBranchAddress(branchResponse.branch)
        val storePickup = StorePickup(branchResponse.branch.storeId)
        val subscribeCheckOut = AddressInfoExtensionBody(storePickup = storePickup)
        val cartId = prefManager.cartId
        if (cartId != null) {
            HttpManagerMagento.getInstance(context).setProduct2hShippingInformation(cartId,
                    storeAddress, subscribeCheckOut, DeliveryOption.getStorePickupIspu(),
                    object : ApiResponseCallback<ShippingInformationResponse> {
                        override fun success(response: ShippingInformationResponse?) {
                            editStore2hPickupInCartItem(branchResponse, callback)
                        }

                        override fun failure(error: APIError) {
                            callback.onFailure(error.errorMessage ?: defaultErrorMessage)
                        }
                    })
        } else {
            callback.onFailure(context.getString(R.string.not_found_data))
        }
    }

    private fun editStore2hPickupInCartItem(branchResponse: BranchResponse,
                                            callback: EditStorePickupCallback) {
        RealmController.getInstance().editBranchInCartItem(branchResponse.branch,
                object : DatabaseListener {
                    override fun onSuccessfully() {
                        callback.onSuccessfully()
                    }

                    override fun onFailure(error: Throwable) {
                        callback.onFailure(error.localizedMessage ?: defaultErrorMessage)
                    }

                })
    }

    private val defaultErrorMessage: String = context.getString(R.string.some_thing_wrong)

    /**
     * param language
     * in PWB is th
     * in CDS is cds_th
     * */
    private fun requestLanguage(context: Context): String {
        val language = PreferenceManager(context).getDefaultLanguage()
        return when (BuildConfig.FLAVOR) {
            "cds" -> "cds_$language"
            else -> language
        }
    }

}