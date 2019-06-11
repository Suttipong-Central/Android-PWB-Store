package cenergy.central.com.pwb_store.model.body

import com.google.gson.annotations.SerializedName
import java.util.jar.Attributes

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class CartItemBody(var cartItem: CartBody? = null) {
    companion object {
        fun create(cartId: String, sku: String, listOptionsBody: ArrayList<OptionBody>): CartItemBody {
            return if (listOptionsBody.isNotEmpty()) {
                CartItemBody(CartBody(cartId, sku, 1, ProductOptionBody(OptionExtensionBody(listOptionsBody)))) // default add qty 1
            } else {
                CartItemBody(CartBody(cartId, sku, 1)) // default add qty 1
            }
        }
    }
}

data class CartBody(
        @SerializedName("quote_id")
        var cartId: String,
        var sku: String,
        var qty: Int = 1,
        @SerializedName("product_option")
        var productOption: ProductOptionBody? = null
)

data class ProductOptionBody(
        @SerializedName("extension_attributes")
        var extension: OptionExtensionBody
)

data class OptionExtensionBody(
        @SerializedName("configurable_item_options")
        var configItemOption: ArrayList<OptionBody> = arrayListOf()
)

data class OptionBody(
        @SerializedName("option_id")
        var attributeId: String? = "",
        @SerializedName("option_value")
        var optionValue: Int? = 0

)