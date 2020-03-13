package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

class ProductSearchResponse(
        @SerializedName("items")
        var products: ArrayList<ProductSearch> = arrayListOf(),
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0)

class ProductSearch(
        var id: Int = 0,
        var sku: String = "",
        var name: String = "")