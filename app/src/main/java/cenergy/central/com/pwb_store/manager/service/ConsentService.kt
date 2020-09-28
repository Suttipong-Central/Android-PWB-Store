package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.ConsentBody
import cenergy.central.com.pwb_store.model.response.ConsentInfoResponse
import cenergy.central.com.pwb_store.model.response.ConsentResponse
import retrofit2.Call
import retrofit2.http.*

interface ConsentService {
    @GET("/consent_info")
    fun getConsentInfo(
            @Header("client") client: String,
            @Header("client-type") clientType: String,
            @Header("retailer-id") retailerId: String,
            @Query("channel") channel: String,
            @Query("partner") partner: String,
            @Header("Content-Type") type: String,
            @Header("x-api-key") key: String): Call<ConsentInfoResponse>

    @GET("/member/consent_info")
    fun getConsentInfoStaging(
            @Header("client") client: String,
            @Header("client-type") clientType: String,
            @Header("retailer-id") retailerId: String,
            @Query("channel") channel: String,
            @Query("partner") partner: String,
            @Header("Content-Type") type: String,
            @Header("x-api-key") key: String): Call<ConsentInfoResponse>

    @POST("/consent")
    fun setConsent(
            @Header("client") client: String,
            @Header("client-type") clientType: String,
            @Header("retailer-id") retailerId: String,
            @Header("Content-Type") type: String,
            @Header("x-api-key") key: String,
            @Body consentBody: ConsentBody): Call<ConsentResponse>

    @POST("/member/consent")
    fun setConsentStaging(
            @Header("client") client: String,
            @Header("client-type") clientType: String,
            @Header("retailer-id") retailerId: String,
            @Header("Content-Type") type: String,
            @Header("x-api-key") key: String,
            @Body consentBody: ConsentBody): Call<ConsentResponse>
}