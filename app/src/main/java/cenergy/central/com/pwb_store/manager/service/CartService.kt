package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.body.CartItemBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface CartService {

    @POST("/rest/V1/guest-carts")
    fun createCart(): Call<String>

    @POST("/rest/V1/guest-carts/{cartId}/items")
    fun addProduct(@Path("cartId") cartId: String,
                   @Body cartItemBody: CartItemBody
    ): Call<CartItem>

    @GET("/rest/V1/guest-carts/{quote_id}/items")
    fun viewCart(
            @Path("quote_id") quoteID: String
    ): Call<List<CartItem>>
}
