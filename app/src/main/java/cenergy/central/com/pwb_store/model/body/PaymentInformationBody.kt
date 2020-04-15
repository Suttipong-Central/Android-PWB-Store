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
        var theOneCardNo: String = "",
        @SerializedName("quote_staff")
        var quoteStaffBody: QuoteStaffBody? = null,
        @SerializedName("customer_email")
        var customerEmail: String? = null,
        @SerializedName("customer_name")
        var customerName: String? = null,
        @SerializedName("customer_phone")
        var customerPhone: String? = null,
        @SerializedName("apm_agent_code")
        var agentCode: String? = null,
        @SerializedName("apm_channel_code")
        var agentChannelCode: String? = null)

data class QuoteStaffBody(
        @SerializedName("staff_id")
        var staffId: String = "",
        @SerializedName("retailer_id")
        var retailerId: String = ""
)

data class MethodBody(var method: String,
                      @SerializedName("extension_attributes")
                      var extensionMethodBody: ExtensionMethodBody)

data class PaymentInfoBody(
        var cartId: String = "",
        var email: String = "",
        var paymentMethod: MethodBody,
        var billingAddress: AddressInformation?) {

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
                                  billingAddress: AddressInformation, staffId: String,
                                  retailerId: String, theOneCardNo: String = ""): PaymentInfoBody {
            val staffBody = QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(
                    theOneCardNo = theOneCardNo,
                    quoteStaffBody = staffBody,
                    customerEmail = customerEmail,
                    customerName = billingAddress.getDisplayName(),
                    customerPhone = billingAddress.telephone)
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