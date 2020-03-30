package cenergy.central.com.pwb_store.model.response

import cenergy.central.com.pwb_store.model.Product
import cenergy.central.com.pwb_store.model.ProductFilter
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class ProductResponse(
        @PrimaryKey
        var categoryId: String = "",
        var products: RealmList<Product> = RealmList(),
        @Ignore
        var currentPage: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0,
        var filters: RealmList<ProductFilter> = RealmList()): RealmModel {

    fun isFirstPage(): Boolean {
        return currentPage == 1
    }
}