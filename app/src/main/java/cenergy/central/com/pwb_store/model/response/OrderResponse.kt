package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.AddressInformation
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Anuphap Suwannamas on 26/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

open class OrderResponse(
        @SerializedName("increment_id")
        var orderId: String? = "",
        @SerializedName("base_currency_code")
        var currencyCode: String? = "",
        @SerializedName("base_grand_total")
        var baseTotal: Double = 0.0,
        @SerializedName("base_tax_amount")
        var baseTax: Double = 0.0,
        @SerializedName("customer_email")
        var customerEmail: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "",
        @SerializedName("shipping_description")
        var shippingDescription: String = "",
        var state: String = "",
        var status: String = "",
        var storeId: Int? = null,
        @SerializedName("store_name")
        var storeName: String? = null,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        var weight: Double = 0.0,
        @SerializedName("items")
        var items: RealmList<Item>? = null,
        @SerializedName("billing_address")
        var billingAddress: AddressInformation? = null
) : RealmObject()

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
        var imageUrl: String = "" // image cache for demo will delete later
) : RealmObject()