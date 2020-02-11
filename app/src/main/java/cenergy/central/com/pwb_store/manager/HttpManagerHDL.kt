package cenergy.central.com.pwb_store.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.activity.LoginActivity
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.manager.service.HDLService
import cenergy.central.com.pwb_store.model.body.BookingShippingSlotBody
import cenergy.central.com.pwb_store.model.body.ShippingSlotBody
import cenergy.central.com.pwb_store.model.response.BookingNumberResponse
import cenergy.central.com.pwb_store.model.response.HDLMemberResponse
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
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

class HttpManagerHDL(var context: Context) {
    private  var retrofit: Retrofit
    private val pref by lazy { PreferenceManager(context) }
    private val database by lazy { RealmController.getInstance() }

    init {
        val session = auth(pref.accessKey!!, pref.secretKey!!)
        val awsCredentialsProvider = PwbAWSCredentialsProvider(session)
        val awsInterceptor = AwsInterceptor(awsCredentialsProvider, pref.serviceName!!, pref.region!!, pref.xApiKey!!)
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
                pref.xApiKey != null && pref.serviceName != null
    }

    fun getHDLCustomer(number: String, callback: ApiResponseCallback<HDLMemberResponse>){
        if (isSecretKeyNotNull()){
            val mHDLService = retrofit.create(HDLService::class.java)
            mHDLService.getHDLMembers(number, true).enqueue(object : Callback<HDLMemberResponse>{
                override fun onResponse(call: Call<HDLMemberResponse>, response: Response<HDLMemberResponse>) {
                    if (response.body() != null){
                        callback.success(response.body())
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<HDLMemberResponse>, t: Throwable) {
                    callback.failure(t.getResultError())
                }
            })
        } else {
            userLogout(context)
        }
    }

    fun getShippingSlot(shippingSlotBody: ShippingSlotBody, callback: ApiResponseCallback<ShippingSlotResponse>) {
        if (isSecretKeyNotNull()){
            val mHDLService = retrofit.create(HDLService::class.java)
            mHDLService.getShippingSlot("application/json", shippingSlotBody).enqueue(object : Callback<ShippingSlotResponse> {
                override fun onResponse(call: Call<ShippingSlotResponse>?, response: Response<ShippingSlotResponse>?) {
                    if (response != null && response.isSuccessful) {
                        val orderResponse = response.body()
                        callback.success(orderResponse)
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<ShippingSlotResponse>?, t: Throwable) {
                    callback.failure(t.getResultError())
                }
            })
        } else {
            userLogout(context)
        }
    }

    fun createBooking(bookingShippingSlotBody: BookingShippingSlotBody, callback: ApiResponseCallback<BookingNumberResponse>) {
        val mHDLService = retrofit.create(HDLService::class.java)
        mHDLService.createBooking("application/json", bookingShippingSlotBody).enqueue(object : Callback<BookingNumberResponse> {
            override fun onResponse(call: Call<BookingNumberResponse>?, response: Response<BookingNumberResponse>?) {
                if (response != null && response.isSuccessful) {
                    val orderResponse = response.body()
                    callback.success(orderResponse)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<BookingNumberResponse>?, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    private class PwbAWSCredentialsProvider internal constructor(private val session: Session) : AWSCredentialsProvider {
        override fun getCredentials(): AWSCredentials {
            return BasicAWSCredentials(session.accessKey!!, session.secretKey!!)
        }

        override fun refresh() {

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
        context.startActivity(intent)
    }

    private inner class Session {
        internal var accessKey: String? = null
        internal var secretKey: String? = null
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: HttpManagerHDL? = null

        fun getInstance(context: Context): HttpManagerHDL {
            if (instance == null)
                instance = HttpManagerHDL(context)
            return instance as HttpManagerHDL
        }
    }
}