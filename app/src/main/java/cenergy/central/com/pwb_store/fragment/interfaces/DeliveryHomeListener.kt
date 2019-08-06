package cenergy.central.com.pwb_store.fragment.interfaces

import cenergy.central.com.pwb_store.model.ShippingSlot

interface DeliveryHomeListener{
    fun onPaymentClickListener(shippingSlot: ShippingSlot, date: Int, month: Int, year: Int, shippingDate: String)
}