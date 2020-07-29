package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.model.*
import cenergy.central.com.pwb_store.model.ShippingSlot
import cenergy.central.com.pwb_store.model.response.*
import java.util.*

interface PaymentProtocol {
    fun getItems(): List<ShoppingCartItem>
    fun getTotalPrice(): Double
    fun getDiscount(): Double
    fun getPromotionDiscount(): Double
    fun getHDLMembers(): List<HDLCustomerInfos>
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
    fun getPaymentAgents(): List<PaymentAgent>
    fun getPaymentPromotions(): List<PaymentCreditCardPromotion>
    fun getConsentInfo(): ConsentInfoResponse?
    fun getCartTotal(): CartTotal?
    fun getCacheItems(): List<CacheCartItem>

    fun setPaymentInformation(paymentMethod: PaymentMethod, promotionId: Int?)
    fun updatePaymentInformation(paymentMethod: PaymentMethod, promotionId: Int?)
}