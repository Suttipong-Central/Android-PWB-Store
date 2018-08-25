package cenergy.central.com.pwb_store.model.body

import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 25/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */
data class UpdateItemBody(
        var cartItem: ItemBody? = null
)

data class ItemBody(
        @SerializedName("quote_id")
        var cartId: String,
        @SerializedName("item_id")
        var itemId: Long,
        var qty: Int = 1
)