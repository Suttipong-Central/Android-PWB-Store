package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import android.util.Log
import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.body.ProductListBody
import cenergy.central.com.pwb_store.model.body.SortOrder
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.realm.DatabaseListener
import cenergy.central.com.pwb_store.realm.RealmController
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.ParsingUtils
import cenergy.central.com.pwb_store.utils.getResultError
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.util.*

class ProductListAPI {
    companion object {
        //Pagination
        const val PER_PAGE = 20
        // Filter condition type
        const val FILTER_EQUAL = "eq"
        const val FILTER_GREATER_THAN = "gt"
        // Filter key/value
        const val FILTER_GROUP_FIELD_PRODUCT_1H = "expr-p"
        const val FILTER_VALUE_PRODUCT_1H = "(stock.salable=1 OR (stock.ispu_salable=1 AND shipping_methods='storepickup_ispu'))"
    }

    fun retrieveProducts(context: Context, isSearch: Boolean, searchTerm: String,
                         brandName: String? = null,
                         pageSize: Int, currentPage: Int,
                         sortOrders: ArrayList<SortOrder>, force: Boolean = false,
                         callback: ApiResponseCallback<ProductResponse>) {

        // setup filter
        val filterGroups = ArrayList<FilterGroups>()
        val searchTermKey = if (isSearch) "search_term" else "category_id"
        filterGroups.add(FilterGroups.createFilterGroups(searchTermKey, searchTerm, FILTER_EQUAL))
        if (BuildConfig.FLAVOR !== "cds") {
            filterGroups.add(FilterGroups.createFilterGroups(FILTER_GROUP_FIELD_PRODUCT_1H,
                    FILTER_VALUE_PRODUCT_1H, FILTER_EQUAL))
        }
        filterGroups.add(FilterGroups.createFilterGroups("status", "1", FILTER_EQUAL))
        filterGroups.add(FilterGroups.createFilterGroups("visibility", "4", FILTER_EQUAL))
        filterGroups.add(FilterGroups.createFilterGroups("price", "0", FILTER_GREATER_THAN))

        // TODO: un-hide market place seller
        filterGroups.add(FilterGroups.createFilterGroups("marketplace_seller", "null"))

        if (brandName != null && brandName.isNotEmpty()) {
            filterGroups.add(FilterGroups.createFilterGroups("brand", brandName, FILTER_EQUAL))
        }

        // Setup for call http
        val database = RealmController.getInstance()
        val apiManager = HttpManagerMagento.getInstance(context)
        // For cache endpoint
        val endpointName = "rest/catalog-service/${apiManager.getLanguage()}/V1/products/search?" +
                "$searchTermKey=$searchTerm/"

        // If already cached then do nothing
        val productResponse = database.getProductResponseByCategoryId(searchTerm)
        if (productResponse != null && searchTermKey == "category_id") {
            val localCurrentPage = productResponse.products.size / PER_PAGE
            val skip = currentPage > localCurrentPage

            if (!skip || (!force && database.hasFreshlyCachedEndpoint(endpointName, 1))) {
                Log.i("ProductListAPI", "retrieveProducts: using cached")
                //TODO: Fix about call api with sort
                callback.success(productResponse)
                return
            }
        }

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

        Log.i("ProductListAPI", "retrieveProducts: calling endpoint")
        apiManager.defaultHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                if (response != null) {
                    try {
                        val newProductResponse = ParsingUtils.parseToProductResponse(response)
                        
                        if (searchTermKey == "category_id") {
                            newProductResponse.categoryId = searchTerm
                            // cache product response
                            Log.i("ProductListAPI", "store product response!")
                            database.storeProductResponse(newProductResponse, object : DatabaseListener {
                                override fun onSuccessfully() {
                                    callback.success(database.getProductResponseByCategoryId(searchTerm))
                                }

                                override fun onFailure(error: Throwable) {
                                    callback.failure(error.getResultError())
                                }
                            })
                        }
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