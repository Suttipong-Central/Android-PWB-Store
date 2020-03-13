package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.Item
import cenergy.central.com.pwb_store.model.OderExtension
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
        @SerializedName("base_subtotal_incl_tax")
        var total: Double = 0.0,
        @SerializedName("base_grand_total")
        var baseTotal: Double = 0.0,
        @SerializedName("base_discount_amount")
        var discount: Double = 0.0,
        @SerializedName("base_tax_amount")
        var baseTax: Double = 0.0,
        @SerializedName("customer_email")
        var customerEmail: String? = "",
        @SerializedName("quote_id")
        var cartId: String? = "",
        @SerializedName("shipping_description")
        var shippingDescription: String = "",
        @SerializedName("shipping_incl_tax")
        var shippingAmount: Double = 0.0,
        var state: String = "",
        var status: String = "",
        var storeId: Int? = null,
        @SerializedName("store_name")
        var storeName: String? = null,
        @SerializedName("created_at")
        var createdAt: String = "",
        var weight: Double = 0.0,
        @SerializedName("items")
        var items: RealmList<Item>? = null,
        @SerializedName("billing_address")
        var billingAddress: AddressInformation? = null,
        @SerializedName("extension_attributes")
        var orderExtension: OderExtension? = null,
        var shippingType: String? = "") : RealmObject()