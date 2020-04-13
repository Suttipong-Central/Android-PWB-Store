package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.response.PaymentAgent
import cenergy.central.com.pwb_store.model.response.PaymentInformationResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentApi {
    fun retrievePaymentInformation(context: Context, cartId: String,
                                   callback: ApiResponseCallback<List<PaymentAgent>>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        apiManager.cartService.retrievePaymentInformation(apiManager.getLanguage(),
                cartId).enqueue(object : Callback<PaymentInformationResponse> {
            override fun onResponse(call: Call<PaymentInformationResponse>,
                                    response: Response<PaymentInformationResponse>) {
                if (response.isSuccessful) {
                    val paymentInformation = response.body()
                    val paymentAgents = paymentInformation?.extension?.paymentAgents  ?: arrayListOf()
                    callback.success(paymentAgents)
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<PaymentInformationResponse>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
}