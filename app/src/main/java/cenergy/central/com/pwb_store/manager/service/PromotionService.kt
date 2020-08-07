package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.response.PromotionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PromotionService {
    @GET("/rest/{lang}/V1/promotion-suggestion/product/{sku}")
    fun getPromotionSuggestion(@Header("Authorization") token: String,
                               @Header("client") client: String,
                               @Header("client_type") clientType: String,
                               @Path("lang") language: String,
                               @Path("sku") sku: String): Call<PromotionResponse>

    @GET("/rest/{lang}/V1/promotion-suggestion/product/")
    fun getPromotionSuggestionBySKUs(
            @Header("Authorization") token: String,
            @Header("client") client: String,
            @Header("client_type") clientType: String,
            @Path("lang") language: String,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") fieldSKU: String,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") valueSKUs: String,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") conditionType: String
    ): Call<List<PromotionResponse>>
}