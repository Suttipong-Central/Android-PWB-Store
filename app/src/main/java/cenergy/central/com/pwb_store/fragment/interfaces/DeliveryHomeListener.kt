package cenergy.central.com.pwb_store.fragment.interfaces

import cenergy.central.com.pwb_store.model.response.Slot

interface DeliveryHomeListener{
    fun onPaymentClickListener(slot: Slot, date: Int, month: Int, year: Int, shippingDate: String)
}