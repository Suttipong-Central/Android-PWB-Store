package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.response.OfflinePriceItem
import cenergy.central.com.pwb_store.model.response.OfflinePriceProductsResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class OfflinePriceAPI {
    companion object{
        @JvmStatic
        fun retrieveOfflinePriceProducts(context: Context, productIds: String, retailerId: String, callback: ApiResponseCallback<OfflinePriceProductsResponse>){
            val apiManager = HttpManagerMagento.getInstance(context)
            val httpUrl = HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.PWB_HOST_NAME)
                    .addPathSegments("rest/${apiManager.getLanguage()}/V1/pricing-per-store/priceperstore/search")
                    .addQueryParameter("searchCriteria[filterGroups][0][filters][0][field]", "product_id")
                    .addQueryParameter("searchCriteria[filterGroups][0][filters][0][value]", productIds)
                    .addQueryParameter("searchCriteria[filterGroups][0][filters][0][condition_type]", "in")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][field]", "retailer_id")
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][value]", retailerId)
                    .addQueryParameter("searchCriteria[filterGroups][1][filters][0][condition_type]", "in")
                    .build()

            val request = Request.Builder()
                    .url(httpUrl)
                    .build()

            apiManager.defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                    if (response != null) {
                        try {
                            val data = response.body()
                            val offlinePriceProductsResponse = OfflinePriceProductsResponse()
                            val offlinePriceResponseObj = JSONObject(data?.string())
                            val offlinePriceArray = offlinePriceResponseObj.getJSONArray("items")
                            for (i in 0 until offlinePriceArray.length()){
                                val offlinePriceObj = offlinePriceArray.getJSONObject(i)
                                val offlinePriceItem = OfflinePriceItem()
                                if (offlinePriceObj.has("entity_id")){
                                    offlinePriceItem.entityId = offlinePriceObj.getString("entity_id")
                                }
                                if (offlinePriceObj.has("product_id")){
                                    offlinePriceItem.productId = offlinePriceObj.getString("product_id")
                                }
                                if (offlinePriceObj.has("price")){
                                    offlinePriceItem.price = offlinePriceObj.getDouble("price")
                                }
                                if (offlinePriceObj.has("special_price")){
                                    offlinePriceItem.specialPrice = offlinePriceObj.getDouble("special_price")
                                }
                                if (offlinePriceObj.has("special_price_start")){
                                    offlinePriceItem.specialFromDate = offlinePriceObj.getString("special_price_start")
                                }
                                if (offlinePriceObj.has("special_price_end")){
                                    offlinePriceItem.specialToDate = offlinePriceObj.getString("special_price_end")
                                }
                                if (offlinePriceObj.has("retailer_id")){
                                    offlinePriceItem.retailerId = offlinePriceObj.getString("retailer_id")
                                }
                                if (offlinePriceObj.has("product_sku")){
                                    offlinePriceItem.productSku = offlinePriceObj.getString("product_sku")
                                }
                                if (offlinePriceObj.has("created_at")){
                                    offlinePriceItem.createdAt = offlinePriceObj.getString("created_at")
                                }
                                if (offlinePriceObj.has("updated_at")){
                                    offlinePriceItem.updatedAt = offlinePriceObj.getString("updated_at")
                                }
                                offlinePriceProductsResponse.items.add(offlinePriceItem)
                            }
                            callback.success(offlinePriceProductsResponse)
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

        fun retrieveOfflinePriceBySKU(context: Context, sku: String, retailerId: String, callback: ApiResponseCallback<OfflinePriceItem>){
            val apiManager = HttpManagerMagento.getInstance(context)
            val httpUrl = HttpUrl.Builder()
                    .scheme("https")
                    .host(Constants.PWB_HOST_NAME)
                    .addPathSegments("rest/${apiManager.getLanguage()}/V1/pricing-per-store/get-price")
                    .addQueryParameter("sku", sku)
                    .addQueryParameter("retailerId", retailerId)
                    .build()

            val request = Request.Builder()
                    .url(httpUrl)
                    .build()

            apiManager.defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                    if (response != null) {
                        try {
                            val data = response.body()
                            val offlinePriceItem = OfflinePriceItem()
                            val offlinePriceObj = JSONObject(data?.string())
                            if (offlinePriceObj.has("entity_id")){
                                offlinePriceItem.entityId = offlinePriceObj.getString("entity_id")
                            }
                            if (offlinePriceObj.has("product_id")){
                                offlinePriceItem.productId = offlinePriceObj.getString("product_id")
                            }
                            if (offlinePriceObj.has("price")){
                                offlinePriceItem.price = offlinePriceObj.getDouble("price")
                            }
                            if (offlinePriceObj.has("special_price")){
                                offlinePriceItem.specialPrice = offlinePriceObj.getDouble("special_price")
                            }
                            if (offlinePriceObj.has("special_price_start")){
                                offlinePriceItem.specialFromDate = offlinePriceObj.getString("special_price_start")
                            }
                            if (offlinePriceObj.has("special_price_end")){
                                offlinePriceItem.specialToDate = offlinePriceObj.getString("special_price_end")
                            }
                            if (offlinePriceObj.has("retailer_id")){
                                offlinePriceItem.retailerId = offlinePriceObj.getString("retailer_id")
                            }
                            if (offlinePriceObj.has("product_sku")){
                                offlinePriceItem.productSku = offlinePriceObj.getString("product_sku")
                            }
                            if (offlinePriceObj.has("created_at")){
                                offlinePriceItem.createdAt = offlinePriceObj.getString("created_at")
                            }
                            if (offlinePriceObj.has("updated_at")){
                                offlinePriceItem.updatedAt = offlinePriceObj.getString("updated_at")
                            }
                            callback.success(offlinePriceItem)
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