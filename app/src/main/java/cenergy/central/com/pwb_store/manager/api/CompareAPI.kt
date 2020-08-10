package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompareAPI {
    fun retrieveCompareProduct(context: Context, productSKUs: String, callback: ApiResponseCallback<List<CompareProductResponse>>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        apiManager.compareService.getCompareProduct(Constants.CLIENT_MAGENTO,
                HttpManagerMagento.CLIENT_NAME_E_ORDERING, apiManager.getUserClientType(),
                apiManager.getLanguage(), productSKUs).enqueue(object : Callback<List<CompareProductResponse>> {
            override fun onResponse(call: Call<List<CompareProductResponse>>, response: Response<List<CompareProductResponse>>) {
                if (response.isSuccessful) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<CompareProductResponse>>, t: Throwable) {
                callback.failure(APIError(t))
            }
        })
    }
}