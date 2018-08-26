package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.body.CartItemBody
import cenergy.central.com.pwb_store.model.body.PaymentMethodBody
import cenergy.central.com.pwb_store.model.body.ShippingBody
import cenergy.central.com.pwb_store.model.body.UpdateItemBody
import cenergy.central.com.pwb_store.model.response.OrderResponse
import cenergy.central.com.pwb_store.model.response.ShippingInformationResponse
import retrofit2.Call
import retrofit2.http.*

internal interface CartService {

    @POST("/rest/V1/guest-carts")
    fun createCart(): Call<String>

    @POST("/rest/V1/guest-carts/{cartId}/items")
    fun addProduct(@Path("cartId") cartId: String,
                   @Body cartItemBody: CartItemBody): Call<CartItem>

    @GET("/rest/V1/guest-carts/{quoteId}/items")
    fun viewCart(
            @Path("quoteId") quoteID: String): Call<List<CartItem>>

    @DELETE("/rest/V1/guest-carts/{cartId}/items/{itemId}")
    fun deleteItem(@Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long): Call<Boolean>

    @PUT("/rest/V1/guest-carts/{cartId}/items/{itemId}")
    fun updateItem(@Path("cartId") cartId: String,
                   @Path("itemId") itemId: Long,
                   @Body updateItemBody: UpdateItemBody): Call<CartItem>

    @POST("/rest/V1/guest-carts/{cartId}/shipping-information")
    fun createShippingInformation(@Path("cartId") cartId: String,
                                  @Body shippingBody: ShippingBody): Call<ShippingInformationResponse>

    @PUT("/rest/V1/guest-carts/{cartId}/order")
    fun updateOrder(@Path("cartId") cartId: String,
                    @Body paymentMethod: PaymentMethodBody): Call<String>

    @GET("/rest/V1/orders/{orderId}")
    fun getOrder(@Path("cartId") cartId: String): Call<OrderResponse>
}
