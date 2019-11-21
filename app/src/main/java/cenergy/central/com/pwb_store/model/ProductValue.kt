package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductValue (
        @SerializedName("value_index")
        var index: Int = 0,
        @SerializedName("extension_attributes")
        var valueExtension: ProductValueExtension? = null): Parcelable