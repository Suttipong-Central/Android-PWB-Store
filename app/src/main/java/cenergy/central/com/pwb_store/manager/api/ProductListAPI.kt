package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.ProductListBody
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.ParsingUtils
import cenergy.central.com.pwb_store.utils.getResultError
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.ArrayList

class ProductListAPI {
    companion object{
        @JvmStatic
        fun retrieveProducts(context: Context, size: Int, currentPage: Int, filterGroups: ArrayList<FilterGroups>,
                             sortOrders: ArrayList<SortOrder>, callback: ApiResponseCallback<ProductResponse>){
            val apiManager = HttpManagerMagento.getInstance(context)
            val body = ProductListBody.createBody(size, currentPage, filterGroups, sortOrders)
            val httpUrl = HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.PWB_HOST_NAME)
                    .addPathSegments("rest/catalog-service/${apiManager.getLanguage()}/V1/products/search")
                    .build()

            val json = Gson().toJson(body)
            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)

            val request = Request.Builder()
                    .url(httpUrl)
                    .post(requestBody)
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