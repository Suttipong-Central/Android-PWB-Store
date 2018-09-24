package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable

class DialogOption(var icon: Int, var description: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(icon)
        parcel.writeInt(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DialogOption> {
        override fun createFromParcel(parcel: Parcel): DialogOption {
            return DialogOption(parcel)
        }

        override fun newArray(size: Int): Array<DialogOption?> {
            return arrayOfNulls(size)
        }

        fun createDialogOption(icon: Int, description: Int): DialogOption {
            return DialogOption(icon = icon, description = description)
        }
    }
}
