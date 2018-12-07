package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Item(
        @PrimaryKey
        @SerializedName("item_id")
        var itemId: Long? = 0,
        var sku: String? = "",
        @SerializedName("qty_ordered")
        var qty: Int = 0,
        var name: String? = "",
        var price: Double? = 0.0,
        @SerializedName("base_price_incl_tax")
        var basePriceIncludeTax: Double? = 0.0,
        @SerializedName("base_row_total_incl_tax")
        var baseTotalIncludeTax: Double? = 0.0,
        @SerializedName("discount_amount")
        var discountAmount: Double? = 0.0,
        @SerializedName("product_type")
        var type: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "",
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("tax_amount")
        var taxAmount: Double = 0.0,
        @SerializedName("tax_percent")
        var taxPercent: Double = 0.0,
        var weight: Double = 0.0,
        var imageUrl: String = "", // image cache for demo will delete later
        var isFreebie: Boolean = false
) : RealmObject()