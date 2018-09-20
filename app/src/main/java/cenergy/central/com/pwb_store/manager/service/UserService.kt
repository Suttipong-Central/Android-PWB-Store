package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.LoginResponse
import cenergy.central.com.pwb_store.model.response.LogoutResponse
import cenergy.central.com.pwb_store.model.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("/rest/V1/integration/admin/token")
    fun userLogin(@Body userBody: UserBody): Call<String>

    @GET("/rest/V1/headless/token/{token}/details")
    fun retrieveUser(@Path("token") token: String): Call<UserResponse>

    @GET("/api/logout")
    fun userLogout(@Header("Authorization") userToken: String): Call<LogoutResponse>
}