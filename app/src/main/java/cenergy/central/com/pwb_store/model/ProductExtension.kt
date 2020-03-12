package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.model.response.OfflinePriceItem
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductExtension(
        @SerializedName("description")
        var description: String? = "",
        @SerializedName("short_description")
        var shortDescription: String? = "",
        var barcode:String? = "",
        @SerializedName("stock_item")
        var stokeItem: StockItem? = null,
        @SerializedName("configurable_product_options")
        var productConfigOptions: List<ProductOption>? = arrayListOf(),
        @SerializedName("configurable_product_links")
        var productConfigLinks: List<String>? = arrayListOf(),
        var specifications: List<Specification> = arrayListOf(),
        @SerializedName("pricing_per_store")
        var pricingPerStore: List<OfflinePriceItem> = arrayListOf()) : Parcelable