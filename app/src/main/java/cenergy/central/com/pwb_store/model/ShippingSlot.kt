package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ShippingSlot(
        val id: String? = null,
        @SerializedName("date_time_from")
        val dateTimeFrom: String? = "",
        @SerializedName("date_time_to")
        val dateTimeTo: String? = "",
        @SerializedName("extension_attributes")
        val slotExtension: SlotExtension? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(SlotExtension::class.java.classLoader))

    fun getTimeDescription(): String {
        return if (dateTimeFrom != null && dateTimeTo != null &&
                dateTimeFrom.isNotEmpty() && dateTimeTo.isNotEmpty()) {
            val timeFrom = dateTimeFrom.split(" ")[1]
            val timeTo = dateTimeTo.split(" ")[1]
            "$timeFrom - $timeTo" // "9:00 - 9:30"
        } else {
            ""
        }
    }

    fun getDate(): String {
        return if (dateTimeFrom != null && dateTimeFrom.isNotEmpty()) {
            dateTimeFrom.split(" ").first()
        } else {
            ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(dateTimeFrom)
        parcel.writeString(dateTimeTo)
        parcel.writeParcelable(slotExtension, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShippingSlot> {
        override fun createFromParcel(parcel: Parcel): ShippingSlot {
            return ShippingSlot(parcel)
        }

        override fun newArray(size: Int): Array<ShippingSlot?> {
            return arrayOfNulls(size)
        }
    }
}

data class SlotExtension(@SerializedName("day_slot_id")
                         val daySlotId: Int,
                         val booking: Booking? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readParcelable(Booking::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(daySlotId)
        parcel.writeParcelable(booking, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SlotExtension> {
        override fun createFromParcel(parcel: Parcel): SlotExtension {
            return SlotExtension(parcel)
        }

        override fun newArray(size: Int): Array<SlotExtension?> {
            return arrayOfNulls(size)
        }
    }
}

data class Booking(val id: Int,
                   @SerializedName("slot_id")
                   val slotId: Int,
                   @SerializedName("quote_id")
                   val quoteId: Int,
                   @SerializedName("booking_code")
                   val bookingCode: String, //ED52B1E7-F9D5-4ED1-8436-B65D2533C3ED
                   @SerializedName("installation_code")
                   val installation_code: Int? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readValue(Int::class.java.classLoader) as? Int)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(slotId)
        parcel.writeInt(quoteId)
        parcel.writeString(bookingCode)
        parcel.writeValue(installation_code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }
}
