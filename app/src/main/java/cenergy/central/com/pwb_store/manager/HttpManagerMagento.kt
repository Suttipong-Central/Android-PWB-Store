package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.CategoryUtils
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.extensions.asPostcode
import cenergy.central.com.pwb_store.extensions.modifyToCdsType
import cenergy.central.com.pwb_store.manager.api.ProductDetailApi
import cenergy.central.com.pwb_store.manager.api.PwbMemberApi
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.service.*
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.*
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class HttpManagerMagento(context: Context, isSerializeNull: Boolean = false) {

    private var retrofit: Retrofit
    var defaultHttpClient: OkHttpClient
    private var database = RealmController.getInstance()
    private val preferenceManager by lazy { cenergy.central.com.pwb_store.manager.preferences.PreferenceManager(context) }

    var cartService: CartService
    var hdlService: HDLService

    private lateinit var userToken: String

    init {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
        defaultHttpClient = OkHttpClient.Builder().apply {
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)
                }
                .addInterceptor(interceptor)
                .build()

        val gson = if (isSerializeNull) GsonConverterFactory.create(GsonBuilder().serializeNulls().create()) else GsonConverterFactory.create()
        retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_MAGENTO)
                .addConverterFactory(gson)
                .client(defaultHttpClient)
                .build()

        cartService = retrofit.create(CartService::class.java)
        hdlService = retrofit.create(HDLService::class.java)
    }

    companion object {
        //Specific Header
        const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER = "Bearer"
        const val OPEN_ORDER_CREATED_PAGE = "OpenOrderCreatedPage"

        @SuppressLint("StaticFieldLeak")
        private var instance: HttpManagerMagento? = null

        fun getInstance(context: Context, isSerializeNull: Boolean): HttpManagerMagento {
            if (instance == null)
                instance = HttpManagerMagento(context, isSerializeNull)
            return instance as HttpManagerMagento
        }

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

    fun userLogin(username: String, password: String, callback: ApiResponseCallback<UserInformation>) {
        val userService = retrofit.create(UserService::class.java)
        val userBody = UserBody(username = username, password = password)
        userService.userLogin(userBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>?) {
                if (response != null && response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        setUserToken(loginResponse)
                        // get user information
                        getUserId(callback)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun getUserId(callback: ApiResponseCallback<UserInformation>) {
        val userService = retrofit.create(UserService::class.java)
        userService.retrieveUserId("$BEARER $userToken").enqueue(object : Callback<LoginUserResponse> {
            override fun onResponse(call: Call<LoginUserResponse>, response: Response<LoginUserResponse>?) {
                if (response?.body() != null) {
                    getBranchUser(response.body()!!, callback)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    private fun getBranchUser(userResponse: LoginUserResponse, callback: ApiResponseCallback<UserInformation>) {
        val userService = retrofit.create(UserService::class.java)
        userService.retrieveStoreUser("$BEARER $userToken").enqueue(object : Callback<UserBranch> {
            override fun onResponse(call: Call<UserBranch>, response: Response<UserBranch>?) {
                if (response?.body() != null) {
                    val userBranch = response.body()

                    //TODO: Mock up data will delete soon
                    val user = User(userResponse.userId, "", userResponse.staffId, 223L,
                            "chuan@central.tech", "", "", 0, "")

                    if (userBranch != null && userBranch.items.size > 0) {
                        val sellerCode = userBranch.items[0].code
                        if (BuildConfig.FLAVOR == "cds"){
                            val store = Store()
                            store.retailerId = sellerCode
                            // save user token
                            database.saveUserToken(UserToken(token = userToken))
                            // save user information
                            val userInformation = UserInformation(userId = user.userId, user = user, store = store)
                            database.saveUserInformation(userInformation)
                            callback.success(userInformation)
                        } else {
                            getStoreLocation(user, sellerCode, callback)
                        }
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<UserBranch>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    private fun getStoreLocation(user: User, sellerCode: String, callback: ApiResponseCallback<UserInformation>) {
        val userService = retrofit.create(UserService::class.java)
        userService.retrieveStoreLocation(getLanguage(), sellerCode, "seller_code").enqueue(object : Callback<StoreLocationResponse> {
            override fun onResponse(call: Call<StoreLocationResponse>, response: Response<StoreLocationResponse>?) {
                if (response?.body() != null) {
                    val storeLocation = response.body()?.items?.firstOrNull()
                    val store = Store()
                    store.retailerId = sellerCode
                    if (storeLocation != null) {
                        store.storeId = storeLocation.id.toLong()
                        store.storeName = storeLocation.name
                        store.province = storeLocation.extension?.address?.region ?: ""
                        store.district = storeLocation.extension?.address?.city ?: ""
                        store.postalCode = storeLocation.extension?.address?.postcode ?: ""
                    }

                    // save user token
                    database.saveUserToken(UserToken(token = userToken))
                    // save user information
                    val userInformation = UserInformation(userId = user.userId, user = user, store = store)
                    database.saveUserInformation(userInformation)
                    callback.success(userInformation)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<StoreLocationResponse>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
    // endregion

    /**
     * @param parentId is categoryId
     *
     * Endpoint
     * @query include_in_menu
     * @query parent_id
     * */
    fun retrieveCategory(categoryId: String, includeInMenu: Boolean,
                         specialIds: ArrayList<String> = arrayListOf(),
                         callback: ApiResponseCallback<List<Category>>) {

        val httpUrl = if (includeInMenu) {
            HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.PWB_HOST_NAME)
                    .addPathSegments("rest/${getLanguage()}/V1/categories/list")
                    .addQueryParameter("searchCriteria[filterGroups][0][filters][0][field]", "include_in_menu")
                    .addQueryParameter("searchCriteria[filterGroups][0][filters][0][value]", "1")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][field]", "parent_id")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][value]", categoryId)
                    .addQueryParameter("searchCriteria[filterGroups][2][filters][0][field]", "is_active")
                    .addQueryParameter("searchCriteria[filterGroups][2][filters][0][value]", "1")
                    .build()
        } else {
            HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.PWB_HOST_NAME)
                    .addPathSegments("rest/${getLanguage()}/V1/categories/list")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][field]", "parent_id")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][value]", categoryId)
                    .build()
        }

        val request = Request.Builder()
                .url(httpUrl)
                .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body()
                if (body == null) {
                    APIErrorUtils.parseError(response)?.let { callback.failure(it) }
                    return
                }

                try {
                    val categories = arrayListOf<Category>()
                    val data = JSONObject(body.string())
                    val items = data.getJSONArray("items")
                    for (i in 0 until items.length()) {
                        val category = Category()
                        val categoryObj = items.getJSONObject(i)
                        category.id = categoryObj.getString("id")
                        category.parentId = categoryObj.getString("parent_id")
                        category.departmentName = categoryObj.getString("name")
                        category.isActive = categoryObj.getBoolean("is_active")
                        category.position = categoryObj.getInt("position")
                        category.level = categoryObj.getString("level")
                        category.children = categoryObj.getString("children")
                        category.createdAt = categoryObj.getString("created_at")
                        category.updatedAt = categoryObj.getString("updated_at")
                        category.path = categoryObj.getString("path")
                        category.includeInMenu = if (categoryObj.getBoolean("include_in_menu")) 1 else 0

                        val customAttributes = categoryObj.getJSONArray("custom_attributes")
                        for (k in 0 until customAttributes.length()) {
                            // hard code get icon for tablet :(
                            when (customAttributes.getJSONObject(k).getString("name")) {
                                "image" -> {
                                    category.imageURL = customAttributes.getJSONObject(k).getString("value")
                                }
                            }
                        }
                        categories.add(category)
                    }

                    val modifiedCategories = arrayListOf<Category>()
                    categories.sortBy { it.position } // sort by category.position

                    // is super parent?
                    if (categoryId == CategoryUtils.SUPER_PARENT_ID) {
                        val filteredCategories = categories.filterIndexed { index, category ->
                            // filter only selected positions
                            index <= CategoryUtils.CATEGORY_SELECTED_POSITIONS && category.IsIncludeInMenu()
                        }
                        modifiedCategories.addAll(filteredCategories)

                        // add special category
                        specialIds.forEach {
                            val cat = categories.firstOrNull { category ->  category.id == it }
                            if (cat != null) {
                                modifiedCategories.add(cat)
                            }
                        }
                    } else {
                        modifiedCategories.addAll(categories)
                    }
                    callback.success(modifiedCategories) // return result
                } catch (e: Exception) {
                    callback.failure(e.getResultError())
                    Log.e("JSON Parser", "Error parsing data $e")
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.failure(e.getResultError())
            }
        })
    }

    // region product
    fun getProductDetail(sku: String, callback: ApiResponseCallback<Product?>) {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments(ProductDetailApi().getPath(getLanguage(), sku))
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()
                    val product = Product()
                    val productExtension = ProductExtension()
                    val stockItem = StockItem()
                    val images = arrayListOf<ProductGallery>()
                    val productOptions = arrayListOf<ProductOption>()
                    val productIdChildren = arrayListOf<String>()
                    val specifications = arrayListOf<Specification>()

                    try {
                        val productObject = JSONObject(data?.string())
                        product.id = productObject.getLong("id")
                        product.sku = productObject.getString("sku")
                        product.name = productObject.getString("name")
                        product.price = productObject.getDouble("price")
                        product.status = productObject.getInt("status")

                        if (productObject.has("type_id")) {
                            product.typeId = productObject.getString("type_id")
                        }

                        val extensionObj = productObject.getJSONObject("extension_attributes")
                        val stockObject = extensionObj.getJSONObject("stock_item")
//                        stockItem.itemId = stockObject.getLong("item_id")
                        stockItem.productId = stockObject.getLong("product_id")
                        stockItem.stockId = stockObject.getLong("stock_id")
                        if (!stockObject.isNull("qty")) {
                            stockItem.qty = stockObject.getInt("qty")
                        }
                        stockItem.isInStock = stockObject.getBoolean("is_in_stock")
                        stockItem.maxQTY = stockObject.getInt("max_sale_qty")
                        stockItem.minQTY = stockObject.getInt("min_sale_qty")
                        if (extensionObj.has("ispu_salable")) {
                            stockItem.is2HProduct = extensionObj.getBoolean("ispu_salable")
                        }
                        if (extensionObj.has("salable")) {
                            stockItem.isSalable = extensionObj.getBoolean("salable")
                        }
                        productExtension.stokeItem = stockItem // add stockItem to productExtension

                        // get product specification
                        if (extensionObj.has("specification_attributes")) {
                            val specAttrs = extensionObj.getJSONArray("specification_attributes")
                            for (specIndex in 0 until specAttrs.length()) {
                                val specAttr = specAttrs.getJSONObject(specIndex)
                                if (specAttr.has("attribute_code") && specAttr.has("label")) {
                                    // no need attr star_rating
                                    if (!specAttr.getString("attribute_code").contains("star_rating")) {
                                        val attrCode = specAttr.getString("attribute_code")
                                        val label = specAttr.getString("label")
                                        specifications.add(Specification(code = attrCode, label = label))
                                    }
                                }
                            }
                        }

                        if (extensionObj.has("configurable_product_options")) {
                            val productConfigArray = extensionObj.getJSONArray("configurable_product_options")
                            for (i in 0 until productConfigArray.length()) {
                                val id = productConfigArray.getJSONObject(i).getInt("id")
                                val attrId = productConfigArray.getJSONObject(i).getString("attribute_id")
                                val label = productConfigArray.getJSONObject(i).getString("label")
                                val position = productConfigArray.getJSONObject(i).getInt("position")
                                val productId = productConfigArray.getJSONObject(i).getLong("product_id")
                                val productValues = arrayListOf<ProductValue>()
                                if (productConfigArray.getJSONObject(i).has("values")) {
                                    val valuesArray = productConfigArray.getJSONObject(i).getJSONArray("values")
                                    for (j in 0 until valuesArray.length()) {
                                        val index = valuesArray.getJSONObject(j).getInt("value_index")
                                        val valueExtensionObject = valuesArray.getJSONObject(j).getJSONObject("extension_attributes")
                                        var valueLabel = ""
                                        if (valueExtensionObject.has("label")){
                                            valueLabel = valueExtensionObject.getString("label")
                                        }
                                        var value = ""
                                        if (valueExtensionObject.has("frontend_value")){
                                            value = valueExtensionObject.getString("frontend_value")
                                        }
                                        var type = ""
                                        if (valueExtensionObject.has("frontend_type")){
                                            type = valueExtensionObject.getString("frontend_type")
                                        }
                                        val productIDs = arrayListOf<Long>()
                                        if (valueExtensionObject.has("products")) {
                                            val productArray = valueExtensionObject.getJSONArray("products")
                                            for (k in 0 until productArray.length()) {
                                                productIDs.add(productArray.getLong(k))
                                            }
                                        }
                                        productValues.add(ProductValue(index, ProductValueExtension(valueLabel, value, type, productIDs)))
                                    }
                                }
                                productOptions.add(ProductOption(id, productId, attrId, label, position, productValues))
                            }
                        }
                        productExtension.productConfigOptions = productOptions

                        if (extensionObj.has("configurable_product_links")) {
                            val productChildrenArray = extensionObj.getJSONArray("configurable_product_links")
                            for (i in 0 until productChildrenArray.length()){
                                productIdChildren.add(productChildrenArray.getString(i))
                            }
                        }
                        productExtension.productConfigLinks = productIdChildren

                        val galleryArray = productObject.getJSONArray("media_gallery_entries")
                        for (i in 0 until galleryArray.length()) {
                            val id = galleryArray.getJSONObject(i).getString("id")
                            val type = galleryArray.getJSONObject(i).getString("media_type")
                            var label = ""
                            if(galleryArray.getJSONObject(i).has("label")) {
                                label = galleryArray.getJSONObject(i).getString("label")
                            }
                            var position = 0
                            if (!galleryArray.getJSONObject(i).isNull("position")) {
                                position = galleryArray.getJSONObject(i).getInt("position")
                            }
                            var disabled = false
                            if (!galleryArray.getJSONObject(i).isNull("disabled")) {
                                disabled = galleryArray.getJSONObject(i).getBoolean("disabled")
                            }
                            val file = galleryArray.getJSONObject(i).getString("file")
                            images.add(ProductGallery(id, type, label, position, disabled, file))
                        }
                        product.gallery = images

                        val attrArray = productObject.getJSONArray("custom_attributes")
                        for (i in 0 until attrArray.length()) {
                            val customAttr = attrArray.getJSONObject(i)
                            when (customAttr.getString("name")) {
                                "special_price" -> {
                                    val specialPrice = customAttr.getString("value")
                                    product.specialPrice = if (specialPrice.trim() == "") 0.0 else specialPrice.toDouble()
                                }

                                "special_from_date" -> {
                                    product.specialFromDate = customAttr.getString("value")
                                }

                                "special_to_date" -> {
                                    product.specialToDate = customAttr.getString("value")
                                }

                                "image" -> {
                                    product.image = customAttr.getString("value")
                                }

                                "delivery_method" -> {
                                    product.deliveryMethod = customAttr.getString("value")
                                }

                                "brand" -> {
                                    product.brand = customAttr.getString("value")
                                }

                                "description" -> {
                                    productExtension.description = customAttr.getString("value")
                                }

                                "short_description" -> {
                                    productExtension.shortDescription = customAttr.getString("value")
                                }

                                "barcode" -> {
                                    productExtension.barcode = customAttr.getString("value")
                                }

                                "payment_methods" -> {
                                    product.paymentMethod = customAttr.getString("value")
                                }

                                "shipping_methods" -> {
                                    product.shippingMethods = customAttr.getString("value")
                                }

                                "url_key" -> {
                                    product.urlKey = customAttr.getString("value")
                                }
                            }
                            // set value to product specifications
                            val customAttrCode = customAttr.getString("attribute_code")
                            specifications.forEach {
                                if (it.code == customAttrCode) {
                                    val customAttrValue = customAttr.getString("value")
                                    it.value = customAttrValue
                                }
                            }
                        }

                        val attrOptions = productObject.getJSONArray("custom_attributes_option")
                        // set value to product specifications
                        for (optionIndex in 0 until attrOptions.length()) {
                            val attrOption = attrOptions.getJSONObject(optionIndex)
                            val customAttrCode = attrOption.getString("attribute_code")
                            specifications.forEach {
                                if (it.code == customAttrCode) {
                                    val customAttrValue = attrOption.getString("value")
                                    it.value = customAttrValue
                                }
                            }
                        }
                        productExtension.specifications = specifications // addd product spec to product extension
                        product.extension = productExtension // add product extension to product
                        callback.success(product)
                    } catch (e: Exception) {
                        callback.failure(e.getResultError())
                        Log.e("JSON Parser", "Error parsing data $e")
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
                response?.close()
            }

            override fun onFailure(call: okhttp3.Call?, e: IOException) {
                callback.failure(e.getResultError())
            }
        })
    }

    fun getDeliveryInformation(sku: String, callback: ApiResponseCallback<List<DeliveryInfo>>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getDeliveryInfo(getLanguage(), sku).enqueue(object : Callback<List<DeliveryInfo>> {
            override fun onResponse(call: Call<List<DeliveryInfo>>, response: Response<List<DeliveryInfo>>) {
                if (response.body() != null) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<DeliveryInfo>>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun getAvailableStore(sku: String, callback: ApiResponseCallback<List<StoreAvailable>>) {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments("${getLanguage()}/rest/V1/storepickup/stores/active/$sku")
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()
                    val storeAvailableList: ArrayList<StoreAvailable> = arrayListOf()

                    try {
                        val storeAvailableArray = JSONArray(data?.string())
                        for (i in 0 until storeAvailableArray.length()) {
                            val storeAvailable = StoreAvailable()
                            val storeAvailableObject = storeAvailableArray.getJSONObject(i)
                            if (storeAvailableObject.has("source_item")) {
                                val sourceObject = storeAvailableObject.getJSONObject("source_item")
                                if (sourceObject.has("quantity")) {
                                    storeAvailable.qty = sourceObject.getInt("quantity")
                                }
                            }
                            if (storeAvailableObject.has("store")) {
                                val storeObject = storeAvailableObject.getJSONObject("store")
                                if (storeObject.has("name")) {
                                    storeAvailable.name = storeObject.getString("name")
                                }
                                if (storeObject.has("seller_code")) {
                                    storeAvailable.sellerCode = storeObject.getString("seller_code")
                                }
                                if (storeObject.has("custom_attributes")) {
                                    val attrArray = storeObject.getJSONArray("custom_attributes")
                                    for (j in 0 until attrArray.length()) {
                                        when (attrArray.getJSONObject(j).getString("name")) {
                                            "contact_phone" -> {
                                                storeAvailable.contactPhone = attrArray.getJSONObject(j).getString("value")
                                            }
                                        }
                                    }
                                }
                            }
                            storeAvailableList.add(storeAvailable)
                        }
                        callback.success(storeAvailableList)
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

    fun getProductByBarcode(barcode: String, callback: ApiResponseCallback<Product?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductByBarcode(Constants.CLIENT_MAGENTO, getLanguage(), "barcode", barcode, "eq")
                .enqueue(object : Callback<ProductSearchResponse> {
                    override fun onResponse(call: Call<ProductSearchResponse>?, response: Response<ProductSearchResponse>?) {
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

                    override fun onFailure(call: Call<ProductSearchResponse>?, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
    }

    fun getProductByProductJda(jda: String, callback: ApiResponseCallback<Product?>) {
        val productService = retrofit.create(ProductService::class.java)
        productService.getProductByBarcode(Constants.CLIENT_MAGENTO, getLanguage(), "jda_sku", jda, "eq")
                .enqueue(object : Callback<ProductSearchResponse> {
                    override fun onResponse(call: Call<ProductSearchResponse>?, response: Response<ProductSearchResponse>?) {
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

                    override fun onFailure(call: Call<ProductSearchResponse>?, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
    }
    // end product

    // region Cart
    fun getCart(callback: ApiResponseCallback<String?>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.createCart(getLanguage()).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                if (response != null && response.isSuccessful) {
                    val cartId = response.body()
                    callback.success(cartId)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun addProductToCart(cartId: String, cartItemBody: CartItemBody, callback: ApiResponseCallback<CartItem>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.addProduct(getLanguage(), cartId, cartItemBody).enqueue(object : Callback<CartItem> {
            override fun onResponse(call: Call<CartItem>?, response: Response<CartItem>?) {
                if (response != null && response.isSuccessful) {
                    val cartItem = response.body()
                    callback.success(cartItem)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<CartItem>?, t: Throwable) {
                callback.failure(t.getResultError())
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

            override fun onFailure(call: Call<Boolean>?, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun updateItem(cartId: String, itemId: Long, qty: Int, branch: Branch? = null,
                   callback: ApiResponseCallback<CartItem>) {
        val cartService = retrofit.create(CartService::class.java)

        val updateItemBody = if (branch != null) {
            UpdateItemBody.create(cartId, itemId, qty, branch)
        } else {
            UpdateItemBody.create(cartId, itemId, qty)
        }

        cartService.updateItem(getLanguage(), cartId, itemId, updateItemBody).enqueue(object : Callback<CartItem> {
            override fun onResponse(call: Call<CartItem>?, response: Response<CartItem>?) {
                if (response != null && response.isSuccessful) {
                    val cartItem = response.body()
                    callback.success(cartItem)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<CartItem>?, t: Throwable) {
                callback.failure(t.getResultError())
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
                callback.failure(t.getResultError())
            }
        })
    }

    fun createShippingInformation(cartId: String, shippingAddress: AddressInformation,
                                  billingAddress: AddressInformation,
                                  addressInfoExtensionBody: AddressInfoExtensionBody,
                                  deliveryOption: DeliveryOption,
                                  callback: ApiResponseCallback<ShippingInformationResponse>) {

        val cartService = retrofit.create(CartService::class.java)

        // clone shipping address and clear vat information
        // because no need send vat and company with shipping address
        val newShippingAddress = AddressInformation(
                city = shippingAddress.city,
                region = shippingAddress.region,
                regionId = shippingAddress.regionId,
                regionCode = shippingAddress.regionCode,
                countryId = shippingAddress.countryId,
                street = shippingAddress.street,
                postcode = shippingAddress.postcode,
                firstname = shippingAddress.firstname,
                lastname = shippingAddress.lastname,
                email = shippingAddress.email,
                telephone = shippingAddress.telephone,
                subAddress = shippingAddress.subAddress,
                sameBilling = shippingAddress.sameBilling,
                company = "",
                vatId = "")

        // TODO: Refactor checking app flavor
        val addressInformationBody = if (BuildConfig.FLAVOR == "pwb") {
            AddressInformationBody(newShippingAddress, billingAddress,
                    deliveryOption.methodCode, deliveryOption.carrierCode, addressInfoExtensionBody)
        } else {
            AddressInformationBody(newShippingAddress.modifyToCdsType(),
                    billingAddress.modifyToCdsType(), deliveryOption.methodCode,
                    deliveryOption.carrierCode, addressInfoExtensionBody)
        }

        val shippingBody = ShippingBody(addressInformationBody)
        cartService.createShippingInformation(getLanguage(), cartId, shippingBody)
                .enqueue(object : Callback<ShippingInformationResponse> {
                    override fun onResponse(call: Call<ShippingInformationResponse>?,
                                            response: Response<ShippingInformationResponse>?) {
                        if (response != null && response.isSuccessful) {
                            val shippingInformation = response.body()
                            callback.success(shippingInformation)
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ShippingInformationResponse>?, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
    }

    fun setProduct2hShippingInformation(cartId: String, storeAddress: AddressInformation,
                                        addressInfoExtensionBody: AddressInfoExtensionBody,
                                        deliveryOption: DeliveryOption,
                                        callback: ApiResponseCallback<ShippingInformationResponse>) {

        val addressInformationBody = AddressInformationBody(storeAddress, null,
                deliveryOption.methodCode, deliveryOption.carrierCode, addressInfoExtensionBody)
        val shippingBody = ShippingBody(addressInformationBody)

        cartService.createShippingInformation(getLanguage(), cartId, shippingBody)
                .enqueue(object : Callback<ShippingInformationResponse> {
                    override fun onResponse(call: Call<ShippingInformationResponse>?,
                                            response: Response<ShippingInformationResponse>?) {
                        if (response != null && response.isSuccessful) {
                            val shippingInformation = response.body()
                            callback.success(shippingInformation)
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ShippingInformationResponse>?, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
    }

    fun getOrder(orderId: String, callback: ApiResponseCallback<OrderResponse>) {
        val cartService = retrofit.create(CartService::class.java)
        cartService.getOrder(Constants.CLIENT_MAGENTO, getLanguage(), orderId).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>?, response: Response<OrderResponse>?) {
                if (response != null && response.isSuccessful) {
                    val orderResponse = response.body()
                    callback.success(orderResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<OrderResponse>?, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
    // endregion

    // region get PWB Customer
    fun getPWBCustomer(telephone: String, callback: ApiResponseCallback<List<EOrderingMember>>) {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments(PwbMemberApi().getPath(getLanguage()))
                .addQueryParameter("searchCriteria[filter_groups][0][filters][0][field]", "telephone")
                .addQueryParameter("searchCriteria[filter_groups][0][filters][0][value]", telephone)
                .addQueryParameter("searchCriteria[filter_groups][0][filters][0][condition_type]", "eq")
                .build()

        val request = Request.Builder()
                .url(httpUrl)
                .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                .build()

        defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    val data = response.body()

                    try {
                        val dataObject = JSONObject(data?.string())
                        val items = dataObject.getJSONArray("items")
                        val memberList: ArrayList<EOrderingMember> = arrayListOf()

                        if (items.length() < 1) {
                            callback.success(arrayListOf())
                            return
                        }

                        for (i in 0 until items.length()) {
                            val memberDetail = items.getJSONObject(i)
                            val pwbMember = EOrderingMember()

                            if (memberDetail.has("id")) {
                                pwbMember.id = memberDetail.getLong("id")
                            }

                            if (memberDetail.has("customer_id")) {
                                pwbMember.customerId = memberDetail.getLong("customer_id")
                            }

                            if (memberDetail.has("region_id")) {
                                pwbMember.regionId = memberDetail.getInt("region_id")
                            }

                            if (memberDetail.has("country_id")) {
                                pwbMember.countryId = memberDetail.getString("country_id")
                            }

                            if (memberDetail.has("street")) {
                                val streets = arrayListOf<String>()
                                val streetArrayObject = memberDetail.getJSONArray("street")
                                for (j in 0 until streetArrayObject.length()) {
                                    streets.add(streetArrayObject.getString(j))
                                }
                                pwbMember.street = streets
                            }

                            if (memberDetail.has("telephone")) {
                                pwbMember.telephone = memberDetail.getString("telephone")
                            }

                            if (memberDetail.has("postcode")) {
                                pwbMember.postcode = memberDetail.getString("postcode")
                            }

                            if (memberDetail.has("city")) {
                                pwbMember.city = memberDetail.getString("city")
                            }

                            if (memberDetail.has("firstname")) {
                                pwbMember.firstname = memberDetail.getString("firstname")
                            }

                            if (memberDetail.has("lastname")) {
                                pwbMember.lastname = memberDetail.getString("lastname")
                            }

                            if (memberDetail.has("default_shipping")) {
                                pwbMember.defaultShipping = memberDetail.getBoolean("default_shipping")
                            }

                            if (memberDetail.has("default_billing")) {
                                pwbMember.defaultShipping = memberDetail.getBoolean("default_billing")
                            }

                            if (memberDetail.has("custom_attributes")) {
                                val memberSubAddress = MemberSubAddress()
                                val customAttributes = memberDetail.getJSONArray("custom_attributes")
                                for (m in 0 until customAttributes.length()) {
                                    val ctmAttr = customAttributes.getJSONObject(m)
                                    when (ctmAttr.getString("name")) {
                                        "house_no" -> {
                                            val houseNo = ctmAttr.getString("value")
                                                    ?: ""
                                            memberSubAddress.houseNo = houseNo
                                        }

                                        "building" -> {
                                            val building = ctmAttr.getString("value") ?: ""
                                            memberSubAddress.building = building
                                        }

                                        "soi" -> {
                                            val soi = ctmAttr.getString("value") ?: ""
                                            memberSubAddress.soi = soi
                                        }

                                        "address_line" -> {
                                            val street = ctmAttr.getString("value") ?: ""
                                            memberSubAddress.street = street
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
                                            val subDistrict = ctmAttr.getString("value")
                                                    ?: ""
                                            memberSubAddress.subDistrict = subDistrict
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
                                pwbMember.subAddress = memberSubAddress
                            }
                            memberList.add(pwbMember)
                        }
                        callback.success(memberList)
                    } catch (e: Exception) {
                        callback.failure(e.getResultError())
                        Log.e("JSON Parser", "Error parsing data $e")
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
                response?.close()
            }

            override fun onFailure(call: okhttp3.Call?, e: IOException?) {
                callback.failure(APIError(e))
            }
        })
    }
    // endregion

    // region address information
    fun getProvinces(force: Boolean = false, callback: ApiResponseCallback<List<Province>>) {
        val endpointName = "rest/${getLanguage()}/V1/region/province"
        val memberService = retrofit.create(MemberService::class.java)

        // If already cached then do nothing
        if (!force && database.hasFreshlyCachedEndpoint(endpointName, 24)) {
            Log.i("ApiManager", "getProvinces: using cached")
            callback.success(database.provinces)
            return
        }

        Log.i("ApiManager", "getProvinces: calling endpoint")
        memberService.getProvinces(Constants.CLIENT_MAGENTO, getLanguage()).enqueue(object : Callback<List<Province>> {
            override fun onResponse(call: Call<List<Province>>, response: Response<List<Province>>) {
                if (response.isSuccessful) {
                    val provinces = response.body()
                    if (provinces != null) {
                        // store provinces
                        provinces.forEach { database.storeProvince(it) }

                        // cached endpoint
                        database.updateCachedEndpoint(endpointName)
                        when (getLanguage()) {
                            "th" -> {
                                database.clearCachedEndpoint("rest/${AppLanguage.EN}/V1/region/province")
                                Log.i("ApiManager", "getProvinces: clear endpoint ${AppLanguage.EN}")
                            }
                            "en" -> {
                                database.clearCachedEndpoint("rest/${AppLanguage.TH}/V1/region/province")
                                Log.i("ApiManager", "getProvinces: clear endpoint ${AppLanguage.TH}")
                            }
                        }

                        callback.success(provinces)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun getDistricts(provinceId: String, callback: ApiResponseCallback<List<District>>) {
        val memberService = retrofit.create(MemberService::class.java)
        memberService.getDistricts(Constants.CLIENT_MAGENTO, getLanguage(), provinceId).enqueue(object : Callback<List<District>> {
            override fun onResponse(call: Call<List<District>>, response: Response<List<District>>) {
                if (response.isSuccessful) {
                    val districts = response.body()
                    if (districts != null) {
                        // store districts
                        districts.forEach { database.storeDistrict(it) }
                        callback.success(districts)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<District>>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun getSubDistricts(provinceId: String, districtId: String, callback: ApiResponseCallback<List<SubDistrict>>) {
        val memberService = retrofit.create(MemberService::class.java)
        memberService.getSubDistricts(Constants.CLIENT_MAGENTO, getLanguage(), provinceId, districtId).enqueue(object : Callback<List<SubDistrict>> {
            override fun onResponse(call: Call<List<SubDistrict>>, response: Response<List<SubDistrict>>) {
                if (response.isSuccessful) {
                    val subDistricts = response.body()
                    if (subDistricts != null) {
                        // store districts & store postcode
                        subDistricts.forEach {
                            database.storeSubDistrict(it)
                            database.storePostcode(it.asPostcode())
                        }
                        callback.success(subDistricts)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<SubDistrict>>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
    // endregion

    /**
     * param language
     * in PWB is th
     * in CDS is cds_th
     * */
    fun getLanguage(): String {
        val language = preferenceManager.getDefaultLanguage()
        return when (BuildConfig.FLAVOR) {
            "cds" -> "cds_$language"
            else -> language
        }
    }

    fun createRequestHttps(path: String): Request {
        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments(path)
                .build()

        return Request.Builder()
                .url(httpUrl)
                .addHeader(HEADER_AUTHORIZATION, Constants.CLIENT_MAGENTO)
                .build()
    }
}
