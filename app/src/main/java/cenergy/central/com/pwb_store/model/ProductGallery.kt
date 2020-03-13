package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 29/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class ProductGallery(var id: String = "",
                          @SerializedName("media_type") var type: String? = "",
                          var label: String? = "",
                          var position: Int = 0,
                          var disabled: Boolean = false,
                          var file: String = ""): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(label)
        parcel.writeInt(position)
        parcel.writeByte(if (disabled) 1 else 0)
        parcel.writeString(file)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductGallery> {
        override fun createFromParcel(parcel: Parcel): ProductGallery {
            return ProductGallery(parcel)
        }

        override fun newArray(size: Int): Array<ProductGallery?> {
            return arrayOfNulls(size)
        }
    }
}