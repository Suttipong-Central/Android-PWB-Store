package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.service.CartService
import cenergy.central.com.pwb_store.manager.service.CategoryService
import cenergy.central.com.pwb_store.manager.service.ProductService
import cenergy.central.com.pwb_store.manager.service.UserService
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.*
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


class HttpManagerMagento(context: Context) {

    private var retrofit: Retrofit
    private var defaultHttpClient: OkHttpClient
    private var database = RealmController.getInstance()
    private val preferenceManager by lazy { cenergy.central.com.pwb_store.manager.preferences.PreferenceManager(context) }

    private lateinit var userToken: String

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

        fun getInstance(context: Context): HttpManagerMagento {
            if (instance == null)
                instance = HttpManagerMagento(context)
            return instance as HttpManagerMagento
        }
    }

    // region user
    fun setUserToken(token: String) {
        this.userToken = token
    }

    fun getUserToken(): String = this.userToken

    fun userLogin(username: String, password: String, callback: ApiResponseCallback<UserResponse?>) {
        val userService = retrofit.create(UserService::class.java)
        val userBody = UserBody(username = username, password = password)
        userService.userLogin(userBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>?) {
                if (response != null && response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        setUserToken(loginResponse)
                        // get user information
                        getUserDetail(callback)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getUserDetail(callback: ApiResponseCallback<UserResponse?>) {
        val userService = retrofit.create(UserService::class.java)
        userService.retrieveUser(userToken).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                if (response != null && response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        // save user token
                        database.saveUserToken(UserToken(token = userToken))
                        // save user information
                        val userInformation = UserInformation(userId = userResponse.user.userId,
                                user = userResponse.user, store = userResponse.store)
                        database.saveUserInformation(userInformation)
                        callback.success(userResponse)
                    } else {
                        callback.success(null)
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }
    // endregion

    fun retrieveCategories(force: Boolean, callback: ApiResponseCallback<Category?>) {
        // If already cached then do
        val endpointName = "/rest/V1/categories"
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
        when (getLanguage()) {
            AppLanguage.EN.key -> {
                val endpointName = "/th/rest/V1/headless/categories?categoryId=$categoryId&categoryLevel$categoryLevel"
                database.clearCachedEndpoint(endpointName)

            }
            AppLanguage.TH.key -> {
                val endpointName = "/en/rest/V1/headless/categories?categoryId=$categoryId&categoryLevel$categoryLevel"
                database.clearCachedEndpoint(endpointName)

            }
        }
        val endpointName = "/${getLanguage()}/rest/V1/headless/categories?categoryId=$categoryId&categoryLevel$categoryLevel"
        if (!force && database.hasFreshlyCachedEndpoint(endpointName)) {
            Log.i("PBE", "retrieveCategories: using cached ${getLanguage()}")
            callback.success(database.category)
            return
        }

        Log.i("PBE", "retrieveCategories: calling endpoint ${getLanguage()}")
        val categoryService = retrofit.create(CategoryService::class.java)
        categoryService.getCategories(getLanguage(),categoryId, categoryLevel).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>?, response: Response<Category>?) {
                if (response != null) {
                    if(response.isSuccessful){
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

                        if (category != null) {
                            // Store to database
                            database.saveCategory(category)

                            // Update cached endpoint
                            database.updateCachedEndpoint(endpointName)
                        }

                        callback.success(category)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Category>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }

    fun retrieveProducts(category: String, categoryId: String, conditionType: String,
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

    fun retrieveProducts(categoryId: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductList(getLanguage(), categoryId, "status", 1, "eq", pageSize, currentPage).enqueue(object : Callback<ProductResponse> {
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

    fun retrieveProducts(categoryId: String, pageSize: Int, currentPage: Int, orderName: String, orderDir: String, callback: ApiResponseCallback<ProductResponse?>) {

        if (orderName == "" && orderDir == "") {
            retrieveProducts(categoryId, pageSize, currentPage, callback)
            return
        } else {
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductList(getLanguage(), categoryId, "status", 1, "eq", pageSize, currentPage, orderName, orderDir).enqueue(object : Callback<ProductResponse> {
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
    }

    fun retrieveProductsFilterByBrand(categoryId: String, brandId: Long, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductsFilterByBrand(getLanguage(), categoryId, "status", 1, "eq", "brand", brandId, "eq", pageSize, currentPage).enqueue(object : Callback<ProductResponse> {
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

    fun retrieveProductsFilterByBrand(categoryId: String, brandId: Long, pageSize: Int, currentPage: Int, orderName: String, orderDir: String, callback: ApiResponseCallback<ProductResponse?>) {
        if (orderName == "" && orderDir == "") {
            retrieveProductsFilterByBrand(categoryId, brandId, pageSize, currentPage, callback)
        } else {
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductsFilterByBrand(getLanguage(), categoryId, "status", 1, "eq", "brand", brandId, "eq", pageSize, currentPage, orderName, orderDir).enqueue(object : Callback<ProductResponse> {
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
    }

    // TODO: Delete when get new api this case so hard for filter
    fun getProductFromSearch(keyword: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse>) {
//        val productService = retrofit.create(ProductService::class.java)
//        productService.getProductFromSearch("name", "%$keyword%", "like",
//                "sku", keyword, "eq", "brand", "%$keyword%", "like", pageSize, currentPage,
//                "items[id,sku,name,price,status],total_count").enqueue(object : Callback<ProductResponse> {
//            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>?) {
//                if (response != null) {
//                    callback.success(response.body())
//                } else {
//                    callback.failure(APIErrorUtils.parseError(response))
//                }
//            }
//
//            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
//                callback.failure(APIError(t))
//            }
//        })
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegment("rest")
                .addPathSegment("V1")
                .addPathSegment("products")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][1][field]", "name")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][1][value]", "%$keyword%")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][1][conditionType]", "like")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][2][field]", "sku")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][2][value]", keyword)
                .addQueryParameter("searchCriteria[filterGroups][0][filters][2][conditionType]", "eq")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][3][field]", "brand")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][3][value]", "%$keyword%")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][3][conditionType]", "like")
                .addQueryParameter("searchCriteria[filterGroups][0][filters][3][value]", "%$keyword%")
                .addQueryParameter("searchCriteria[pageSize]", pageSize.toString())
                .addQueryParameter("searchCriteria[currentPage]", currentPage.toString())
                .addQueryParameter("fields", "items[id,sku,name,price,status,custom_attributes],total_count")
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .build()
        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback{

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()

                    try {
                        val productResponse = ProductResponse()
                        val productResponseObject = JSONObject(data?.string())
                        val totalCount = productResponseObject.getInt("total_count")
                        val productObjList = productResponseObject.getJSONArray("items")

                        val products = arrayListOf<Product>()
                        if (productObjList != null && productObjList.length() > 0) {
                            for (i in 0 until productObjList.length()) {
                                val product = Product()
                                val productObject = productObjList.getJSONObject(i)
                                product.id = productObject.getInt("id")
                                product.sku = productObject.getString("sku")
                                product.name = productObject.getString("name")
                                product.price = productObject.getDouble("price")
                                product.status = productObject.getInt("status")

                                val attrArray = productObject.getJSONArray("custom_attributes")
                                for (j in 0 until attrArray.length()) {
                                    val attrName = attrArray.getJSONObject(j).getString("name")
                                    when (attrName) {
                                        "special_price" -> {
                                            val specialPrice = attrArray.getJSONObject(j).getString("value")
                                            product.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                                        }

                                        "special_from_date" -> {
                                            product.specialFromDate = attrArray.getJSONObject(j).getString("value")
                                        }

                                        "special_to_date" -> {
                                            product.specialToDate = attrArray.getJSONObject(j).getString("value")
                                        }

                                        "image" -> {
                                            product.image = attrArray.getJSONObject(j).getString("value")
                                        }
//
//                                        "delivery_method" -> {
//                                            product.deliveryMethod = attrArray.getJSONObject(j).getString("value")
//                                        }
//
//                                        "brand" -> {
//                                            product.brand = attrArray.getJSONObject(j).getString("value")
//                                        }
                                    }
                                }
                                products.add(product)
                            }
                        }

                        productResponse.products = products
                        productResponse.totalCount = totalCount

                        callback.success(productResponse)
                    } catch (e: Exception) {
                        callback.failure(APIError(e))
                        Log.e("JSON Parser", "Error parsing data " + e.toString())
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.failure(APIError(e))
            }
        })
    }

    fun getProductFromSearchNewAPI(keyword: String, pageSize: Int, currentPage: Int, orderName: String, orderDir: String, callback: ApiResponseCallback<ProductResponse?>){

        if (orderName == "" && orderDir == "") {
            getProductFromSearchNewAPI(keyword, pageSize, currentPage, callback)
        } else {
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductSearch(getLanguage(), keyword, pageSize, currentPage, orderName, orderDir).enqueue(object : Callback<ProductSearchResponse> {
                override fun onResponse(call: Call<ProductSearchResponse>?, response: Response<ProductSearchResponse>?) {
                    if (response != null) {
                        val productSearchResponse = response.body()
                        if (productSearchResponse != null) {
                            callback.success(ProductResponse.asProductResponse(keyword, productSearchResponse))
                        } else {
                            callback.failure(APIErrorUtils.setErrorMessage("ProductSearchResponse is null"))
                        }
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                    callback.failure(APIError(t))
                }
            })
        }
    }

    fun getProductFromSearchNewAPI(keyword: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>){
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductSearch(getLanguage(), keyword, pageSize, currentPage).enqueue(object : Callback<ProductSearchResponse>{
            override fun onResponse(call: Call<ProductSearchResponse>?, response: Response<ProductSearchResponse>?) {
                if (response != null){
                    val productSearchResponse = response.body()
                    if(productSearchResponse != null){
                        callback.success(ProductResponse.asProductResponse(keyword, productSearchResponse))
                    } else {
                        callback.failure(APIErrorUtils.setErrorMessage("ProductSearchResponse is null"))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getBrands(categoryId: String, callback: ApiResponseCallback<List<Brand>>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getBrands(categoryId).enqueue(object : Callback<BrandResponse> {
            override fun onResponse(call: Call<BrandResponse>?, response: Response<BrandResponse>?) {
                if (response != null) {
                    val brandResponse = response.body()
                    val brands = brandResponse?.items ?: arrayListOf()
                    for (brand in brands) {
                        database.saveBands(brand)
                    }

                    if (brands.isNotEmpty()) {
                        brands.sortedBy { it.name }
                    }

                    callback.success(brands)
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
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegment(getLanguage())
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
                    val images = arrayListOf<ProductGallery>()

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
                        val galleryArray = productObject.getJSONArray("media_gallery_entries")
                        for (i in 0 until galleryArray.length()) {
                            val id = galleryArray.getJSONObject(i).getString("id")
                            val type = galleryArray.getJSONObject(i).getString("media_type")
                            val label = galleryArray.getJSONObject(i).getString("label")
                            val position = galleryArray.getJSONObject(i).getInt("position")
                            val disabled = galleryArray.getJSONObject(i).getBoolean("disabled")
                            val file = galleryArray.getJSONObject(i).getString("file")
                            images.add(ProductGallery(id, type, label, position, disabled, file))
                        }
                        product.gallery = images

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
                                    product.image = attrArray.getJSONObject(i).getString("value")
                                }

                                "delivery_method" -> {
                                    product.deliveryMethod = attrArray.getJSONObject(i).getString("value")
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

                                "barcode" -> {
                                    productExtension.barcode = attrArray.getJSONObject(i).getString("value")
                                }
                            }
                        }
                        product.extension = productExtension // add product extension to product
                        callback.success(product)
                    } catch (e: Exception) {
                        callback.failure(APIError(e))
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

    fun getProductFromBarcode(filterBarcode: String, barcode: String, eq: String, orderBy: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<Product?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductFromBarcode(getLanguage(), filterBarcode, barcode, eq, orderBy, pageSize, currentPage)
                .enqueue(object : Callback<ProductByBarcodeResponse> {
                    override fun onResponse(call: Call<ProductByBarcodeResponse>?, response: Response<ProductByBarcodeResponse>?) {
                        if (response != null) {
                            val productResponse = response.body()
                            if (productResponse != null && productResponse.products.size > 0) {
                                getProductDetail(productResponse.products[0].sku, callback)
                            } else {
                                callback.success(null)
                            }
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ProductByBarcodeResponse>?, t: Throwable?) {
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
        cartService.addProduct(getLanguage(),cartId, cartItemBody).enqueue(object : Callback<CartItem> {
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
        cartService.viewCart(getLanguage(), cartId).enqueue(object : Callback<List<CartItem>> {
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
        cartService.updateItem(getLanguage() ,cartId, itemId, updateItemBody).enqueue(object : Callback<CartItem> {
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

    fun getOrderDeliveryOptions(cartId: String, shippingAddress: AddressInformation,
                                callback: ApiResponseCallback<List<DeliveryOption>>) {
        val cartService = retrofit.create(CartService::class.java)
        val deliveryBody = DeliveryOptionsBody(shippingAddress)
        cartService.getOrderDeliveryOptions(getLanguage(), cartId, deliveryBody).enqueue(object : Callback<List<DeliveryOption>> {
            override fun onResponse(call: Call<List<DeliveryOption>>, response: Response<List<DeliveryOption>>?) {
                if (response != null && response.isSuccessful) {
                    val shippingInformation = response.body()
                    callback.success(shippingInformation)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<DeliveryOption>>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun createShippingInformation(cartId: String, shippingAddress: AddressInformation, billingAddress: AddressInformation,
                                  subscribeCheckOut: SubscribeCheckOut, deliveryOption: DeliveryOption, callback: ApiResponseCallback<ShippingInformationResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        val addressInformationBody = AddressInformationBody(shippingAddress, billingAddress, deliveryOption.methodCode,
                deliveryOption.carrierCode, subscribeCheckOut)
        val shippingBody = ShippingBody(addressInformationBody)
        cartService.createShippingInformation(getLanguage(), cartId, shippingBody).enqueue(object : Callback<ShippingInformationResponse> {
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

    fun updateOder(cartId: String, email: String, staffId: String, storeId: String, callback: ApiResponseCallback<String>) {
        val cartService = retrofit.create(CartService::class.java)
        val method = MethodBody("payatstore") // will change soon
        val paymentMethodBody = PaymentInformationBody(cartId, method, email, staffId, storeId)
        cartService.updateOrder(getLanguage(), cartId, paymentMethodBody).enqueue(object : Callback<String> {
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
        cartService.getOrder(getLanguage(), orderId).enqueue(object : Callback<OrderResponse> {
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

    // region store
    fun getBranches(pageSize: Int, currentPage: Int, callback: ApiResponseCallback<BranchResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.getBranches(getLanguage(), "storepickup_id", "ASC", pageSize, currentPage).enqueue(object : Callback<BranchResponse> {
            override fun onResponse(call: Call<BranchResponse>, response: Response<BranchResponse>?) {
                if (response != null && response.isSuccessful) {
                    val branchResponse = response.body()
                    callback.success(branchResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<BranchResponse>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getBranches(callback: ApiResponseCallback<BranchResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.getBranches(getLanguage(),"storepickup_id", "ASC", 13, 1).enqueue(object : Callback<BranchResponse> {
            override fun onResponse(call: Call<BranchResponse>, response: Response<BranchResponse>?) {
                if (response != null && response.isSuccessful) {
                    val branchResponse = response.body()
                    callback.success(branchResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<BranchResponse>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }
    // endregion

    // region get PWB Customer
    fun getPWBCustomer(telephone: String, callback: ApiResponseCallback<List<PwbMember>>) {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegment(getLanguage())
                .addPathSegment(POWERBUY.MEMBER.PATH_REST)
                .addPathSegment(POWERBUY.MEMBER.PATH_V1)
                .addPathSegment(POWERBUY.MEMBER.PATH_HEADLESS)
                .addPathSegment(POWERBUY.MEMBER.PATH_CUSTOMERS)
                .addPathSegment(telephone)
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()

                    try {
                        val dataObject = JSONObject(data?.string())
                        val items = dataObject.getJSONArray("items")
                        val memberList: ArrayList<PwbMember> = arrayListOf()

                        if (items.length() < 1) {
                            callback.success(arrayListOf())
                            return
                        }

                        for (i in 0 until items.length()) {
                            val memberDetail = items.getJSONObject(i)
                            val id = if (memberDetail.has(POWERBUY.MEMBER.ID)) memberDetail.getLong(POWERBUY.MEMBER.ID) else 0L
                            val firstname = if (memberDetail.has(POWERBUY.MEMBER.FIRST_NAME)) {
                                memberDetail.getString(POWERBUY.MEMBER.FIRST_NAME) ?: ""
                            } else ""
                            val lastname = if (memberDetail.has(POWERBUY.MEMBER.LAST_NAME)) {
                                memberDetail.getString(POWERBUY.MEMBER.LAST_NAME) ?: ""
                            } else ""
                            val email = if (memberDetail.has(POWERBUY.MEMBER.EMAIL)) {
                                memberDetail.getString(POWERBUY.MEMBER.EMAIL) ?: ""
                            } else ""
                            val t1cNo = if (memberDetail.has(POWERBUY.MEMBER.THE_1_CARD_NUMNER)) {
                                memberDetail.getString(POWERBUY.MEMBER.THE_1_CARD_NUMNER) ?: ""
                            } else ""

                            val memberAddressList: ArrayList<MemberAddress> = arrayListOf()
                            if (memberDetail.has(POWERBUY.MEMBER.ADDRESSES)) {
                                val addresses = memberDetail.getJSONArray(POWERBUY.MEMBER.ADDRESSES)
                                for (k in 0 until addresses.length()) {
                                    val addressDetail = addresses.getJSONObject(k)
                                    val memberAddress = MemberAddress()
                                    if (addressDetail.has("id")) {
                                        memberAddress.id = addressDetail.getLong("id")
                                    }

                                    if (addressDetail.has("customer_id")) {
                                        memberAddress.customerId = addressDetail.getLong("customer_id")
                                    }

                                    if (addressDetail.has("region_id")) {
                                        memberAddress.regionId = addressDetail.getInt("region_id")
                                    }

                                    if (addressDetail.has("country_id")) {
                                        memberAddress.countryId = addressDetail.getString("country_id")
                                    }

                                    if (addressDetail.has("street")) {
                                        val streets = arrayListOf<String>()
                                        val streetArrayObject = addressDetail.getJSONArray("street")
                                        for (j in 0 until streetArrayObject.length()) {
                                            streets.add(streetArrayObject.getString(j))
                                        }
                                        memberAddress.street = streets
                                    }

                                    if (addressDetail.has("telephone")) {
                                        memberAddress.telephone = addressDetail.getString("telephone")
                                    }

                                    if (addressDetail.has("postcode")) {
                                        memberAddress.postcode = addressDetail.getString("postcode")
                                    }

                                    if (addressDetail.has("city")) {
                                        memberAddress.city = addressDetail.getString("city")
                                    }

                                    if (addressDetail.has("firstname")) {
                                        memberAddress.firstname = addressDetail.getString("firstname")
                                    }

                                    if (addressDetail.has("lastname")) {
                                        memberAddress.lastname = addressDetail.getString("lastname")
                                    }

                                    if (addressDetail.has("default_shipping")) {
                                        memberAddress.defaultShipping = addressDetail.getBoolean("default_shipping")
                                    }

                                    if (addressDetail.has("default_billing")) {
                                        memberAddress.defaultShipping = addressDetail.getBoolean("default_billing")
                                    }

                                    if (addressDetail.has("custom_attributes")) {
                                        val memberSubAddress = MemberSubAddress()
                                        val customAttributes = addressDetail.getJSONArray("custom_attributes")
                                        for (m in 0 until customAttributes.length()) {
                                            val ctmAttr = customAttributes.getJSONObject(m)
                                            val attrName = ctmAttr.getString("name")

                                            when (attrName) {
                                                "house_no" -> {
                                                    val houseNo = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.houseNo = houseNo
                                                }

                                                "district" -> {
                                                    val district = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.district = district
                                                }

                                                "district_id" -> {
                                                    val districtId = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.districtId = districtId
                                                }

                                                "subdistrict" -> {
                                                    val subdistrict = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.subDistrict = subdistrict
                                                }

                                                "subdistrict_id" -> {
                                                    val subDistrictId = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.subDistrictId = subDistrictId
                                                }

                                                "postcode_id" -> {
                                                    val postcodeId = ctmAttr.getString("value")
                                                            ?: ""
                                                    memberSubAddress.postcodeId = postcodeId
                                                }
                                            }
                                        }
                                        memberAddress.subAddress = memberSubAddress
                                    }
                                    memberAddressList.add(memberAddress)
                                }
                            }

                            memberList.add(PwbMember(id, firstname, lastname, email, t1cNo, memberAddressList))
                        }

                        callback.success(memberList)
                    } catch (e: Exception) {
                        callback.failure(APIError(e))
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
    // endregion

    private fun getLanguage(): String = preferenceManager.getDefaultLanguage()
}
