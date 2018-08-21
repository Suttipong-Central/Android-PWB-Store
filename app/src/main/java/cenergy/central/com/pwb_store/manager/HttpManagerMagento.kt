package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.service.CategoryService
import cenergy.central.com.pwb_store.manager.service.ProductService
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Category
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductFilterHeader
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpManagerMagento {
    private val mContext: Context = Contextor.getInstance().context
    private var retrofit: Retrofit? = null

    init {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
        val defaultHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .addHeader(HEADER_AUTHORIZATION, CLIENT_MAGENTO)
                            .build()
                    chain.proceed(request)
                }
                .addInterceptor(interceptor)
                .build()

        // Create gson
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_MAGENTO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultHttpClient)
                .build()
    }

    companion object {
        //Specific Header
        private const val HEADER_AUTHORIZATION = "Authorization"
        //Specific Client
        private const val CLIENT_MAGENTO = "Bearer ba102y6thpckeoqgo196u82tllvlf50q"
        private const val BASE_URL_MAGENTO = "https://staging.powerbuy.co.th/"

        @SuppressLint("StaticFieldLeak")
        private var instance: HttpManagerMagento? = null

        fun getInstance(): HttpManagerMagento {
            if (instance == null)
                instance = HttpManagerMagento()
            return instance as HttpManagerMagento
        }
    }

    fun retrieveCategories(force:Boolean, callback: ApiResponseCallback<Category?>) {
        // If already cached then do
        val endpointName = "/rest/V1/categories"
        val database = RealmController.with(mContext)
        if (!force && database.hasFreshlyCachedEndpoint(endpointName)) {
            Log.i("PBE", "retrieveCategories: using cached")
            callback.success(database.category)
            return
        }

        Log.i("PBE", "retrieveCategories: calling endpoint")
        retrofit?.let {
            val categoryService = it.create(CategoryService::class.java)
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
        retrofit?.let {
            val categoryService = it.create(CategoryService::class.java)
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
    }

    fun retrieveProductList(category: String, categoryId: String, conditionType: String,
                            pageSize: Int, currentPage: Int, typeSearch: String, fields: String,
                            callback: ApiResponseCallback<ProductResponse?>){
        retrofit?.let { retrofit ->
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductList(category, categoryId, conditionType, pageSize,
                    currentPage, typeSearch, fields).enqueue(object : Callback<ProductResponse>{
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

    fun retrieveProductList(categoryId: String, pageSize: Int, currentPage: Int, callback: ApiResponseCallback<ProductResponse?>) {
        retrofit?.let { retrofit ->
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductList(categoryId, pageSize, currentPage).enqueue(object : Callback<ProductResponse>{
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

    fun retrieveProductDetail(sku: String, string: String, callback: ApiResponseCallback<Product?>) {
        retrofit?.let { retrofit ->
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductDetail(sku, string)
            .enqueue(object : Callback<Product>{
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
    }

    fun getProductFromBarcode(filterBarcode: String, barcode: String, eq: String, orderBy: String,
                              pageSize: Int, currentPage: Int, callback: ApiResponseCallback<Product?>) {
        retrofit?.let {
            val productService = it.create(ProductService::class.java)
            productService.getProductFromBarcode(filterBarcode, barcode, eq, orderBy, pageSize, currentPage)
                    .enqueue(object : Callback<ProductResponse> {

                        override fun onResponse(call: Call<ProductResponse>?, response: Response<ProductResponse>?) {
                            if (response != null) {
                                val productResponse = response.body()
                                if (productResponse?.products!!.size > 0) {
                                    callback.success(productResponse.products[0])
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
    }
}
