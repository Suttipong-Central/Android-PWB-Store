package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.response.CompareProductResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CompareService {

    @GET("/rest/{lang}/V1/products/compare")
    fun getCompareProduct(@Header("Authorization") token: String,
                          @Header("client") client: String,
                          @Header("client-type") clientType: String,
                          @Header("retailer-id") retailerId: String,
                          @Path("lang") language: String,
                          @Query("sku") sku: String): Call<List<CompareProductResponse>>
}
