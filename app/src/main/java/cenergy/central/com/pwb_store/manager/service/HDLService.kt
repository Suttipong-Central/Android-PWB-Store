package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.body.ShippingSlotBody
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HDLService {
    @POST("/v1/logistics/shipment/list-shipping-slot")
    fun getShippingSlot(
            @Header("Content-Type") type: String,
            @Body shippingSlotBody: ShippingSlotBody): Call<ShippingSlotResponse>
}
