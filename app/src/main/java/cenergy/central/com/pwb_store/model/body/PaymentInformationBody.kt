package cenergy.central.com.pwb_store.model.body

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class PaymentInformationBody(
        var cartId: String = "",
        var paymentMethod: MethodBody,
        var email: String = "",
        var staffId: String = "",
        var storeId: String = "")

data class MethodBody(var method: String)