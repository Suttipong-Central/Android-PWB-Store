package cenergy.central.com.pwb_store.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formatter(): String {
    val dateFormatter = SimpleDateFormat("dd mm yyyy, HH:mm:ss", Locale("TH"))
    return dateFormatter.format(this)
}

fun Date.formatterUTC(defaultLanguage: String): String {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale(defaultLanguage))
    val timezone = TimeZone.getTimeZone("GMT+7").id
    dateFormatter.timeZone = TimeZone.getTimeZone(timezone)
    return dateFormatter.format(this)
}

fun String.toDate(): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(this)
}