package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Product
import com.google.gson.annotations.SerializedName

class ProductResponse(
        @SerializedName("items")
        var products: ArrayList<Product>? = arrayListOf(),
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0) {

    fun isFirstPage(): Boolean {
        return currentPage == 1
    }
}