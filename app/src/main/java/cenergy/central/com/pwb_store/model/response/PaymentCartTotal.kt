package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.TotalSegment
import com.google.gson.annotations.SerializedName
import io.realm.annotations.PrimaryKey

class PaymentCartTotal(
        @SerializedName("subtotal_incl_tax")
        var totalPrice: Double = 0.0,
        @SerializedName("base_discount_amount")
        var discountPrice: Double = 0.0,
        @SerializedName("items_qty")
        var qty: Int = 0,
        var items: List<ShoppingCartItem>? = arrayListOf(),
        @SerializedName("total_segments")
        var totalSegment: List<TotalSegment>? = arrayListOf(),
        var couponCode: String = "",
        @SerializedName("base_shipping_amount")
        var shippingAmount: Double = 0.0
)

class ShoppingCartItem(
        @PrimaryKey
        @SerializedName("item_id")
        var id: Long? = 0,
        var qty: Int? = 0,
        var sku: String? = "",
        @SerializedName("price_incl_tax")
        var price: Double? = 0.0,
        var name: String? = "",
        @SerializedName("extension_attributes")
        var extension: ItemExtension? = null,
        @SerializedName("discount_amount")
        var discount: Double? = 0.0
)

class ItemExtension(
        @SerializedName("sales_rules")
        var saleRules: List<Rules> = arrayListOf(),
        @SerializedName("free_items_added")
        var freebies: List<FreeItem> = arrayListOf(),
        @SerializedName("amasty_promo")
        var amastyPromo: FreeItemImage? = null
)

class FreeItem(
        @SerializedName("quote_id")
        var quoteId: Long = 0,
        @SerializedName("item_id")
        var id: Long = 0,
        var sku: String = "",
        @SerializedName("sales_rule_id")
        var saleRuleId: Long = 0,
        var qty: Int = 0,
        @SerializedName("intent_qty")
        var intentQty: Int = 0,
        @SerializedName("for_item_id")
        var forItemId: Long = 0,
        @SerializedName("associated_item_id")
        var associatedItemId: Long = 0
)

class FreeItemImage(
        @SerializedName("image_src")
        var src: String = "",
        @SerializedName("image_alt")
        var alt: String = "",
        @SerializedName("image_width")
        var width: String = "",
        @SerializedName("image_height")
        var height: String = ""
)

class Rules(
        @SerializedName("quote_id")
        var quoteId: Long? = 0,
        @SerializedName("quote_item_id")
        var itemId: Long? = 0,
        @SerializedName("rule_id")
        var ruleId: Long? = 0,
        @SerializedName("discount_amount")
        var discount: Double? = 0.0,
        var order: Int? = 0,
        @SerializedName("priority_order")
        var orderPriority: Int? = 0,
        @SerializedName("coupon_code")
        var couponCode: String? = "",
        @SerializedName("discount_tax")
        var tax: Double? = 0.0,
        @SerializedName("calculation_base_price")
        var basePrice: Int? = 0,
        @SerializedName("address_id")
        var addressId: Long? = 0
)