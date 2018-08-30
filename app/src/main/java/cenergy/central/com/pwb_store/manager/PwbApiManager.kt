package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import cenergy.central.com.pwb_store.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Anuphap Suwannamas on 30/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class PwbApiManager {
        private val mContext: Context = Contextor.getInstance().context
        private var retrofit: Retrofit
        private var httpClient: OkHttpClient

        init {
            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient = OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                                .addHeader(HEADER_AUTHORIZATION, "//TODO: add client")
                                .build()
                        chain.proceed(request)
                    }
                    .addInterceptor(interceptor)
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl("//TODO: Add hostname")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
        }

        companion object {
            //Specific Header
            private const val HEADER_AUTHORIZATION = "Authorization"

            @SuppressLint("StaticFieldLeak")
            private var instance: PwbApiManager? = null

            fun getInstance(): PwbApiManager {
                if (instance == null)
                    instance = PwbApiManager()
                return instance as PwbApiManager
            }
        }
}