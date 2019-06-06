package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.extensions.createLongList
import cenergy.central.com.pwb_store.extensions.writeLongList
import com.google.gson.annotations.SerializedName

class ProductValueExtension (
        var label: String = "",
        @SerializedName("frontend_value")
        var value: String = "",
        @SerializedName("frontend_type")
        var type: String = "",
        var products: List<Long> = arrayListOf()
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createLongList()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(value)
        parcel.writeString(type)
        parcel.writeLongList(products)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductValueExtension> {
        override fun createFromParcel(parcel: Parcel): ProductValueExtension {
            return ProductValueExtension(parcel)
        }

        override fun newArray(size: Int): Array<ProductValueExtension?> {
            return arrayOfNulls(size)
        }
    }
}