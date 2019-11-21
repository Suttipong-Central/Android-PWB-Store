package cenergy.central.com.pwb_store.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Specification(var code: String,
                         var label: String,
                         var value: String? = "") : Parcelable