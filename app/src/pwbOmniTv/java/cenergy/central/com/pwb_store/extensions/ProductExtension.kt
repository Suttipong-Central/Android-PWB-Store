package cenergy.central.com.pwb_store.extensions

import cenergy.central.com.pwb_store.model.Product
import java.text.SimpleDateFormat
import java.util.*

fun Product.isSpecialPrice(): Boolean {
    if (specialFromDate != null && specialToDate != null) {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val specialFormDateTime = formatter.parse(specialFromDate!!)
        val specialToDateTime = formatter.parse(specialToDate!!)
        val current = Date()
        if (specialPrice < price) {
            return (current.time >= specialFormDateTime!!.time) && (current.time <= specialToDateTime!!.time)
        }
    }
    return false
}