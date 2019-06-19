package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.body.PaymentInfoBody
import cenergy.central.com.pwb_store.model.response.PaymentMethod
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class OrderApi {

    /**
     * Use this because we need to check response from API is 200 with orderID or 200 with redirect link
     * **/
    fun updateOrder(context: Context, cartId: String, staffId: String, sellerCode: String, paymentMethod: PaymentMethod,
                    email: String, billingAddress: AddressInformation, callback: ApiResponseCallback<String>){
        val apiManager = HttpManagerMagento.getInstance(context)

        val paymentMethodBody = PaymentInfoBody.createPaymentInfoBody(cartId = cartId,
                staffId = staffId, retailerId = sellerCode, email = email, billingAddress = billingAddress, paymentMethod = paymentMethod.code)

        val httpUrl = HttpUrl.Builder()
                .scheme("https")
                .host(Constants.PWB_HOST_NAME)
                .addPathSegments("/rest/${apiManager.getLanguage()}/V1/guest-carts/$cartId/payment-information")
                .build()

        val json = Gson().toJson(paymentMethodBody)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)

        val request = Request.Builder()
                .url(httpUrl)
                .addHeader(HttpManagerMagento.OPEN_ORDER_CREATED_PAGE, Constants.CLIENT_MAGENTO)
                .post(requestBody)
                .build()

        apiManager.defaultHttpClient.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    try {
                        response.body().toString().toInt()
                        callback.success(response.body().toString())
                    } catch (e: Exception) {
                        if(response.code() == 200){
                            callback.success(HttpManagerMagento.OPEN_ORDER_CREATED_PAGE)
                        } else {
                            callback.failure(APIError(e))
                            Log.e("JSON Parser", "Error parsing data $e")
                        }
                    }
                } else {
                    callback.failure(APIError(response.code().toString(), response.message()))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.failure(APIError(e))
            }
        })
    }
}