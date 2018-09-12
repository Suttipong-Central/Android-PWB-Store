package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.AddressInformation

interface PaymentBillingListener{
    fun setShippingAddressInfo(shippingAddress: AddressInformation)
}