package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.ShippingSlot
import com.google.gson.annotations.SerializedName


data class BookingSlotBody (@SerializedName ("slot")
                            var shippingSlot: ShippingSlot)