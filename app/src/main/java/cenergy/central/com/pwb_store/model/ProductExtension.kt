package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 24/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class ProductExtension(@SerializedName("description")
                            var description: String? = "",
                            @SerializedName("short_description")
                            var shortDescription: String? = "",
                            var barcode:String? = "",
                            @SerializedName("stock_item")
                            var stokeItem: StockItem? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(StockItem::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeString(shortDescription)
        parcel.writeString(barcode)
        parcel.writeParcelable(stokeItem, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductExtension> {
        override fun createFromParcel(parcel: Parcel): ProductExtension {
            return ProductExtension(parcel)
        }

        override fun newArray(size: Int): Array<ProductExtension?> {
            return arrayOfNulls(size)
        }
    }
}