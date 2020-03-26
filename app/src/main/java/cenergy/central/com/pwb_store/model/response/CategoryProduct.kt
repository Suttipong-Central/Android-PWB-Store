package cenergy.central.com.pwb_store.model.response

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@RealmClass
open class CategoryProduct(
        @SerializedName("category_id")
        var categoryId: Long = 0,
        var name: String = "",
        var level: Int = 0,
        @SerializedName("parent_id")
        var parentId: Long = 0,
        @SerializedName("url_key")
        var urlKey: String? = null,
        @SerializedName("url_path")
        var urlPath: String? = null,
        var isParent: Boolean = false) : Parcelable, RealmModel

object CategoryProductRealmListParceler: RealmListParceler<CategoryProduct> {
    override val clazz: Class<CategoryProduct>
        get() = CategoryProduct::class.java
}