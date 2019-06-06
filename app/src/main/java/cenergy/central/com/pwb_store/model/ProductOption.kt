package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ProductOption (
        var id: Int = 0,
        @SerializedName("product_id")
        var productId: Long,
        @SerializedName("attribute_id")
        var attrId: String = "",
        var label: String = "",
        var position: Int = 0,
        var values: List<ProductValue>
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.createTypedArrayList(ProductValue))
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeLong(productId)
        parcel.writeString(attrId)
        parcel.writeString(label)
        parcel.writeInt(position)
        parcel.writeTypedList(values)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductOption> {
        override fun createFromParcel(parcel: Parcel): ProductOption {
            return ProductOption(parcel)
        }

        override fun newArray(size: Int): Array<ProductOption?> {
            return arrayOfNulls(size)
        }
    }
}