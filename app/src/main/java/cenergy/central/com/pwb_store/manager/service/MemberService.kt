package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.PwbMember
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.PwbMemberResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface MemberService {

    @GET("/v2/members/by-mobilephone")
    fun geT1CtMemberFromMobile(@Query("mobilePhone") mobilePhone: String,
                               @Query("mobileCountryCode") mobileCountryCode: String): Call<List<MemberResponse>>

    @GET("/v2/customer/{customerId}/extended")
    fun getT1CMember(@Path("customerId") customerId: String): Call<Member>


    // get PWB customer
    @GET("rest/V1/headless/customers/{telephone}")
    fun getCustomer(@Path("telephone") telephone: String): Call<PwbMemberResponse>
}
