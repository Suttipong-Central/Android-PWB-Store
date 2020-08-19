package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import cenergy.central.com.pwb_store.Constants
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class InstallmentPlan(
        var period: Int = 0,
        @SerializedName("interest_type")
        var interestType: String = "",
        @SerializedName("min_amount")
        var minAmount: Int = 0,
        @SerializedName("valid_from")
        var validFrom: String = "",
        var active: Boolean = false,
        var update: String = "",
        var bank: BankInstallment? = null,
        @SerializedName("valid_until")
        var validUntil: String = "",
        @SerializedName("bank_id")
        var bankId: Int = 0,
        var name: String = "",
        @SerializedName("customer_rate")
        var customerRate: Double = 0.0,
        @SerializedName("installmentplan_id")
        var id: Int = 0,
        @SerializedName("max_amount")
        var maxAmount: Long = 0,
        var create: String = "",
        var currency: String = "",
        @SerializedName("merchant_rate")
        var merchantRate: Double = 0.0
) : Parcelable {
        fun getBankImageUrl(): String {
                // example -> /media/banks/ktc22x22.jpg
                return if (bank != null) "${Constants.BASE_URL_MAGENTO}/media/${bank!!.image}" else ""
        }
        fun getBankColor(): String {
                return if (bank != null) bank!!.bankColor else ""
        }
}

@Parcelize
class BankInstallment(
        @SerializedName("bank_id")
        var id: Int = 0,
        @SerializedName("bank_image")
        var image: String = "",
        var name: String = "",
        @SerializedName("bank_icon")
        var icon: String = "",
        var active: Boolean = false,
        var create: String = "",
        var update: String = "",
        @SerializedName("bank_color")
        var bankColor: String = ""
) : Parcelable