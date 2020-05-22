package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OfflinePriceItem(
        @SerializedName("entity_id")
        var entityId: String = "",
        @SerializedName("product_id")
        var productId: String = "",
        var price: Double = 0.0,
        @SerializedName("special_price")
        var specialPrice: Double = 0.0,
        @SerializedName("special_price_start")
        var specialFromDate: String? = null,
        @SerializedName("special_price_end")
        var specialToDate: String? = null,
        @SerializedName("retailer_id")
        var retailerId: String = "",
        @SerializedName("product_sku")
        var productSku: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = "") : Parcelable