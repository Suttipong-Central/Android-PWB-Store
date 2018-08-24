package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.body.CartItemBody
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
}
