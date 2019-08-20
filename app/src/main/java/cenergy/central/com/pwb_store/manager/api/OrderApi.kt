package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.body.PaymentInfoBody
import cenergy.central.com.pwb_store.model.response.PaymentMethod
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderApi {


    companion object {
        private const val RESPONSE_REDIRECT_CODE = 302
        private const val HEADER_LOCATION = "Location"
    }

    fun updateOrder(context: Context, cartId: String, staffId: String, sellerCode: String, paymentMethod: PaymentMethod,
                    email: String, billingAddress: AddressInformation, theOneCardNo: String, callback: CreateOderCallback) {
        val apiManager = HttpManagerMagento.getInstance(context)

        val paymentMethodBody = PaymentInfoBody.createPaymentInfoBody(cartId = cartId,
                staffId = staffId, retailerId = sellerCode, customerEmail = email, billingAddress = billingAddress,
                paymentMethod = paymentMethod, theOneCardNo = theOneCardNo)
        apiManager.cartService.updateOrder(apiManager.getLanguage(), cartId, paymentMethodBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                } else if (response.code() == RESPONSE_REDIRECT_CODE && response.errorBody() != null) { // 302 redirect
                    val errorBody = response.errorBody()?.string().toString().replace("\"", "")
                    val url = response.headers()[HEADER_LOCATION] ?: ""
                    callback.onSuccessAndRedirect(errorBody, url)
                } else {
                    callback.onFailure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.onFailure(APIError(t))
            }
        })
    }

    interface CreateOderCallback {
        fun onSuccess(oderId: String?)
        fun onSuccessAndRedirect(oderId: String?, url: String)
        fun onFailure(error: APIError)
    }
}