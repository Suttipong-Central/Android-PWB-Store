package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.service.CartService
import cenergy.central.com.pwb_store.manager.service.UserService
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.LoginResponse
import cenergy.central.com.pwb_store.model.response.UserResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                                .build()
                        chain.proceed(request)
                    }
                    .addInterceptor(interceptor)
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl(HOST_NAME)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build()
        }

        companion object {
            private const val HOST_NAME = "http://chuanl.ddns.net"

            @SuppressLint("StaticFieldLeak")
            private var instance: PwbApiManager? = null

            fun getInstance(): PwbApiManager {
                if (instance == null)
                    instance = PwbApiManager()
                return instance as PwbApiManager
            }
        }

    fun userLogin(username: String, password: String, callback: ApiResponseCallback<LoginResponse> ){
        val userService = retrofit.create(UserService::class.java)
        val userBody = UserBody(username = username, password = password)
        userService.userLogin(userBody).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>?) {
                if (response != null && response.isSuccessful) {
                    val loginResponse = response.body()
                    callback.success(loginResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getUserDetail(bearerToken: String, callback: ApiResponseCallback<UserResponse> ){
        val userService = retrofit.create(UserService::class.java)
        //todo get user detail here krub P'Aa
    }
}