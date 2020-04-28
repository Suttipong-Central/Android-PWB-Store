package cenergy.central.com.pwb_store.manager.listeners

import cenergy.central.com.pwb_store.model.AddressInformation

interface PaymentBillingListener{
    fun saveAddressInformation(shippingAddress: AddressInformation, billingAddress: AddressInformation?,
                               t1cNumber: String, privacyVersion: String?, isCheckConsent: Boolean = false)
    fun setBillingAddressWithIspu(billingAddress: AddressInformation, t1cNumber: String,
                                  privacyVersion: String?, isCheckConsent: Boolean = false)
}