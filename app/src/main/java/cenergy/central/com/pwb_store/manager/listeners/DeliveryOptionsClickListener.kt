package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.DeliveryOption

interface DeliveryOptionsClickListener{
    fun onExpressOrStandardClickListener(deliveryOptions: DeliveryOption)
    fun onHomeClickListener(deliveryOptions: DeliveryOption)
    fun onStorePickUpClickListener(deliveryOptions: DeliveryOption)
}