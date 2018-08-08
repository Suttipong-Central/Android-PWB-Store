package cenergy.central.com.pwb_store.model

import android.os.Parcel
import android.os.Parcelable

class Product() : Parcelable,IViewType {

    var id: Int = 0
    var viewTypeID: Int = 0
    var sku: String = ""
    var name: String = ""
    var attributeID: Int = 0
    var price: Int = 0
    var status: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        sku = parcel.readString()
        name = parcel.readString()
        attributeID = parcel.readInt()
        price = parcel.readInt()
        status = parcel.readInt()
    }

    override fun getViewTypeId(): Int {
        return viewTypeID
    }

    override fun setViewTypeId(id: Int) {
        this.viewTypeID = id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(sku)
        parcel.writeString(name)
        parcel.writeInt(attributeID)
        parcel.writeInt(price)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}