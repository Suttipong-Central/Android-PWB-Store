package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.UserBody
import cenergy.central.com.pwb_store.model.response.LoginResponse
import cenergy.central.com.pwb_store.model.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HEAD
import retrofit2.http.POST

interface UserService {

    @POST("/api/login")
    fun userLogin(@Body userBody: UserBody): Call<LoginResponse>


}