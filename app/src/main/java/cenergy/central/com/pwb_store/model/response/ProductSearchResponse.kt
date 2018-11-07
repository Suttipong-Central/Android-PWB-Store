package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

class ProductSearchResponse(
        @SerializedName("popular_products")
        var popularProducts: List<ProductSearch>? = null,
        @SerializedName("popular_searches")
        var popularSearches: List<PopularSearch>? = null
){
    fun hasPopularSearch(): Boolean{
        return popularSearches != null && popularSearches!!.isNotEmpty()
    }
    fun hasPopularProducts(): Boolean{
        return popularProducts != null && popularProducts!!.isNotEmpty()
    }
}

class PopularSearch(
        var title: String? = "",
        @SerializedName("num_results")
        var result: Int? = 0,
        @SerializedName("search_results")
        var productList: List<ProductSearch>? = listOf()
)

class ProductSearch(
        var id: Int? = 0,
        var name: String? = "",
        var sku: String? = "",
        var thumbnail: String? = "",
        var price: Double? = 0.0,
        var brand: String? = "",
        var url: String? = ""
){
    fun isNotNull(): Boolean{
        return id != null && name != null && sku != null && price != null && brand != null
    }
}
