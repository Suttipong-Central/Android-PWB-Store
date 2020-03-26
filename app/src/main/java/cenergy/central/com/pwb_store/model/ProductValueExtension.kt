package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.extensions.createRealmListLong
import cenergy.central.com.pwb_store.extensions.writeRealmListLong
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith

@Parcelize
@RealmClass
open class ProductValueExtension(
        var label: String? = "",
        @SerializedName("frontend_value")
        var value: String? = "",
        @SerializedName("frontend_type")
        var type: String? = "",
        var products: @WriteWith<ProductValueExtensionRealmListParceler> RealmList<Long> = RealmList()) : Parcelable, RealmModel

object ProductValueExtensionRealmListParceler: Parceler<RealmList<Long>> {
    override fun create(parcel: Parcel): RealmList<Long> = parcel.createRealmListLong()

    override fun RealmList<Long>.write(parcel: Parcel, flags: Int) {
        parcel.writeRealmListLong(this)
    }
}