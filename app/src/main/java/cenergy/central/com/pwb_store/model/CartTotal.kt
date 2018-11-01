package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class CartTotal(@SerializedName("grand_total")
                     var grandTotal: Double = 0.0,
                     @SerializedName("base_grand_total")
                     var baseGrandTotal: Double = 0.0,
                     @SerializedName("subtotal")
                     var subTotal: Double = 0.0,
                     @SerializedName("base_subtotal")
                     var baseSubTotal: Double = 0.0,
                     @SerializedName("discount_amount")
                     var discountAmount: Double = 0.0,
                     @SerializedName("tax_amount")
                     var taxAmount: Double = 0.0,
                     @SerializedName("base_currency_code")
                     var currencyCode: String = "",
                     @SerializedName("quote_currency_code")
                     var quoteCurrencyCode: String = ""
)