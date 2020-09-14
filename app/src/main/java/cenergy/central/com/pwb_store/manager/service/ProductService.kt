package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.response.ProductAvailableResponse
import cenergy.central.com.pwb_store.model.response.ProductSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/rest/{lang}/V1/products")
    fun getProductByBarcode(
            @Header("Authorization") token: String,
            @Path("lang") language: String,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") barCodeName: String,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") barCode: String,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") eq: String): Call<ProductSearchResponse>

    @GET("/rest/{lang}/V1/products")
    fun getProductByProductJda(
            @Header("Authorization") token: String,
            @Path("lang") language: String,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") jdaName: String,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") jda: String,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") eq: String): Call<ProductSearchResponse>

    @GET("/rest/{lang}/V1/delivery-info/products/{sku}")
    fun getDeliveryInfo(
            @Path("lang") language: String,
            @Path("sku") sku: String): Call<List<DeliveryInfo>>

    @GET("/rest/V1/storepickup/product-availability/{retailer}")
    fun getProductAvailable(
            @Path("retailer") retailerId: Int,
            @Query("searchCriteria[filter_groups][0][filters][0][field]") sku: String,
            @Query("searchCriteria[filter_groups][0][filters][0][value]") skus: String
    ): Call<List<ProductAvailableResponse>>
}
