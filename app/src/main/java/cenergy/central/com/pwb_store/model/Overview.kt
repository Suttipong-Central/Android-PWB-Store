package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable

class Overview() : IViewType, Parcelable{

    lateinit var productId: String
    lateinit var overviewHTML: String
    var viewTypeID = 0

    constructor(productId: String, overview: String) : this() {
        this.productId = productId
        this.overviewHTML = overview
    }

    constructor(parcel: Parcel) : this() {
        productId = parcel.readString() ?: ""
        overviewHTML = parcel.readString() ?: ""
        viewTypeId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(overviewHTML)
        parcel.writeInt(viewTypeID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Overview> {
        override fun createFromParcel(parcel: Parcel): Overview {
            return Overview(parcel)
        }

        override fun newArray(size: Int): Array<Overview?> {
            return arrayOfNulls(size)
        }
    }

    override fun getViewTypeId(): Int {
        return this.viewTypeID
    }

    override fun setViewTypeId(viewTypeID: Int) {
        this.viewTypeID = viewTypeID
    }
}