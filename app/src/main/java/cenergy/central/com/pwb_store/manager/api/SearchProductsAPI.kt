package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.ParsingUtils
import cenergy.central.com.pwb_store.utils.getResultError
import okhttp3.HttpUrl
import okhttp3.Request
import java.io.IOException

class SearchProductsAPI {
    companion object{
        @JvmStatic
        fun retrieveProductsFromSearch(context: Context, pageSize: Int, currentPage: Int, twoHourField: String,
                                       twoHourValue: String, keyword: String, sortName: String,
                                       sortType: String, callback: ApiResponseCallback<ProductResponse>) {
            val apiManager = HttpManagerMagento.getInstance(context)
            val httpUrl = HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.DATALAKE_HOST_NAME)
                    .addPathSegments("search-service/${apiManager.getLanguage()}/products")
                    .addQueryParameter("searchCriteria[pageSize]", "$pageSize")
                    .addQueryParameter("searchCriteria[currentPage]", "$currentPage")
                    .addQueryParameter("searchCriteria[filter_groups][0][filters][0][field]", "status")
                    .addQueryParameter("searchCriteria[filter_groups][0][filters][0][value]", "1")
                    .addQueryParameter("searchCriteria[filter_groups][1][filters][0][field]", "visibility")
                    .addQueryParameter("searchCriteria[filter_groups][1][filters][0][value]", "4")
                    .addQueryParameter("searchCriteria[filter_groups][2][filters][0][field]", twoHourField)
                    .addQueryParameter("searchCriteria[filter_groups][2][filters][0][value]", twoHourValue)
                    .addQueryParameter("searchCriteria[filter_groups][3][filters][0][field]", "price")
                    .addQueryParameter("searchCriteria[filter_groups][3][filters][0][value]", "0")
                    .addQueryParameter("searchCriteria[filter_groups][3][filters][0][condition_type]", "gt")
                    .addQueryParameter("searchCriteria[filter_groups][4][filters][0][field]", "search_term")
                    .addQueryParameter("searchCriteria[filter_groups][4][filters][0][value]", keyword)
                    .addQueryParameter("searchCriteria[filter_groups][4][filters][0][condition_type]", "eq")
                    .addQueryParameter("searchCriteria[sortOrders][0][field]", sortName)
                    .addQueryParameter("searchCriteria[sortOrders][0][direction]", sortType)
                    .build()

            val request = Request.Builder()
                    .url(httpUrl)
                    .addHeader(Constants.CONTENT_TYPE, Constants.APPTICATION_JSON)
                    .addHeader(Constants.DATALAKE_X_SUBJECT_ID_HEADER, Constants.DATALAKE_X_SUBJECT_ID_VALUE)
                    .addHeader(Constants.DATALAKE_X_API_KEY_HEADER, Constants.DATALAKE_X_API_KEY_VALUE)
                    .build()

            apiManager.defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                    if (response != null) {
                        try {
                            callback.success(ParsingUtils.parseToProductResponse(response))
                        } catch (e: Exception) {
                            callback.failure(e.getResultError())
                            Log.e("JSON Parser", "Error parsing data $e")
                        }
                    } else {
                        callback.failure(APIErrorUtils.parseError(response))
                    }
                    response?.close()
                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    callback.failure(e.getResultError())
                }
            })
        }
    }
}