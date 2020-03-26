package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.realm.RealmListParceler
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@RealmClass
open class ProductGallery(var id: String = "",
                          @SerializedName("media_type") var type: String? = "",
                          var label: String? = "",
                          var position: Int = 0,
                          var disabled: Boolean = false,
                          var file: String = ""): Parcelable, RealmModel

object ProductGalleryRealmListParceler: RealmListParceler<ProductGallery> {
    override val clazz: Class<ProductGallery>
        get() = ProductGallery::class.java
}