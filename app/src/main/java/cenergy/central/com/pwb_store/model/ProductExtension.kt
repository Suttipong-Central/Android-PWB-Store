package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.extensions.readStringRealmList
import cenergy.central.com.pwb_store.extensions.writeStringRealmList
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith

@Parcelize
open class ProductExtension(
        @SerializedName("description")
        var description: String? = "",
        @SerializedName("short_description")
        var shortDescription: String? = "",
        var barcode:String? = "",
        @SerializedName("stock_item")
        var stokeItem: StockItem? = null,
        @SerializedName("configurable_product_options")
        var productConfigOptions: @WriteWith<ProductOptionRealmListParceler> RealmList<ProductOption>? = RealmList(),
        @SerializedName("configurable_product_links")
        var productConfigLinks: @WriteWith<StringRealmListParceler> RealmList<String>? = RealmList(),
        var specifications: @WriteWith<SpecificationRealmListParceler> RealmList<Specification> = RealmList()) : Parcelable, RealmObject()

object StringRealmListParceler: Parceler<RealmList<String>?> {
        override fun create(parcel: Parcel): RealmList<String>? = parcel.readStringRealmList()

        override fun RealmList<String>?.write(parcel: Parcel, flags: Int) {
                parcel.writeStringRealmList(this)
        }
}