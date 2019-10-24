package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.activity.CheckoutType
import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.response.BranchResponse
import cenergy.central.com.pwb_store.model.response.CartTotalResponse
import cenergy.central.com.pwb_store.model.response.MemberResponse
import cenergy.central.com.pwb_store.model.response.PaymentMethod
import java.util.*

/**
 * Created by Anuphap Suwannamas on 12/9/2018 AD.
 * Email: Anupharpae@gmail.com
 */

interface PaymentProtocol {
    fun getItems(): List<CartItem>
    fun getCartTotalResponse(): CartTotalResponse
    fun getPWBMembers(): List<EOrderingMember>
    fun getMembers(): List<MemberResponse>
    fun getDeliveryOptions(): List<DeliveryOption>
    fun getShippingAddress(): AddressInformation?
    fun getBillingAddress(): AddressInformation?
    fun getSelectedDeliveryType(): DeliveryType?
    fun getEnableDateShipping(): ArrayList<ShippingSlot>
    fun getBranches(): ArrayList<BranchResponse>
    fun getSelectedBranch(): Branch?
    fun getPWBMemberByIndex(index: Int): EOrderingMember?
    fun getPaymentMethods(): List<PaymentMethod>
    fun getT1CardNumber(): String
    fun getCheckType(): CheckoutType
}