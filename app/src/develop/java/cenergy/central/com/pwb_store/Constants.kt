package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class Constants {
    companion object {
        // region magento
        const val BASE_URL_MAGENTO = "https://central.dev.bybluecomgroup.com"
        const val HOST_NAME = "central.dev.bybluecomgroup.com"
        const val CLIENT_MAGENTO = "Bearer pak63fnfvh8cna93jvouxk0lrbuc9exq"
        // endregion

        // region central
        const val CENTRAL_HOST_NAME = "https://uat-api.central.tech"
        const val CLIENT_SERVICE_NAME = "execute-api"
        const val CLIENT_REGION = "ap-southeast-1"
        const val CLIENT_X_API_KEY = "lIrZy8ZTEvkmu4uDe0m06wqNo91REUN7aWnk6GYi"
        const val CLIENT_ACCESS_KEY = "AKIAIIIW7RXNLIKRTSNQ"
        const val CLIENT_SECRET_KEY = "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"
        // endregion

        // region formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // endregion
    }
}