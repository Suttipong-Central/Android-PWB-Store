package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName
import io.realm.annotations.PrimaryKey

class CartTotalResponse(
        @SerializedName("base_grand_total")
        var totalPrice: Double = 0.0,
        @SerializedName("base_discount_amount")
        var discountPrice: Double = 0.0,
        @SerializedName("items_qty")
        var qty: Int = 0,
        var items: List<ShoppingCartItem>? = arrayListOf()
)

class ShoppingCartItem(
        @PrimaryKey
        @SerializedName("item_id")
        var id: Long? = 0,
        var qty: Int? = 0,
        var sku: String,
        @SerializedName("price_incl_tax")
        var price: Double? = 0.0,
        var name: String? = "",
        @SerializedName("extension_attributes")
        var extension: ItemExtension,
        @SerializedName("discount_amount")
        var discount: Double? = 0.0
)

class ItemExtension(
        @SerializedName("sales_rules")
        var saleRules: List<Rules>,
        @SerializedName("free_items_added")
        var freebies: List<FreeItem>,
        @SerializedName("amasty_promo")
        var amastyPromo: FreeItemImage
)

class FreeItem(
        @SerializedName("quote_id")
        var quoteId: Long,
        @SerializedName("item_id")
        var id: Long,
        var sku: String,
        @SerializedName("sales_rule_id")
        var saleRuleId: Long,
        var qty: Int,
        @SerializedName("intent_qty")
        var intentQty: Int,
        @SerializedName("for_item_id")
        var forItemId: Long,
        @SerializedName("associated_item_id")
        var associatedItemId: Long
)

class FreeItemImage(
        @SerializedName("image_src")
        var src: String,
        @SerializedName("image_alt")
        var alt: String,
        @SerializedName("image_width")
        var width: String,
        @SerializedName("image_height")
        var height: String
)

class Rules(
        @SerializedName("quote_id")
        var quoteId: Long? = 0
)