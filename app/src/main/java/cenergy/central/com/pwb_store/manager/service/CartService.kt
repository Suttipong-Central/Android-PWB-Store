package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.body.*
import cenergy.central.com.pwb_store.model.response.CartResponse
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.model.response.PaymentInformationResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import retrofit2.Call
import retrofit2.http.*

interface CartService {

    @POST("/rest/{lang}/V1/guest-carts")
    fun createCart(@Header("client") client: String,
                   @Header("client-type") clientType: String,
                   @Path("lang") language: String): Call<String>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/items")
    fun addProduct(@Header("client") client: String,
                   @Header("client-type") clientType: String,
                   @Path("lang") language: String,
                   @Path("cartId") cartId: String,
                   @Body cartItemBody: CartItemBody): Call<CartItem>

    @GET("/rest/{lang}/V1/guest-carts/{cartId}")
    fun viewCart(@Header("client") client: String,
                 @Header("client-type") clientType: String,
                 @Path("lang") language: String,
                 @Path("cartId") quoteID: String): Call<CartResponse>

    @PUT("/rest/V1/guest-carts/{cartId}/coupons/{code}")
    fun addCoupon(@Header("client") client: String,
                  @Header("client-type") clientType: String,
                  @Path("cartId") cartId: String,
                  @Path("code") code: String): Call<Boolean>

    @DELETE("/rest/V1/guest-carts/{cartId}/coupons")
    fun deleteCoupon(@Header("client") client: String,
                     @Header("client-type") clientType: String,
                     @Path("cartId") cartId: String): Call<Boolean>

    @DELETE("/rest/V1/guest-carts/{cartId}/items/{itemId}")
    fun deleteItem(@Header("client") client: String,
                   @Header("client-type") clientType: String,
                   @Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long): Call<Boolean>

    @PUT("/rest/{lang}/V1/guest-carts/{cartId}/items/{itemId}")
    fun updateItem(@Header("client") client: String,
                   @Header("client-type") clientType: String,
                   @Path("lang") language: String,
                   @Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long,
                   @Body updateItemBody: UpdateItemBody): Call<CartItem>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/estimate-shipping-methods")
    fun getOrderDeliveryOptions(@Header("client") client: String,
                                @Header("client-type") clientType: String,
                                @Path("lang") language: String,
                                @Path("cartId") cartId: String,
                                @Body deliveryBody: DeliveryOptionsBody): Call<List<DeliveryOption>>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/shipping-information")
    fun createShippingInformation(@Header("client") client: String,
                                  @Header("client-type") clientType: String,
                                  @Path("lang") language: String,
                                  @Path("cartId") cartId: String,
                                  @Body shippingBody: ShippingBody): Call<ShippingInformationResponse>

    @GET("/rest/{lang}/V1/guest-carts/{cartId}/payment-information")
    fun retrievePaymentInformation(@Header("client") client: String,
                                   @Header("client-type") clientType: String,
                                   @Path("lang") language: String,
                                   @Path("cartId") cartId: String): Call<PaymentInformationResponse>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/payment-information")
    fun setPaymentInformation(@Header("client") client: String,
                              @Header("client-type") clientType: String,
                              @Path("lang") language: String,
                              @Path("cartId") cartId: String,
                              @Body paymentInformation: PaymentInfoBody): Call<String>

    @POST("/rest/{lang}/V1/guest-carts/{cartId}/set-payment-information")
    fun updatePaymentInformation(@Header("client") client: String,
                                 @Header("client-type") clientType: String,
                                 @Path("lang") language: String,
                                 @Path("cartId") cartId: String,
                                 @Body paymentInformation: PaymentInfoBody): Call<Boolean>

    @GET("/rest/{lang}/V1/orders/{orderId}")
    fun getOrder(@Header("Authorization") token: String,
                 @Header("client") client: String,
                 @Header("client-type") clientType: String,
                 @Path("lang") language: String,
                 @Path("orderId") orderId: String): Call<OrderResponse>
}
