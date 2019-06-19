package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
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

    fun updateOrder(context: Context, cartId: String, staffId: String, sellerCode: String, paymentMethod: PaymentMethod,
                    email: String, billingAddress: AddressInformation, callback: ApiResponseCallback<String>){
        val apiManager = HttpManagerMagento.getInstance(context)

        val paymentMethodBody = PaymentInfoBody.createPaymentInfoBody(cartId = cartId,
                staffId = staffId, retailerId = sellerCode, email = email, billingAddress = billingAddress, paymentMethod = paymentMethod.code)
        apiManager.cartService.updateOrder(apiManager.getLanguage(), cartId, paymentMethodBody).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }
}