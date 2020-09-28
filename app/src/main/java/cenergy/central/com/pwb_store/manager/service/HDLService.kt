package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.body.BookingSlotBody
import cenergy.central.com.pwb_store.model.response.HDLMemberResponse
import retrofit2.Call
import retrofit2.http.*

interface HDLService {
    @GET("/v1/customers/{number}")
    fun getHDLMembers(@Path("number") number: String,
                      @Header("cxfTrace") cxfTrace: Boolean): Call<HDLMemberResponse>

    // mdc
    @PUT("/rest/V1/guest-carts/{cartId}/shipping-slot-hdl/book")
    fun createBooking(@Header("client") client: String,
                      @Header("client-type") clientType: String,
                      @Header("retailer-id") retailerId: String,
                      @Path("cartId") cartId: String,
                      @Body bookingSlotBody: BookingSlotBody): Call<ShippingSlot>
}
