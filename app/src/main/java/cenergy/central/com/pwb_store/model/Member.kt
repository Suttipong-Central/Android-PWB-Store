package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class Member(var nationalId: String = "",
                  var nationalityId: String = "",
                  var passportNo: String = "",
                  var dateOfBirth: String = "",
                  var documentCountryCode: String = "",
                  var homeNo: String = "",
                  @SerializedName("homeVillageOrBuilding")
                  var homeBuilding: String = "",
                  var homeMoo: String,
                  var homeSoi: String,
                  var homeYak: String,
                  var homeRoad: String,
                  var homeSubDistrict: String,
                  var homeDistrict: String,
                  var homeCity: String,
                  var homePostalCode: String,
                  var homePhone: String,
                  var homePhoneCountryCode: String,
                  var homeExtension: String,
                  var mobilePhone: String,
                  var email: String,
                  var sex: String,
                  @SerializedName("memberNo")
                  var customerId: String,
                  var cardNo: String,
                  var mobileCountryCode: String,
                  var title: NamePattern? = null,
                  @SerializedName("firstName")
                  var firstname: NamePattern? = null,
                  @SerializedName("lastName")
                  var lastname: NamePattern? = null,
                  var memberTier: String = "",
                  var isStaff: String = "",
                  var status: String = ""
): Parcelable {
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
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(NamePattern::class.java.classLoader),
            parcel.readParcelable(NamePattern::class.java.classLoader),
            parcel.readParcelable(NamePattern::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nationalId)
        parcel.writeString(nationalityId)
        parcel.writeString(passportNo)
        parcel.writeString(dateOfBirth)
        parcel.writeString(documentCountryCode)
        parcel.writeString(homeNo)
        parcel.writeString(homeBuilding)
        parcel.writeString(homeMoo)
        parcel.writeString(homeSoi)
        parcel.writeString(homeYak)
        parcel.writeString(homeRoad)
        parcel.writeString(homeSubDistrict)
        parcel.writeString(homeDistrict)
        parcel.writeString(homeCity)
        parcel.writeString(homePostalCode)
        parcel.writeString(homePhone)
        parcel.writeString(homePhoneCountryCode)
        parcel.writeString(homeExtension)
        parcel.writeString(mobilePhone)
        parcel.writeString(email)
        parcel.writeString(sex)
        parcel.writeString(customerId)
        parcel.writeString(cardNo)
        parcel.writeString(mobileCountryCode)
        parcel.writeParcelable(title, flags)
        parcel.writeParcelable(firstname, flags)
        parcel.writeParcelable(lastname, flags)
        parcel.writeString(memberTier)
        parcel.writeString(isStaff)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Member> {
        override fun createFromParcel(parcel: Parcel): Member {
            return Member(parcel)
        }

        override fun newArray(size: Int): Array<Member?> {
            return arrayOfNulls(size)
        }
    }

    fun getFirstName(): String {
        return if (firstname != null) {
            if (firstname!!.thai.isNotEmpty()) {
                firstname!!.thai
            } else {
                firstname!!.eng
            }
        } else {
            ""
        }
    }

    fun getLastName(): String {
        return if (lastname != null) {
            if (lastname!!.thai.isNotEmpty()) {
                lastname!!.thai
            } else {
                lastname!!.eng
            }
        } else {
            ""
        }
    }
}
