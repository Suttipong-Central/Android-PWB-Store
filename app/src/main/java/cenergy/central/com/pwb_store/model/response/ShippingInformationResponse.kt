package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.CartTotal
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class ShippingInformationResponse(@SerializedName("payment_methods")
                                       var paymentMethods: ArrayList<PaymentMethod>? = arrayListOf(),
                                       var totals: CartTotal? = null
)

data class PaymentMethod(var code: String? = "", var title: String? = "")