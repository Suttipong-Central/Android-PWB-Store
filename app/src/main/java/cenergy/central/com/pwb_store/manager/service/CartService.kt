package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import retrofit2.Call
import retrofit2.http.*

internal interface CartService {

    @POST("/rest/{lang}/V1/guest-carts")
    fun createCart(@Path("lang") language: String): Call<String>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/items")
    fun addProduct(@Path("lang") language: String,
                   @Path("cartId") cartId: String,
                   @Body cartItemBody: CartItemBody): Call<CartItem>

    @GET("/rest/{lang}/V1/guest-carts/{quoteId}/items")
    fun viewCart(@Path("lang") language: String,
                 @Path("quoteId") quoteID: String): Call<List<CartItem>>

    @DELETE("/rest/V1/guest-carts/{cartId}/items/{itemId}")
    fun deleteItem(@Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long): Call<Boolean>

    @PUT("/rest/{lang}/V1/guest-carts/{cartId}/items/{itemId}")
    fun updateItem(@Path("lang") language: String,
                   @Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long,
                   @Body updateItemBody: UpdateItemBody): Call<CartItem>

//    @POST("/rest/{lang}/V1/headless/guest-carts/{cartId}/estimate-shipping-methods")
    @POST("/rest/{lang}/V1/guest-carts/{cartId}/estimate-shipping-methods")
    fun getOrderDeliveryOptions(@Path("lang") language: String,
                                @Path("cartId") cartId: String,
                                @Body deliveryBody: DeliveryOptionsBody): Call<List<DeliveryOption>>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/shipping-information")
    fun createShippingInformation(@Path("lang") language: String,
                                  @Path("cartId") cartId: String,
                                  @Body shippingBody: ShippingBody): Call<ShippingInformationResponse>

    //    @PUT("/rest/V1/guest-carts/{cartId}/order")
    @POST("/rest/{lang}/V1/headless/guest-carts/{cartId}/payment-information")
    fun updateOrder(@Path("lang") language: String,
                    @Path("cartId") cartId: String,
                    @Body paymentInformation: PaymentInformationBody): Call<String>

    // @PUT("/rest/V1/guest-carts/{cartId}/order")
    @POST("/rest/{lang}/V1/guest-carts/{cartId}/payment-information")
    fun updateOrder(@Path("lang") language: String,
                    @Path("cartId") cartId: String,
                    @Body paymentInformation: PaymentInfoBody): Call<String>

    @GET("/rest/{lang}/V1/orders/{orderId}")
    fun getOrder(
            @Header("Authorization") token: String,
            @Path("lang") language: String,
            @Path("orderId") orderId: String): Call<OrderResponse>

    @GET("/rest/{lang}/all/V1/headless/storepickup")
    fun getBranches(@Path("lang") language: String,
                    @Query("searchCriteria[sortOrders][0][field]") orderBy: String,
                    @Query("searchCriteria[sortOrders][0][direction]") direction: String): Call<BranchResponse>
}
