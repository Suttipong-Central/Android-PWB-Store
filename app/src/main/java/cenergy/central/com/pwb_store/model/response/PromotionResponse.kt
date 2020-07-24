package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

open class PromotionResponse(
        var sku: String = "",
        @SerializedName("extension_attributes")
        var extension: PromotionExtension? = null
)

open class PromotionExtension(
        @SerializedName("credit_card_promotions")
        var creditCardPromotions: ArrayList<CreditCardPromotion> = arrayListOf(),
        @SerializedName("free_items")
        var freeItems: ArrayList<FreeItems> = arrayListOf()
)

open class CreditCardPromotion(
        @SerializedName("promotion_id")
        var id: Long = 0,
        @SerializedName("bank_icon")
        var bankIcon: String = "",
        @SerializedName("bank_color")
        var bankColor: String = "",
        @SerializedName("rule_id")
        var ruleId: Long = 0,
        @SerializedName("rule_name")
        var ruleName: String = "",
        @SerializedName("simple_action")
        var simpleAction: String = "",
        @SerializedName("coupon_code")
        var couponCode: String? = null,
        @SerializedName("is_active")
        var isActive: Boolean = false,
        @SerializedName("from_date")
        var fromDate: String? = null,
        @SerializedName("to_date")
        var toDate: String? = null,
        var label: String? = null,
        @SerializedName("discount_amount")
        var discountAmount: Int = 0,
        @SerializedName("discount_qty")
        var discountQty: Int = 0,
        @SerializedName("discount_step")
        var discountStep: Int = 0
)

open class FreeItems(
        var freebies: ArrayList<Freebie> = arrayListOf(),
        @SerializedName("rule_id")
        var ruleId: Long = 0,
        @SerializedName("rule_name")
        var ruleName: String = "",
        @SerializedName("simple_action")
        var simpleAction: String = "",
        @SerializedName("coupon_code")
        var couponCode: String? = null,
        @SerializedName("is_active")
        var isActive: Boolean = false,
        @SerializedName("from_date")
        var fromDate: String? = null,
        @SerializedName("to_date")
        var toDate: String? = null,
        var label: String? = null,
        @SerializedName("discount_amount")
        var discountAmount: Int = 300,
        @SerializedName("discount_qty")
        var discountQty: Int = 0,
        @SerializedName("discount_step")
        var discountStep: Int = 0
)

open class Freebie(
        var sku: String = "",
        var qty: Int = 0
)