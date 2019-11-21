package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductOption(
        var id: Int = 0,
        @SerializedName("product_id")
        var productId: Long,
        @SerializedName("attribute_id")
        var attrId: String = "",
        var label: String = "",
        var position: Int = 0,
        var values: List<ProductValue>
) : Parcelable