package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.activity.LoginActivity
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.manager.service.MemberService
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class HttpMangerSiebel(var context: Context) {
    private var retrofit: Retrofit
    private val pref by lazy { PreferenceManager(context) }
    private val database by lazy { RealmController.getInstance() }

    init {
        val session = auth(pref.accessKey ?:"", pref.secretKey ?:"")
        val awsCredentialsProvider = PwbAWSCredentialsProvider(session)
        val awsInterceptor = AwsInterceptor(awsCredentialsProvider, pref.serviceName ?:"", pref.region ?:"", pref.xApiKey ?:"")
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) interceptor.level = HttpLoggingInterceptor.Level.BODY
        val defaultHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .build()

                    chain.proceed(request)
                }
                .addInterceptor(awsInterceptor)
                .addInterceptor(interceptor)
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(Constants.CENTRAL_HOST_NAME)
                .addConverterFactory(GsonConverterFactory.create())
                .client(defaultHttpClient)
                .build()
    }

    private fun isSecretKeyNotNull() : Boolean{
        return pref.accessKey != null && pref.secretKey != null && pref.region != null &&
                pref.xApiKey != null && pref.serviceName != null && pref.xApiKeyConsent != null
    }

    private class PwbAWSCredentialsProvider internal constructor(private val session: Session) : AWSCredentialsProvider {

        override fun getCredentials(): AWSCredentials {
            return BasicAWSCredentials(session.accessKey!!, session.secretKey!!)
        }

        override fun refresh() {

        }
    }

    fun verifyMemberFromT1C(mobile: String, mobileCountryCode: String, callback: ApiResponseCallback<List<MemberResponse>>) {
        if (isSecretKeyNotNull()){
            val memberService = retrofit.create(MemberService::class.java)
            memberService.geT1CtMemberFromMobile(mobile, mobileCountryCode).enqueue(object : Callback<List<MemberResponse>> {
                override fun onResponse(call: Call<List<MemberResponse>>?, response: Response<List<MemberResponse>>?) {
                    if (response != null && response.isSuccessful) {
                        val t1MemberResponse = response.body()
                        callback.success(t1MemberResponse)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<List<MemberResponse>>?, t: Throwable) {
                    callback.failure(t.getResultError())
                }
            })
        } else {
            userLogout(context)
        }
    }

    fun getT1CMember(customerId: String, callback: ApiResponseCallback<Member>) {
        if (isSecretKeyNotNull()){
            val memberService = retrofit.create(MemberService::class.java)
            memberService.getT1CMember(customerId).enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>?, response: Response<Member>?) {
                    if (response != null && response.isSuccessful) {
                        val orderResponse = response.body()
                        callback.success(orderResponse)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<Member>?, t: Throwable) {
                    callback.failure(t.getResultError())
                }
            })
        } else {
            userLogout(context)
        }
    }

    private fun auth(accessKey: String, secretKey: String): Session {
        val auth = Session()
        auth.accessKey = accessKey
        auth.secretKey = secretKey
        return auth
    }

    private fun userLogout(context: Context) {
        database.userLogout()
        pref.userLogout()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private inner class Session {
        internal var accessKey: String? = null
        internal var secretKey: String? = null
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: HttpMangerSiebel? = null

        fun getInstance(context: Context): HttpMangerSiebel {
            if (instance == null)
                instance = HttpMangerSiebel(context)
            return instance as HttpMangerSiebel
        }
    }
}


