package cenergy.central.com.pwb_store.manager.service

import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.body.BookingShippingSlotBody
import cenergy.central.com.pwb_store.model.body.BookingSlotBody
import cenergy.central.com.pwb_store.model.body.ShippingSlotBody
import cenergy.central.com.pwb_store.model.response.BookingNumberResponse
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse
import retrofit2.Call
import retrofit2.http.*

interface HDLService {
    @POST("/v1/logistics/shipment/list-shipping-slot")
    fun getShippingSlot(
            @Header("Content-Type") type: String,
            @Body shippingSlotBody: ShippingSlotBody): Call<ShippingSlotResponse>

    @POST("/v1/logistics/shipment/booking")
    fun createBooking(
            @Header("Content-Type") type: String,
            @Body bookingShippingSlotBody: BookingShippingSlotBody): Call<BookingNumberResponse>

    @PUT("/rest/V1/guest-carts/{cartId}/shipping-slot-hdl/book")
    fun createBooking(@Path("cartId") cartId: String,
                      @Body bookingSlotBody: BookingSlotBody): Call<ShippingSlot>
}
