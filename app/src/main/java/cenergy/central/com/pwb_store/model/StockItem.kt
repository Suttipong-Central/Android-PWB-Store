package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 24/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

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
        var maxQTY: Int? = 1) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readByte() != 0.toByte(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(itemId)
        parcel.writeValue(productId)
        parcel.writeValue(stockId)
        parcel.writeValue(qty)
        parcel.writeByte(if (isInStock) 1 else 0)
        parcel.writeValue(maxQTY)
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