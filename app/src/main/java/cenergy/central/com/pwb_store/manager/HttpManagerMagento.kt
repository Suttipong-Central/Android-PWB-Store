package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.service.CartService
import cenergy.central.com.pwb_store.manager.service.CategoryService
import cenergy.central.com.pwb_store.manager.service.ProductService
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.BrandResponse
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class HttpManagerMagento {
    private val mContext: Context = Contextor.getInstance().context
    private var retrofit: Retrofit
    private var defaultHttpClient: OkHttpClient

    init {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
        defaultHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                            .build()
                    chain.proceed(request)
                }
                .addInterceptor(interceptor)
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MAGENTO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultHttpClient)
                .build()
    }

    companion object {
        //Specific Header
        private const val HEADER_AUTHORIZATION = "Authorization"

        @SuppressLint("StaticFieldLeak")
        private var instance: HttpManagerMagento? = null

        fun getInstance(): HttpManagerMagento {
            if (instance == null)
                instance = HttpManagerMagento()
            return instance as HttpManagerMagento
        }
    }

    fun retrieveCategories(force: Boolean, callback: ApiResponseCallback<Category?>) {
        // If already cached then do
        val endpointName = "/rest/V1/categories"
        val database = RealmController.with(mContext)
        if (!force && database.hasFreshlyCachedEndpoint(endpointName)) {
            Log.i("PBE", "retrieveCategories: using cached")
            callback.success(database.category)
            return
        }

        Log.i("PBE", "retrieveCategories: calling endpoint")
        val categoryService = retrofit.create(CategoryService::class.java)
        categoryService.categories.enqueue(object : Callback<Category> {

            override fun onResponse(call: Call<Category>?, response: Response<Category>?) {
                if (response != null) {
                    //TODO: keep just position less than 15
                    val category = response.body()
                    val categoryHeader = category?.filterHeaders
                    if (categoryHeader != null) {
                        val toRemove = arrayListOf<ProductFilterHeader>()
                        for (header in categoryHeader) {
                            if (header.position > 15) {
                                toRemove.add(header)
                            }
                        }
                        category.filterHeaders.removeAll(toRemove)
                    }

                    // Store to database
                    database.saveCategory(category)

                    // Update cached endpoint
                    database.updateCachedEndpoint(endpointName)

                    callback.success(category)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Category>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun retrieveCategories(force: Boolean, categoryId: Int, categoryLevel: Int, callback: ApiResponseCallback<Category?>) {
        // If already cached then do
        val endpointName = "/rest/V1/headless/categories?categoryId=$categoryId&categoryLevel$categoryLevel"
        val database = RealmController.with(mContext)
        if (!force && database.hasFreshlyCachedEndpoint(endpointName)) {
            Log.i("PBE", "retrieveCategories: using cached")
            callback.success(database.category)
            return
        }

        Log.i("PBE", "retrieveCategories: calling endpoint")
        val categoryService = retrofit.create(CategoryService::class.java)
        categoryService.getCategories(categoryId, categoryLevel).enqueue(object : Callback<Category> {

            override fun onResponse(call: Call<Category>?, response: Response<Category>?) {
                if (response != null) {
                    val category = response.body()
                    val categoryHeaders = category?.filterHeaders
                    if (category != null && category.IsIncludeInMenu() && categoryHeaders != null) {
                        val toRemove = arrayListOf<ProductFilterHeader>()
                        for (header in categoryHeaders) {
                            if (!header.IsIncludeInMenu()) {
                                toRemove.add(header)
                            }
                        }
                        category.filterHeaders.removeAll(toRemove)
                    }

                    // Store to database
                    database.saveCategory(category)

                    // Update cached endpoint
                    database.updateCachedEndpoint(endpointName)

                    callback.success(category)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Category>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun retrieveProductList(category: String, categoryId: String, conditionType: String,
                            pageSize: Int, currentPage: Int, typeSearch: String, fields: String,
                            callback: ApiResponseCallback<ProductResponse?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductList(category, categoryId, conditionType, pageSize,
                currentPage, typeSearch, fields).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                if (response != null) {
                    val product = response.body()
                    callback.success(product)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun retrieveProductList(categoryId: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductList(categoryId, "status", 1, "eq", pageSize, currentPage).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                if (response != null) {
                    val product = response.body()
                    callback.success(product)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                callback.failure(APIError(t))

            }
        })
    }

    fun retrieveProductsFilterByBrand(categoryId: String, brandId: Long, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductsFilterByBrand(categoryId, "status", 1, "eq", "brand", brandId, "eq", pageSize, currentPage).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                if (response != null) {
                    val product = response.body()
                    callback.success(product)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getBrands(categoryId: String, callback: ApiResponseCallback<BrandResponse>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getBrands(categoryId).enqueue(object : Callback<BrandResponse> {
            override fun onResponse(call: Call<BrandResponse>?, response: Response<BrandResponse>?) {
                if (response != null) {
                    val product = response.body()
                    callback.success(product)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<BrandResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun retrieveProductDetail(sku: String, callback: ApiResponseCallback<Product?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductDetail(sku)
                .enqueue(object : Callback<Product> {
                    override fun onResponse(call: Call<Product>?, response: Response<Product>?) {
                        if (response != null) {
                            val productDetailNew = response.body()
                            callback.success(productDetailNew)
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<Product>?, t: Throwable?) {
                        callback.failure(APIError(t))
                    }
                })
    }

    /*
    * @Hardcode for get custom_attributes
    *
    * */
    fun getProductDetail(sku: String, callback: ApiResponseCallback<Product?>) {
        val url = "staging.powerbuy.co.th"
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(url)
                .addPathSegment("rest")
                .addPathSegment("V1")
                .addPathSegment("products")
                .addPathSegment(sku)
                .addQueryParameter("searchCriteria[filterGroups][0][filters][0][field]", "status")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][0][value]", "1")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][0][conditionType]", "eq")
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()
                    val product = Product()
                    val productExtension = ProductExtension()
                    val stockItem = StockItem()

                    try {
                        val productObject = JSONObject(data?.string())
                        product.id = productObject.getInt("id")
                        product.sku = productObject.getString("sku")
                        product.name = productObject.getString("name")
                        product.price = productObject.getDouble("price")
                        product.status = productObject.getInt("status")

                        val extensionObject = productObject.getJSONObject("extension_attributes")
                        val stockObject = extensionObject.getJSONObject("stock_item")
                        stockItem.itemId = stockObject.getLong("item_id")
                        stockItem.productId = stockObject.getLong("product_id")
                        stockItem.stockId = stockObject.getLong("stock_id")
                        stockItem.qty = stockObject.getInt("qty")
                        stockItem.isInStock = stockObject.getBoolean("is_in_stock")
                        stockItem.maxQTY = stockObject.getInt("max_sale_qty")

                        productExtension.stokeItem = stockItem // add stockItem to productExtension
//                    product.extension = productExtension // add product extension to product

                        val attrArray = productObject.getJSONArray("custom_attributes")
                        for (i in 0 until attrArray.length()) {
                            val attrName = attrArray.getJSONObject(i).getString("name")
                            when (attrName) {
                                "special_price" -> {
                                    val specialPrice = attrArray.getJSONObject(i).getString("value")
                                    product.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                                }

                                "special_from_date" -> {
                                    product.specialFromDate = attrArray.getJSONObject(i).getString("value")
                                }

                                "special_to_date" -> {
                                    product.specialToDate = attrArray.getJSONObject(i).getString("value")
                                }

                                "image" -> {
                                    product.imageUrl = attrArray.getJSONObject(i).getString("value")
                                }

                                "brand" -> {
                                    product.brand = attrArray.getJSONObject(i).getString("value")
                                }

                                "description" -> {
                                    productExtension.description = attrArray.getJSONObject(i).getString("value")
                                }

                                "short_description" -> {
                                    productExtension.shortDescription = attrArray.getJSONObject(i).getString("value")
                                }
                            }
                        }
                        product.extension = productExtension // add product extension to product
                        callback.success(product)
                    }catch (e: Exception) {
                        APIErrorUtils.parseError(response)?.let { callback.failure(it) }
                        Log.e("JSON Parser", "Error parsing data " + e.toString())
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: okhttp3.Call?, e: IOException?) {
                callback.failure(APIError(e))
            }
        })
    }

    fun getProductFromBarcode(filterBarcode: String, barcode: String, eq: String, orderBy: String,
                              pageSize: Int, currentPage: Int, callback: ApiResponseCallback<Product?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductFromBarcode(filterBarcode, barcode, eq, orderBy, pageSize, currentPage)
                .enqueue(object : Callback<ProductResponse> {

                    override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                        if (response != null) {
                            val productResponse = response.body()
                            if (productResponse?.products!!.size > 0) {
                                callback.success(productResponse.products[0])
                            } else {
                                callback.success(null)
                            }
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ProductResponse>?, t: Throwable?) {
                        callback.failure(APIError(t))
                    }
                })
    }

    // region Cart
    fun getCart(callback: ApiResponseCallback<String?>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.createCart().enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response != null && response.isSuccessful) {
                    val cartId = response.body()
                    callback.success(cartId)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun addProductToCart(cartId: String, cartItemBody: CartItemBody, callback: ApiResponseCallback<CartItem>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.addProduct(cartId, cartItemBody).enqueue(object : Callback<CartItem> {
            override fun onResponse(call: Call<CartItem>?, response: Response<CartItem>?) {
                if (response != null && response.isSuccessful) {
                    val cartItem = response.body()
                    callback.success(cartItem)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<CartItem>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun viewCart(cartId: String, callback: ApiResponseCallback<List<CartItem>>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.viewCart(cartId).enqueue(object : Callback<List<CartItem>> {
            override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                if (response.isSuccessful) {
                    val cartItemList = response.body()
                    callback.success(cartItemList)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun deleteItem(cartId: String, itemId: Long, callback: ApiResponseCallback<Boolean>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.deleteItem(cartId, itemId).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (response != null && response.isSuccessful) {
                    callback.success(response.body() ?: false)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun updateItem(cartId: String, itemId: Long, qty: Int, callback: ApiResponseCallback<CartItem>) {
        val cartService = retrofit.create(CartService::class.java)
        val item = ItemBody(cartId = cartId, itemId = itemId, qty = qty)
        val updateItemBody = UpdateItemBody(cartItem = item)
        cartService.updateItem(cartId, itemId, updateItemBody).enqueue(object : Callback<CartItem> {
            override fun onResponse(call: Call<CartItem>?, response: Response<CartItem>?) {
                if (response != null && response.isSuccessful) {
                    val cartItem = response.body()
                    callback.success(cartItem)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<CartItem>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun createShippingInformation(cartId: String, shippingAddress: AddressInformation, billingAddress: AddressInformation
                                  , callback: ApiResponseCallback<ShippingInformationResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        val addressInformationBody = AddressInformationBody(shippingAddress, billingAddress, "storepickup", "storepickup")
        val shippingBody = ShippingBody(addressInformationBody)
        cartService.createShippingInformation(cartId, shippingBody).enqueue(object : Callback<ShippingInformationResponse> {
            override fun onResponse(call: Call<ShippingInformationResponse>?, response: Response<ShippingInformationResponse>?) {
                if (response != null && response.isSuccessful) {
                    val shippingInformation = response.body()
                    callback.success(shippingInformation)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ShippingInformationResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun updateOder(cartId: String, callback: ApiResponseCallback<String>) {
        val cartService = retrofit.create(CartService::class.java)
        val method = MethodBody("payatstore")
        val paymentMethodBody = PaymentMethodBody(method)
        cartService.updateOrder(cartId, paymentMethodBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response != null && response.isSuccessful) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getOrder(orderId: String, callback: ApiResponseCallback<OrderResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.getOrder(orderId).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>?, response: Response<OrderResponse>?) {
                if (response != null && response.isSuccessful) {
                    val orderResponse = response.body()
                    callback.success(orderResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<OrderResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }
    // endregion
}
