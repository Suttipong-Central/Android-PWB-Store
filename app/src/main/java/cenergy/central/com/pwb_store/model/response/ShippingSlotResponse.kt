package cenergy.central.com.pwb_store.model.response

import android.os.Parcel
import android.os.Parcelable

class ShippingSlotResponse(
        var shippingSlot: ArrayList<ShippingSlot> = arrayListOf()
)

class ShippingSlot(
        var shippingDate: String? = "",
        var slot: ArrayList<Slot> = arrayListOf()
)

class Slot(
        var id: Int = 0,
        var description: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Slot> {
        override fun createFromParcel(parcel: Parcel): Slot {
            return Slot(parcel)
        }

        override fun newArray(size: Int): Array<Slot?> {
            return arrayOfNulls(size)
        }
    }
}