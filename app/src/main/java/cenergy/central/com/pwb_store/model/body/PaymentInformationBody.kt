package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.AddressInformation
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

data class ExtensionMethodBody(
        @SerializedName("t1c_earn_card_number")
        var theOneCardNo: String = "",
        @SerializedName("quote_staff")
        var quoteStaffBody: QuoteStaffBody)

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
        fun createPaymentInfoBody(cartId: String, email: String, paymentMethod: String,
                                  billingAddress: AddressInformation, staffId: String,
                                  retailerId: String, theOneCardNo: String): PaymentInfoBody {
            val staffBody = QuoteStaffBody(staffId, retailerId)
            val extMethodBody = ExtensionMethodBody(theOneCardNo, staffBody)
            val methodBody = MethodBody(paymentMethod, extMethodBody)
            return PaymentInfoBody(cartId = cartId, email = email, billingAddress = billingAddress, paymentMethod = methodBody)
        }
    }
}