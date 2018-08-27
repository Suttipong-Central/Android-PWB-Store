package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class NamePattern(
        @SerializedName("th")
        var thai: String = "",
        @SerializedName("en")
        var eng: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thai)
        parcel.writeString(eng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NamePattern> {
        override fun createFromParcel(parcel: Parcel): NamePattern {
            return NamePattern(parcel)
        }

        override fun newArray(size: Int): Array<NamePattern?> {
            return arrayOfNulls(size)
        }
    }
}