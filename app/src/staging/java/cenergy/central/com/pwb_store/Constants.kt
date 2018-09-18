package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class Constants {
    companion object {
        const val BASE_URL_MAGENTO = "https://staging.powerbuy.co.th"
        const val HOST_NAME = "staging.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer ba102y6thpckeoqgo196u82tllvlf50q"

        // formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    }
}