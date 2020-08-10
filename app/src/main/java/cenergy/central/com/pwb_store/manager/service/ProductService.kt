package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.response.ProductSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("/rest/{lang}/V1/delivery-info/products/{sku}")
    fun getDeliveryInfo(
            @Header("client") client: String,
            @Header("client-type") clientType: String,
            @Path("lang") language: String,
            @Path("sku") sku: String): Call<List<DeliveryInfo>>
}
