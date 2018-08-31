package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.LoginResponse
import cenergy.central.com.pwb_store.model.response.LogoutResponse
import cenergy.central.com.pwb_store.model.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("/api/login")
    fun userLogin(@Body userBody: UserBody): Call<LoginResponse>

//    @Headers("Accept: Application/json", "Content-Type: application/x-www-form-urlencoded")
    @POST("/api/details")
    fun retrieveUser(@Header("Authorization") userToken: String): Call<UserResponse>

//    @Headers("Accept: Application/json", "Content-Type: application/x-www-form-urlencoded")
    @GET("/api/logout")
    fun userLogout(@Header("Authorization") userToken: String): Call<LogoutResponse>
}