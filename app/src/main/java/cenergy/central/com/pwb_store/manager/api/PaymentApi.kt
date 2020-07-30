package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.body.PaymentInfoBody
import cenergy.central.com.pwb_store.model.response.PaymentAgent
import cenergy.central.com.pwb_store.model.response.PaymentInformationResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentApi {
    fun retrievePaymentInformation(context: Context, cartId: String,
                                   callback: ApiResponseCallback<PaymentInformationResponse>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        apiManager.cartService.retrievePaymentInformation(apiManager.getLanguage(),
                cartId).enqueue(object : Callback<PaymentInformationResponse> {
            override fun onResponse(call: Call<PaymentInformationResponse>,
                                    response: Response<PaymentInformationResponse>) {
                if (response.isSuccessful) {
                    val paymentInformation = response.body()
                    callback.success(paymentInformation)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<PaymentInformationResponse>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }

    fun updatePaymentInformation(context: Context, cartId: String, paymentMethodBody: PaymentInfoBody,
                                 callback: ApiResponseCallback<Boolean>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        val cartService = apiManager.cartService
        val languageCode = apiManager.getLanguage()
        cartService.updatePaymentInformation(languageCode, cartId, paymentMethodBody).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    callback.success(true)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
}
