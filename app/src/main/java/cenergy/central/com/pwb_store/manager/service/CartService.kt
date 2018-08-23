package cenergy.central.com.pwb_store.manager.service

import retrofit2.Call
import retrofit2.http.POST

internal interface CartService {

    @POST("/rest/V1/guest-carts")
    fun createCart(): Call<String>


}
