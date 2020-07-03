package cenergy.central.com.pwb_store.extensions

import android.content.Context
import android.os.Parcel
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.AddressInformation
import cenergy.central.com.pwb_store.model.CacheCartItem
import cenergy.central.com.pwb_store.model.PaymentMethod
import cenergy.central.com.pwb_store.model.SubAddress
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

fun List<CacheCartItem>.getPaymentType(context: Context): ArrayList<PaymentMethod> {
    val paymentType = arrayListOf<String>()
    forEach { product ->
        if (product.paymentMethod.isNotEmpty() && product.paymentMethod != "") {
            paymentType.addAll(product.paymentMethod.split(","))
        }
    }
    val paymentMethodFilter = arrayListOf<PaymentMethod>()
    val fullPayment = PaymentMethod(title = context.getString(R.string.fullpayment), code = "redirectp2c2p")
    val installment = PaymentMethod(title = context.getString(R.string.installment), code = "redirect_installment")
    paymentType.distinct().forEach {
        if (it == "fullpayment" || it == "redirectp2c2p") {
            if (!paymentMethodFilter.contains(fullPayment)) {
                paymentMethodFilter.add(fullPayment)
            }
        }
        if (it == "installment" || it == "redirect_installment") {
            if (!paymentMethodFilter.contains(installment)) {
                paymentMethodFilter.add(installment)
            }
        }
    }
    return paymentMethodFilter
}

fun Int.dpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this.toFloat() * density).roundToInt()
}

fun List<PaymentMethod>.getMethodTitle(): List<String> {
    val methods = arrayListOf<String>()
    forEach { paymentMethod ->
        paymentMethod.title?.let { methods.add(it) }
    }
    return methods
}

fun Parcel.writeLongList(input: List<Long>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeLong) // Save each element.
}

fun Parcel.createLongList(): List<Long> {
    val size = readLong()
    val output = ArrayList<Long>()
    for (i in 0 until size) {
        output.add(readLong())
    }
    return output
}

fun Double.toPriceDisplay(): String {
    val price = NumberFormat.getInstance(Locale.getDefault()).format(this)
    return "฿$price"
}

fun Double.toStringDiscount(): Double {
    return this.toString().replace("-", "").toDouble()
}

fun String?.toStringDiscount(): Double {
    return this?.replace("-", "")?.toDouble() ?: 0.0
}

fun List<List<Long>>.findIntersect(): List<Long> {
    val sum = arrayListOf<Long>()
    this.forEach {
        sum.addAll(it)
    }
    return sum.groupBy { it }.filter { it.value.size == this.size }.flatMap { it.value }
}

fun AddressInformation.modifyToCdsType(): AddressInformation {
    val oldSubAddress = this.subAddress
    var addressLine = ""

    if (subAddress != null) {
        if (!subAddress!!.houseNumber.isNullOrEmpty()) {
            addressLine += subAddress!!.houseNumber + " "
        }
        if (!subAddress!!.soi.isNullOrEmpty()) {
            addressLine += subAddress!!.soi + ", "
        }
        if (!subAddress!!.addressLine.isNullOrEmpty()) {
            addressLine += subAddress!!.addressLine
        }
    }

    val newSubAddress = SubAddress(mobile = oldSubAddress?.mobile,
            houseNumber = "",
            building = oldSubAddress?.building,
            soi = "",
            t1cNo = oldSubAddress?.t1cNo,
            district = oldSubAddress?.district,
            subDistrict = oldSubAddress?.subDistrict,
            postcode = oldSubAddress?.postcode,
            districtId = oldSubAddress?.districtId,
            subDistrictId = oldSubAddress?.subDistrictId,
            postcodeId = oldSubAddress?.postcodeId,
            addressLine = addressLine
    )

    this.subAddress = newSubAddress
    return this
}