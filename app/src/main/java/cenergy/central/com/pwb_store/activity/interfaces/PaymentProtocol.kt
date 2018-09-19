package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.CartItem
import cenergy.central.com.pwb_store.model.DeliveryOption
import cenergy.central.com.pwb_store.model.DeliveryType
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.ShippingSlotResponse

/**
 * Created by Anuphap Suwannamas on 12/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface PaymentProtocol {
    fun getItems(): List<CartItem>
    fun getMembers(): List<MemberResponse>
    fun getDeliveryOptions(): List<DeliveryOption>
    fun getShippingAddress(): AddressInformation?
    fun getSelectedDeliveryType(): DeliveryType?
    fun getShippingSlot(): ShippingSlotResponse?

    fun retrieveStores()
}