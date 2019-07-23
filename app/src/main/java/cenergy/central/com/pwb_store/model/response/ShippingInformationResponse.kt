package cenergy.central.com.pwb_store.model.response

import android.os.Parcel
import android.os.Parcelable
import cenergy.central.com.pwb_store.model.CartTotal
import com.google.gson.annotations.SerializedName

data class ShippingInformationResponse(@SerializedName("payment_methods")
                                       var paymentMethods: ArrayList<PaymentMethod> = arrayListOf(),
                                       var totals: CartTotal? = null
)

data class PaymentMethod(
        var code: String? = "",
        var title: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentMethod> {
        override fun createFromParcel(parcel: Parcel): PaymentMethod {
            return PaymentMethod(parcel)
        }

        override fun newArray(size: Int): Array<PaymentMethod?> {
            return arrayOfNulls(size)
        }

        const val CASH_ON_DELIVERY = "cashondelivery"
        const val FULL_PAYMENT = "fullpaymentredirect"
        const val PAY_AT_STORE = "e_ordering"
        const val INSTALLMENT = "p2c2p_ipp"
    }
}