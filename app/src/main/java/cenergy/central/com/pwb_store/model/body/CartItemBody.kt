package cenergy.central.com.pwb_store.model.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class CartItemBody(
        var cartItem: CartBody? = null
)

data class CartBody(
        @SerializedName("quote_id")
        var cartId: String,
        var sku: String,
        var qty: Int = 1
)