package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.manager.service.UserService
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Store
import cenergy.central.com.pwb_store.model.UserInformation
import cenergy.central.com.pwb_store.model.UserToken
import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.LoginResponse
import cenergy.central.com.pwb_store.model.response.LogoutResponse
import cenergy.central.com.pwb_store.model.response.UserResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import io.realm.RealmList
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

class PwbApiManager(context: Context) {
    private var retrofit: Retrofit
    private var httpClient: OkHttpClient
    private var database: RealmController = RealmController.with(context)

    private lateinit var userToken: String

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

        if (database.userToken != null) {
            setUserToken(database.userToken.token)
        }
    }

    companion object {
        private const val HOST_NAME = "http://chuanl.ddns.net"

        @SuppressLint("StaticFieldLeak")
        private var instance: PwbApiManager? = null

        fun getInstance(context: Context): PwbApiManager {
            if (instance == null)
                instance = PwbApiManager(context)
            return instance as PwbApiManager
        }
    }


    fun setUserToken(token: String) {
        this.userToken = token
    }

    fun getUserToken(): String = this.userToken

    fun userLogin(username: String, password: String, callback: ApiResponseCallback<UserResponse?>) {
        val userService = retrofit.create(UserService::class.java)
        val userBody = UserBody(username = username, password = password)
        userService.userLogin(userBody).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>?) {
                if (response != null && response.isSuccessful) {
                    val loginResponse = response.body()
                    val userToken = loginResponse?.successes?.token
                    userToken?.let { setUserToken(it) } // save user token

                    // get user information
                    getUserDetail(callback)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }

    fun getUserDetail(callback: ApiResponseCallback<UserResponse?>) {
        val userService = retrofit.create(UserService::class.java)
        userService.retrieveUser("Bearer $userToken").enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                if (response != null && response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        // save user token
                        database.saveUserToken(UserToken(token = userToken))
                        // save user information
                        val stores = RealmList<Store>()
                        if (userResponse.store != null) {
                            stores.add(userResponse.store)
                        }
                        val userInformation = UserInformation(userId = userResponse.user.userId, user = userResponse.user, stores = stores)
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

    fun userLogout(callback: ApiResponseCallback<LogoutResponse?>) {
        val userService = retrofit.create(UserService::class.java)
        userService.userLogout("Bearer $userToken").enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>?, response: Response<LogoutResponse>?) {
                if (response != null && response.isSuccessful) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<LogoutResponse>?, t: Throwable?) {
                callback.failure(APIError(t))
            }
        })
    }
}