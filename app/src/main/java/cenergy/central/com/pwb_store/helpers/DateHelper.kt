package cenergy.central.com.pwb_store.helpers

import java.util.*

class DateHelper {

    companion object {
        fun isLocaleTH(): Boolean {
            val locale = Locale.getDefault().country
            return locale == "th" || locale == "TH"
        }
    }
}
