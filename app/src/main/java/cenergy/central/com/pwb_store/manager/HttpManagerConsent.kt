package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.activity.LoginActivity
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.manager.service.ConsentService
import cenergy.central.com.pwb_store.model.body.ConsentBody
import cenergy.central.com.pwb_store.model.response.ConsentInfoResponse
import cenergy.central.com.pwb_store.model.response.ConsentResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpManagerConsent(var context: Context) {
    private val pref by lazy { PreferenceManager(context) }
    private val database by lazy { RealmController.getInstance() }
    private val apiManager by lazy {  HttpManagerMagento.getInstance(context) }

    private var retrofit: Retrofit
    private var defaultHttpClient: OkHttpClient

    init {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
        defaultHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder().build()
                    chain.proceed(request)
                }
                .addInterceptor(interceptor)
                .build()

        val gson = GsonConverterFactory.create()

        retrofit = Retrofit.Builder()
                .baseUrl(Constants.CONSENT_HOSTNAME)
                .addConverterFactory(gson)
                .client(defaultHttpClient)
                .build()
    }

    fun getConsentInfo(callback: ApiResponseCallback<ConsentInfoResponse>) {
        if (isSecretKeyNotNull()) {
            val consentService = retrofit.create(ConsentService::class.java)
            if (BuildConfig.IS_PRODUCTION) {
                consentService.getConsentInfo(HttpManagerMagento.CLIENT_NAME_E_ORDERING,
                        apiManager.getUserClientType(), apiManager.getUserRetailerId(),
                        Constants.CONSENT_CHANNEL, Constants.CONSENT_PARTNER, "application/json",
                        pref.xApiKeyConsent ?: "").enqueue(object : Callback<ConsentInfoResponse> {
                    override fun onResponse(call: Call<ConsentInfoResponse>, response: Response<ConsentInfoResponse>?) {
                        if (response != null && response.isSuccessful && response.body() != null) {
                            callback.success(response.body())
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ConsentInfoResponse>, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
            } else {
                consentService.getConsentInfoStaging(HttpManagerMagento.CLIENT_NAME_E_ORDERING,
                        apiManager.getUserClientType(), apiManager.getUserRetailerId(),
                        Constants.CONSENT_CHANNEL, Constants.CONSENT_PARTNER, "application/json",
                        pref.xApiKeyConsent ?: "").enqueue(object : Callback<ConsentInfoResponse> {
                    override fun onResponse(call: Call<ConsentInfoResponse>, response: Response<ConsentInfoResponse>?) {
                        if (response != null && response.isSuccessful && response.body() != null) {
                            callback.success(response.body())
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ConsentInfoResponse>, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
            }
        } else {
            userLogout()
        }
    }

    fun setConsent(consentBody: ConsentBody, callback: ApiResponseCallback<ConsentResponse>) {
        if (isSecretKeyNotNull()) {
            val consentService = retrofit.create(ConsentService::class.java)
            if (BuildConfig.IS_PRODUCTION) {
                consentService.setConsent(HttpManagerMagento.CLIENT_NAME_E_ORDERING,
                        apiManager.getUserClientType(), apiManager.getUserRetailerId(),
                        "application/json", pref.xApiKeyConsent ?: "", consentBody).enqueue(object : Callback<ConsentResponse> {
                    override fun onResponse(call: Call<ConsentResponse>, response: Response<ConsentResponse>) {
                        if (response.body() != null && response.isSuccessful) {
                            callback.success(response.body())
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ConsentResponse>, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
            } else {
                consentService.setConsentStaging(HttpManagerMagento.CLIENT_NAME_E_ORDERING,
                        apiManager.getUserClientType(), apiManager.getUserRetailerId(),
                        "application/json", pref.xApiKeyConsent ?: "", consentBody).enqueue(object : Callback<ConsentResponse> {
                    override fun onResponse(call: Call<ConsentResponse>, response: Response<ConsentResponse>) {
                        if (response.body() != null && response.isSuccessful) {
                            callback.success(response.body())
                        } else {
                            callback.failure(APIErrorUtils.parseError(response))
                        }
                    }

                    override fun onFailure(call: Call<ConsentResponse>, t: Throwable) {
                        callback.failure(t.getResultError())
                    }
                })
            }
        } else {
            userLogout()
        }
    }

    private fun isSecretKeyNotNull(): Boolean {
        return pref.accessKey != null && pref.secretKey != null && pref.region != null &&
                pref.xApiKey != null && pref.serviceName != null && pref.xApiKeyConsent != null
    }

    private fun userLogout() {
        database.userLogout()
        pref.userLogout()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: HttpManagerConsent? = null

        fun getInstance(context: Context): HttpManagerConsent {
            if (instance == null)
                instance = HttpManagerConsent(context)
            return instance as HttpManagerConsent
        }
    }
}