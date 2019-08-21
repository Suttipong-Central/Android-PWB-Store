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

// Format of Order success page
fun String.toOrderDateTime(defaultLanguage: String): String {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale(defaultLanguage))
    val timezone = TimeZone.getTimeZone("GMT+7").id
    dateFormatter.timeZone = TimeZone.getTimeZone(timezone)
    return dateFormatter.format(this.toDate("dd MMM yyyy, HH:mm:ss"))
}

fun Date.toDateText(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return formatter.format(this)
}

fun String.toDate(): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    return formatter.parse(this)
}

fun String.toDate(format: String): Date {
    val formatter = SimpleDateFormat(format, Locale.ENGLISH)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(this)
}