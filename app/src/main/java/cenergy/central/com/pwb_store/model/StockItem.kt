package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@RealmClass
open class StockItem(
        @SerializedName("item_id")
        var itemId: Long? = 0,
        @SerializedName("product_id")
        var productId: Long? = 0,
        @SerializedName("stock_id")
        var stockId: Long? = 0,
        var qty: Int? = 0,
        @SerializedName("is_in_stock")
        var isInStock: Boolean = false,
        @SerializedName("max_sale_qty")
        var maxQTY: Int? = 0,
        @SerializedName("min_sale_qty")
        var minQTY: Int? = 0,
        var is2HProduct: Boolean = false,
        var isSalable: Boolean = false) : Parcelable, RealmModel