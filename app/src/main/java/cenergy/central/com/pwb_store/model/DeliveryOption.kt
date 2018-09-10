package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

class DeliveryOption(
        @SerializedName("carrier_code")
        var carrierCode: String = "",
        @SerializedName("method_code")
        var methodCode: String = "",
        @SerializedName("carrier_title")
        var carrierTitle: String = "",
        @SerializedName("method_title")
        var methodTitle: String = "",
        var amount: Int = 0,
        @SerializedName("base_amount")
        var baseAmount: Int = 0,
        var available: Boolean = false,
        @SerializedName("extension_attributes")
        var remote: Remote,
        @SerializedName("error_message")
        var errorMessage: String = "",
        @SerializedName("price_excl_tax")
        var priceExcludeTax: String = "",
        @SerializedName("price_incl_tax")
        var priceIncludeTax: String = ""
        )

data class Remote(@SerializedName("is_remote")
                  var isRemote: String = "")