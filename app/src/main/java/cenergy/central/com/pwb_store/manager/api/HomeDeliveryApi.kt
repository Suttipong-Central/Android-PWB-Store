package cenergy.central.com.pwb_store.manager.api

import android.content.Context
import cenergy.central.com.pwb_store.manager.ApiResponseCallback
import cenergy.central.com.pwb_store.manager.HttpManagerMagento
import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.body.BookingSlotBody
import cenergy.central.com.pwb_store.utils.APIErrorUtils
import cenergy.central.com.pwb_store.utils.getResultError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeDeliveryApi {
    fun createBookingSlot(context: Context, cartId: String, shippingSlot: ShippingSlot, callback: ApiResponseCallback<ShippingSlot>) {
        val apiManager = HttpManagerMagento.getInstance(context)
        val slotBody = BookingSlotBody(shippingSlot)
        apiManager.hdlService.createBooking(HttpManagerMagento.CLIENT_NAME_E_ORDERING, apiManager.getUserClientType(),
                apiManager.getUserRetailerId(), cartId, slotBody).enqueue(object : Callback<ShippingSlot> {
            override fun onResponse(call: Call<ShippingSlot>, response: Response<ShippingSlot>) {
                if (response.isSuccessful) {
                    callback.success(response.body())
                } else {
                    callback.failure(APIErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ShippingSlot>, t: Throwable) {
                callback.failure(t.getResultError())
            }
        })
    }
}