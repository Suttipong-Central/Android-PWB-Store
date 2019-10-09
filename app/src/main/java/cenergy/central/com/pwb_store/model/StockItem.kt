package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StockItem(
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
        var isSalable: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readByte() != 0.toByte(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(itemId)
        parcel.writeValue(productId)
        parcel.writeValue(stockId)
        parcel.writeValue(qty)
        parcel.writeByte(if (isInStock) 1 else 0)
        parcel.writeValue(maxQTY)
        parcel.writeValue(minQTY)
        parcel.writeByte(if (is2HProduct) 1 else 0)
        parcel.writeByte(if (isSalable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StockItem> {
        override fun createFromParcel(parcel: Parcel): StockItem {
            return StockItem(parcel)
        }

        override fun newArray(size: Int): Array<StockItem?> {
            return arrayOfNulls(size)
        }
    }
}