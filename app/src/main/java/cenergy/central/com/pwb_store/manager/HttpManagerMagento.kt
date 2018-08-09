package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.service.CategoryService
import cenergy.central.com.pwb_store.manager.service.ProductService
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
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

    fun retrieveCategories(force:Boolean,callback: ApiResponseCallback<Category?>) {
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

    fun retrieveProductDetail(productId: String, string: String, callback: ApiResponseCallback<ProductDetailNew?>) {
        retrofit?.let { retrofit ->
            val productService = retrofit.create(ProductService::class.java)
            productService.getProductDetail(productId, string)
            .enqueue(object : Callback<ProductDetailNew>{
                override fun onResponse(call: Call<ProductDetailNew>?, response: Response<ProductDetailNew>?) {
                    if (response != null) {
                        val productDetailNew = response.body()
                        callback.success(productDetailNew)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<ProductDetailNew>?, t: Throwable?) {
                    callback.failure(APIError(t))
                }
            })
        }
    }
}
