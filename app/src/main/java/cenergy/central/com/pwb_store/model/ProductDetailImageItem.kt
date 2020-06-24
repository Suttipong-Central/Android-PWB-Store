package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
open class ProductDetailImageItem(
        var id: String? = null,
        @SerializedName("LargeFullUrl")
        var imgUrl: String? = null,
        var slug: String? = null,
        var isSelected: Boolean = false
): Parcelable