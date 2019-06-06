package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ProductValue (
        @SerializedName("value_index")
        var index: Long = 0,
        @SerializedName("extension_attributes")
        var valueExtension: ProductValueExtension? = null): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(ProductValueExtension::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(index)
        parcel.writeParcelable(valueExtension, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductValue> {
        override fun createFromParcel(parcel: Parcel): ProductValue {
            return ProductValue(parcel)
        }

        override fun newArray(size: Int): Array<ProductValue?> {
            return arrayOfNulls(size)
        }
    }
}