package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CompareDao
import cenergy.central.com.pwb_store.model.CompareList
import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by napabhat on 11/8/2017 AD.
 */

interface CompareService {

    @GET("/rest/{lang}/V1/products/compare")
    fun getCompareProduct( @Header("Authorization") token: String,
                           @Path("lang") language: String,
                           @Query("sku") sku: String): Call<List<CompareProductResponse>>

    @GET("/rest/V1/products")
    fun getCompareProductList(
            @Query("searchCriteria[filterGroups][0][filters][0][field]") sku: String,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") skuId: String,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") `in`: String,
            @Query("searchCriteria[filterGroups][1][filters][0][field]") inStore: String,
            @Query("searchCriteria[filterGroups][1][filters][0][value]") storeId: String,
            @Query("searchCriteria[filterGroups][1][filters][0][conditionType]") finSet: String,
            @Query("searchCriteria[sortOrders][0][field]") skuSort: String): Call<CompareList>

    @GET("/rest/V2/compare")
    fun getCompareItem(
            @Query("sku") sku: String): Call<List<CompareDao>>
}
