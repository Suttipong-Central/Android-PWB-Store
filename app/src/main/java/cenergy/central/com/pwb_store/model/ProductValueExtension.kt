package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductValueExtension (
        var label: String? = "",
        @SerializedName("frontend_value")
        var value: String? = "",
        @SerializedName("frontend_type")
        var type: String? = "",
        var products: List<Long> = arrayListOf()
): Parcelable