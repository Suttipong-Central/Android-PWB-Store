package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductFilter
import com.google.gson.annotations.SerializedName

class ProductResponse(
        var products: ArrayList<Product> = arrayListOf(),
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0,
        var filters: ArrayList<ProductFilter> = arrayListOf()) {

    fun isFirstPage(): Boolean {
        return currentPage == 1
    }
}