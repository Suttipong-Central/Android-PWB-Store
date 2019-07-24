package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.extensions.isPayWithCreditCard
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.response.PaymentMethod
import com.google.gson.annotations.SerializedName

data class PaymentInformationBody(
        var cartId: String = "",
        var paymentMethod: MethodBody,
        var email: String = "",
        var staffId: String = "",
        var storeId: String = "",
        var theOneCardNo: String = "")

data class MethodBody(var method: String,
                      @SerializedName("extension_attributes")
                      var extensionMethodBody: ExtensionMethodBody)

/**
 * @param customerEmail
 * @param customerName
 * @param customerPhone
 * those have to set with payment type full_payment or instalment for url redirect to 2c2p
 * */
data class ExtensionMethodBody(
        @SerializedName("t1c_earn_card_number")
        var theOneCardNo: String = "",
        @SerializedName("quote_staff")
        var quoteStaffBody: QuoteStaffBody? = null,
        @SerializedName("customer_email")
        var customerEmail: String = "",
        @SerializedName("customer_name")
        var customerName: String = "",
        @SerializedName("customer_phone")
        var customerPhone: String = "")

data class QuoteStaffBody(
        @SerializedName("staff_id")
        var staffId: String = "",
        @SerializedName("retailer_id")
        var retailerId: String = ""
)

data class PaymentInfoBody(
        var cartId: String = "",
        var email: String = "",
        @SerializedName("payment_method")
        var paymentMethod: MethodBody,
        var billingAddress: AddressInformation) {

    companion object {
        fun createPaymentInfoBody(cartId: String, customerEmail: String, paymentMethod: PaymentMethod,
                                  billingAddress: AddressInformation, staffId: String,
                                  retailerId: String, theOneCardNo: String = ""): PaymentInfoBody {
            val staffBody = if (paymentMethod.isPayWithCreditCard()) null else QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(theOneCardNo = theOneCardNo, quoteStaffBody = staffBody,
                    customerEmail = customerEmail, customerName = "${billingAddress.firstname} ${billingAddress.lastname}",
                    customerPhone = billingAddress.telephone)
            val methodBody = MethodBody(method = paymentMethod.code, extensionMethodBody = extMethodBody)
            return PaymentInfoBody(cartId = cartId, email = customerEmail, billingAddress = billingAddress, paymentMethod = methodBody)
        }
    }
}