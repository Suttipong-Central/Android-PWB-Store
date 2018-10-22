package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 22/10/2018 AD.
 * Email: Anupharpae@gmail.com
 */
data class MemberT1CAddress(
        var homeNo: String? = "",
        @SerializedName("villageOrBuilding")
        var building: String? = "",
        var floor: String? = "",
        var moo: String? = "",
        var soi: String? = "",
        var yak: String? = "",
        var road: String? = "",
        var country: String? = "",
        @SerializedName("postalCode")
        var postcode: String? = "",
        @SerializedName("subDistrict")
        var subDistrict: String? = "",
        var district: String? = "",
        @SerializedName("city")
        var province: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(homeNo)
        parcel.writeString(building)
        parcel.writeString(floor)
        parcel.writeString(moo)
        parcel.writeString(soi)
        parcel.writeString(yak)
        parcel.writeString(road)
        parcel.writeString(country)
        parcel.writeString(postcode)
        parcel.writeString(subDistrict)
        parcel.writeString(district)
        parcel.writeString(province)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemberT1CAddress> {
        override fun createFromParcel(parcel: Parcel): MemberT1CAddress {
            return MemberT1CAddress(parcel)
        }

        override fun newArray(size: Int): Array<MemberT1CAddress?> {
            return arrayOfNulls(size)
        }
    }
}