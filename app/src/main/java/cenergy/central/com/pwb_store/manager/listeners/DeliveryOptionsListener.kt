package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.AddressInformation

interface DeliveryOptionsListener{
    fun onDeliveryOptions(shippingAddress: AddressInformation)
}