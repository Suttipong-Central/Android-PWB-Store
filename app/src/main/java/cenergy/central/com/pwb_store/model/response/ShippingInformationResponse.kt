package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.PaymentMethod
import com.google.gson.annotations.SerializedName

data class ShippingInformationResponse(@SerializedName("payment_methods")
                                       var paymentMethods: ArrayList<PaymentMethod> = arrayListOf(),
                                       var totals: CartTotal? = null
)