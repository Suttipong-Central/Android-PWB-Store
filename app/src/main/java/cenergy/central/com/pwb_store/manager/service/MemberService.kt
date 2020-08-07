package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.District
import cenergy.central.com.pwb_store.model.Member
import cenergy.central.com.pwb_store.model.Province
import cenergy.central.com.pwb_store.model.SubDistrict
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.PwbMemberResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MemberService {

    @GET("/v2/members/by-mobilephone")
    fun geT1CtMemberFromMobile(@Query("mobilePhone") mobilePhone: String,
                               @Query("mobileCountryCode") mobileCountryCode: String): Call<List<MemberResponse>>

    @GET("/v2/customer/{customerId}/extended")
    fun getT1CMember(@Path("customerId") customerId: String): Call<Member>

    // region MDC
    // get Provinces
    @GET("rest/{lang}/V1/region/province")
    fun getProvinces(@Header("Authorization") token: String,
                     @Header("client") client: String,
                     @Header("client_type") clientType: String,
                     @Path("lang") language: String) : Call<List<Province>>

    // get Districts
    @GET("rest/{lang}/V1/region/province/{provinceId}/district")
    fun getDistricts(@Header("Authorization") token: String,
                     @Header("client") client: String,
                     @Header("client_type") clientType: String,
                     @Path("lang") language: String,
                     @Path("provinceId") provinceId: String) : Call<List<District>>

    // get SubDistricts
    @GET("rest/{lang}/V1/region/province/{provinceId}/district/{districtId}/subdistrict")
    fun getSubDistricts(@Header("Authorization") token: String,
                        @Header("client") client: String,
                        @Header("client_type") clientType: String,
                        @Path("lang") language: String,
                        @Path("provinceId") provinceId: String,
                        @Path("districtId") districtId: String) : Call<List<SubDistrict>>
    // endregion
}
