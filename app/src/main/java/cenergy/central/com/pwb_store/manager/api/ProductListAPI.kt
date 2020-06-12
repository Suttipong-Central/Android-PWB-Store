package cenergy.central.com.pwb_store.manager.api

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.StoreAvailable
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.ProductListBody
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.ParsingUtils
import cenergy.central.com.pwb_store.utils.getResultError
import com.google.gson.Gson
import kotlinx.android.synthetic.pwbOmniTv.fragment_detail.*
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.ArrayList

class ProductListAPI {
    companion object{
        @JvmStatic
        fun retrieveProducts(context: Context, pageSize: Int, currentPage: Int, filterGroups: ArrayList<FilterGroups>,
                             sortOrders: ArrayList<SortOrder>, callback: ApiResponseCallback<ProductResponse>){
            val apiManager = HttpManagerMagento.getInstance(context)
            val body = ProductListBody.createBody(pageSize, currentPage, filterGroups, sortOrders)
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
                            val productResponse = ParsingUtils.parseToProductResponse(response)
                            //TODO Display available here later on OmniTV
//                            if (BuildConfig.FLAVOR != "pwbOmniTv"){
//                                callback.success(productResponse)
//                            } else {
//                                checkAvailableStore(context, productResponse, callback)
//                            }
                            callback.success(productResponse)
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

//        fun checkAvailableStore(context: Context, productResponse: ProductResponse, callback: ApiResponseCallback<ProductResponse>) {
//            var count = 0
//            productResponse.products.forEach { product ->
//                HttpManagerMagento.getInstance(context).getAvailableStore(product.sku,
//                        object : ApiResponseCallback<List<StoreAvailable>> {
//                            override fun success(response: List<StoreAvailable>?) {
//                                (context as Activity).runOnUiThread {
//                                    count += 1
//                                    product.availableThisStore = handleAvailableHere(response)
//                                    Log.d("count Available", "count $count size ${productResponse.products.size} sku ${product.sku}")
//                                    if (count == productResponse.products.size){
//                                        count = 0
//                                        callback.success(productResponse)
//                                    }
//                                }
//                            }
//
//                            override fun failure(error: APIError) {
//                                (context as Activity).runOnUiThread {
//                                    count += 1
//                                    Log.d("count Available", "count $count size ${productResponse.products.size}")
//                                    if (count == productResponse.products.size){
//                                        count = 0
//                                        callback.success(productResponse)
//                                    }
//                                }
//                            }
//                        })
//            }
//        }

        fun handleAvailableHere(listStoreAvailable: List<StoreAvailable>?) : Boolean{
            val userInformation = RealmController.getInstance().userInformation
            val retailerId = userInformation?.store?.retailerId
            var stockCurrentStore = false
            if (retailerId != null && listStoreAvailable != null) {
                val store = listStoreAvailable.firstOrNull { it.sellerCode == retailerId }
                if (store != null) {
                    stockCurrentStore = store.qty > 0
                }
            }
            return stockCurrentStore
        }
    }
}