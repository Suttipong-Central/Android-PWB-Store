package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("/rest/V1/integration/admin/token")
    fun userLogin(@Body userBody: UserBody): Call<String>

    @GET("/rest/V1/headless/token/{token}/details")
    fun retrieveUser(@Path("token") token: String): Call<UserResponse>

    @GET("/api/logout")
    fun userLogout(@Header("Authorization") userToken: String): Call<LogoutResponse>

    @GET("/rest/V1/e-ordering/staff")
    fun retrieveUserId(@Header("Authorization") userToken: String): Call<LoginUserResponse>

    @GET("/rest/V1/e-ordering/retailers")
    fun retrieveStoreUser(@Header("Authorization") userToken: String): Call<UserBranch>

    @GET("rest/{lang}/V1/storelocator")
    fun retrieveStoreLocation(@Path("lang") language: String,
                              @Query("criteria[filter_groups][0][filters][0][value]") sellerCode: String,
                              @Query("criteria[filter_groups][0][filters][0][field]") field: String): Call<StoreLocationResponse>
}