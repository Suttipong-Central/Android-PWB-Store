package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.service.CategoryService
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Category
import cenergy.central.com.pwb_store.model.ProductFilterHeader
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

    fun retrieveCategories(callback: ApiResponseCallback<Category?>) {
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
}
