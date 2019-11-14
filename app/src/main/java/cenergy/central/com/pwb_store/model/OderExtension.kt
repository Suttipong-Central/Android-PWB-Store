package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject

open class OderExtension(
        @SerializedName("shipping_assignments")
        var shippingAssignments: RealmList<ShippingAssignment>? = RealmList(),
        var coupon: OrderCoupon? = null) : RealmObject()

open class OrderCoupon(
        @SerializedName("discount_amount")
        var discountAmount: Double = 0.0,
        @SerializedName("discount_amount_formatted")
        var discountFormat: String = "",
        @SerializedName("coupon_code")
        var couponCode: String = ""
) : RealmObject()