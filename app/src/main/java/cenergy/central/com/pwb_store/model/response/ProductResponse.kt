package cenergy.central.com.pwb_store.model.response

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.model.Product
import com.google.gson.annotations.SerializedName

class ProductResponse() : Parcelable{

    @SerializedName("items")
    var products: ArrayList<Product> = arrayListOf()
    var currentPage: Int = 0

    constructor(parcel: Parcel) : this()

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductResponse> {
        override fun createFromParcel(parcel: Parcel): ProductResponse {
            return ProductResponse(parcel)
        }

        override fun newArray(size: Int): Array<ProductResponse?> {
            return arrayOfNulls(size)
        }
    }

    fun isFirstPage() : Boolean{
        return currentPage == 1
    }
}