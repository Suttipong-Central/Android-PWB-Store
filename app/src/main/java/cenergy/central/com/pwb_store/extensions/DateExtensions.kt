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

    // old data we save with "dd MMM yyyy, HH:mm:ss" :(
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        dateFormatter.format(formatter.parse(this))
    } catch (e: Exception) {
        dateFormatter.format(getLegacyOrderDate(this))
    }
}

private fun getLegacyOrderDate(s: String): Date {
    return try {
        s.toDate("dd MMM yyyy, HH:mm:ss", "en")
    } catch (e: Exception) {
        s.toDate("dd MMM yyyy, HH:mm:ss", "TH")
    }
}

fun Date.toDateText(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return formatter.format(this)
}

fun String.toDate(): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    return formatter.parse(this)
}

fun String.toDate(format: String, locale: String): Date {
    val formatter = SimpleDateFormat(format, Locale(locale))
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(this)
}