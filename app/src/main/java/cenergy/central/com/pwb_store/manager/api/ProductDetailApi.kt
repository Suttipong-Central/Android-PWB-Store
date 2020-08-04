package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.Constants
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.service.ProductService
import cenergy.central.com.pwb_store.model.APIError
import cenergy.central.com.pwb_store.model.OfflinePriceItem
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.body.FilterGroups
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.model.response.ProductSearchResponse
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailApi {
    /**
     * @param lang = {{store}}
     * @param sku = {{sku}}
     *
     * */
    fun getPath(lang: String, sku: String): String {
        return "rest/$lang/V2/products/$sku"
    }

    fun getProductByBarcode(context: Context, barcode: String, callback: ApiResponseCallback<Product?>) {
        val filterGroupsList = ArrayList<FilterGroups>()
        filterGroupsList.add(FilterGroups.createFilterGroups("barcode", barcode, FilterGroups.CONDITION_EQUAL))
        filterGroupsList.addAll(FilterGroups.getDefaultFilterGroup())
        ProductListAPI.retrieveProducts(context, 1, 1, filterGroupsList, arrayListOf(), object : ApiResponseCallback<ProductResponse>{
            override fun success(response: ProductResponse?) {
                if (response != null && response.products.isNotEmpty()){
                    callback.success(response.products[0])
                } else {
                    callback.success(null)
                }
            }

            override fun failure(error: APIError) {
                callback.failure(error)
            }
        })
    }

    fun getProductByProductJda(context: Context, jda: String, callback: ApiResponseCallback<Product?>) {
        val filterGroupsList = ArrayList<FilterGroups>()
        filterGroupsList.add(FilterGroups.createFilterGroups("jda_sku", jda, FilterGroups.CONDITION_EQUAL))
        filterGroupsList.addAll(FilterGroups.getDefaultFilterGroup())
        ProductListAPI.retrieveProducts(context, 1, 1, filterGroupsList, arrayListOf(), object : ApiResponseCallback<ProductResponse>{
            override fun success(response: ProductResponse?) {
                if (response != null && response.products.isNotEmpty()){
                    callback.success(response.products[0])
                } else {
                    callback.success(null)
                }
            }

            override fun failure(error: APIError) {
                callback.failure(error)
            }
        })
    }
}