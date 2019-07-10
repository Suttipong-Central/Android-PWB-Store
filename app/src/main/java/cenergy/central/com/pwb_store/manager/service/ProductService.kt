package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.DeliveryInfo
import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductDao
import cenergy.central.com.pwb_store.model.ProductDetail
import cenergy.central.com.pwb_store.model.ProductDetailDao
import cenergy.central.com.pwb_store.model.body.ProductListBody
import cenergy.central.com.pwb_store.model.response.BrandResponse
import cenergy.central.com.pwb_store.model.response.ProductByBarcodeResponse
import cenergy.central.com.pwb_store.model.response.ProductResponse
import cenergy.central.com.pwb_store.model.response.ProductSearchResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/rest/{lang}/V1/products")
    fun getProductFromBarcode(
            @Header("Authorization") token: String,
            @Path("lang") language: String,
            @Query("searchCriteria[filterGroups][0][filters][0][field]") barCodeName: String,
            @Query("searchCriteria[filterGroups][0][filters][0][value]") barCode: String,
            @Query("searchCriteria[filterGroups][0][filters][0][conditionType]") eq: String): Call<ProductByBarcodeResponse>

    @GET("/rest/{lang}/V1/delivery-info/products/{sku}")
    fun getDeliveryInfo(
            @Path("lang") language: String,
            @Path("sku") sku: String): Call<List<DeliveryInfo>>
}
