package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.PaymentMethod
import com.google.gson.annotations.SerializedName

/**
 * @param customerEmail
 * @param customerName
 * @param customerPhone
 * those must set if payment type full_payment or instalment for url redirect to 2c2p
 *
 * @param agentCode
 * @param agentChannelCode
 * those must set if payment type p2c2p_123
 * */
data class ExtensionMethodBody(
        @SerializedName("t1c_earn_card_number")
        val theOneCardNo: String = "",
        @SerializedName("quote_staff")
        val quoteStaffBody: QuoteStaffBody? = null,
        @SerializedName("customer_email")
        val customerEmail: String? = null,
        @SerializedName("customer_name")
        val customerName: String? = null,
        @SerializedName("customer_phone")
        val customerPhone: String? = null,
        @SerializedName("apm_agent_code")
        val agentCode: String? = null,
        @SerializedName("apm_channel_code")
        val agentChannelCode: String? = null,
        @SerializedName("promotion_id")
        val promotionId: Int? = null)

data class QuoteStaffBody(
        @SerializedName("staff_id")
        val staffId: String = "",
        @SerializedName("retailer_id")
        val retailerId: String = ""
)

data class MethodBody(val method: String,
                      @SerializedName("extension_attributes")
                      val extensionMethodBody: ExtensionMethodBody)

data class PaymentInfoBody(
        val cartId: String = "",
        val email: String = "",
        val paymentMethod: MethodBody,
        val billingAddress: AddressInformation?) {

    companion object {
        fun createPaymentInfoBody(cartId: String, email: String, paymentMethod: PaymentMethod,
                                  billingAddress: AddressInformation, staffId: String,
                                  retailerId: String, theOneCardNo: String = ""): PaymentInfoBody {
            val staffBody = QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(theOneCardNo = theOneCardNo, quoteStaffBody = staffBody)
            val methodBody = MethodBody(method = paymentMethod.code, extensionMethodBody = extMethodBody)
            return PaymentInfoBody(cartId = cartId, email = email, billingAddress = billingAddress, paymentMethod = methodBody)
        }

        // payment body for full_payment, installment
        fun createPaymentInfoBody(cartId: String, email: String, customerEmail: String, paymentMethod: PaymentMethod,
                                  promotionId: Int? = null, billingAddress: AddressInformation, staffId: String,
                                  retailerId: String, theOneCardNo: String = ""): PaymentInfoBody {
            val staffBody = QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(
                    theOneCardNo = theOneCardNo,
                    quoteStaffBody = staffBody,
                    customerEmail = customerEmail,
                    customerName = billingAddress.getDisplayName(),
                    customerPhone = billingAddress.telephone,
                    promotionId = promotionId
            )
            val methodBody = MethodBody(method = paymentMethod.code, extensionMethodBody = extMethodBody)
            return PaymentInfoBody(cartId = cartId, email = email, billingAddress = billingAddress, paymentMethod = methodBody)
        }

        // payment body for p2c2p_123
        fun createPaymentInfoBody(cartId: String, email: String, payerName: String, customerEmail: String,
                                  agentCode: String, agentChannelCode: String, mobileNumber: String,
                                  paymentMethod: PaymentMethod, billingAddress: AddressInformation,
                                  staffId: String, retailerId: String,
                                  theOneCardNo: String = ""): PaymentInfoBody {
            val staffBody = QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(
                    theOneCardNo = theOneCardNo,
                    quoteStaffBody = staffBody,
                    customerEmail = customerEmail,
                    customerName = payerName,
                    customerPhone = mobileNumber,
                    agentCode = agentCode,
                    agentChannelCode = agentChannelCode)
            val methodBody = MethodBody(method = paymentMethod.code, extensionMethodBody = extMethodBody)
            return PaymentInfoBody(cartId = cartId, email = email, billingAddress = billingAddress, paymentMethod = methodBody)
        }
    }
}