package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@RealmClass
open class ProductDetailImageItem(
        var productImageId: String? = null,
        @SerializedName("LargeFullUrl")
        var imgUrl: String? = null,
        var viewTypeID: Int = 0,
        var slug: String? = null,
        var isSelected: Boolean = false) : IViewType, Parcelable, RealmModel {

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(id: Int) {
        viewTypeID = id
    }
}

object ProductDetailImageRealmListParceler: RealmListParceler<ProductDetailImageItem> {
    override val clazz: Class<ProductDetailImageItem>
        get() = ProductDetailImageItem::class.java
}

