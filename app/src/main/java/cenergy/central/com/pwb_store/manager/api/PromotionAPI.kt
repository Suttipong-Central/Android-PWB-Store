package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.response.PromotionResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PromotionAPI {
    companion object {
        fun retrievePromotion(context: Context, product: Product, callback: ApiResponseCallback<PromotionResponse>) {
            val apiManager = HttpManagerMagento.getInstance(context)
            apiManager.promotionService.getPromotionSuggestion(Constants.CLIENT_MAGENTO,
                    HttpManagerMagento.CLIENT_NAME_E_ORDERING, apiManager.getUserClientType(),
                    apiManager.getUserRetailerId(), apiManager.getLanguage(), product.sku).enqueue(object : Callback<PromotionResponse> {
                override fun onResponse(call: Call<PromotionResponse>, response: Response<PromotionResponse>) {
                    if (response.isSuccessful) {
                        callback.success(response.body())
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                }

                override fun onFailure(call: Call<PromotionResponse>, t: Throwable) {
                    callback.failure(APIError(t))
                }
            })
        }

        fun retrievePromotionBySKUs(context: Context, productSKUs: String, callback: ApiResponseCallback<List<PromotionResponse>>) {
            val apiManager = HttpManagerMagento.getInstance(context)
            apiManager.promotionService.getPromotionSuggestionBySKUs(Constants.CLIENT_MAGENTO,
                    HttpManagerMagento.CLIENT_NAME_E_ORDERING, apiManager.getUserClientType(),
                    apiManager.getUserRetailerId(), apiManager.getLanguage(), "sku", productSKUs, "in")
                    .enqueue(object : Callback<List<PromotionResponse>> {
                        override fun onResponse(call: Call<List<PromotionResponse>>, response: Response<List<PromotionResponse>>) {
                            if (response.isSuccessful) {
                                callback.success(response.body())
                            } else {
                                callback.failure(APIErrorUtils.parseError(response))
                            }
                        }

                        override fun onFailure(call: Call<List<PromotionResponse>>, t: Throwable) {
                            callback.failure(APIError(t))
                        }
                    })
        }
    }
}