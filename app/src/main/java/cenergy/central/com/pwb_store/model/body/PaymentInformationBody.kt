package cenergy.central.com.pwb_store.model.body

import cenergy.central.com.pwb_store.model.AddressInformation
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class PaymentInformationBody(
        var cartId: String = "",
        var paymentMethod: MethodBody,
        var email: String = "",
        var staffId: String = "",
        var storeId: String = "",
        var theOneCardNo: String = "")

data class MethodBody(var method: String)

data class PaymentInfoBody(
        var cartId: String = "",
        var paymentMethod: MethodBody,
        var email: String = "",
        var billingAddress: AddressInformation,
        @SerializedName("staff_id")
        var staffId: String = "",
        @SerializedName("seller_code")
        var sellerCode: String = "")