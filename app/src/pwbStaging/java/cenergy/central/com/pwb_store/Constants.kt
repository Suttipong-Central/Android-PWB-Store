package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // region magento
        const val BASE_URL_MAGENTO = "https://staging.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer js6rsdr0anv25ubgcqixh5m5mckbbwth"
        const val PWB_HOST_NAME = "staging.powerbuy.co.th"

        // for testing
//        const val BASE_URL_MAGENTO = "https://uat.powerbuy.co.th"
//        const val CLIENT_MAGENTO = "Bearer ngvids7tnggs94sm81k8a3oxjgl9cd16"
//        const val PWB_HOST_NAME = "uat.powerbuy.co.th"
        // endregion

        // region central
        // SIT
//         const val CLIENT_SERVICE_NAME = "execute-api"
//         const val CLIENT_REGION = "ap-southeast-1"
//         const val CLIENT_X_API_KEY = "lIrZy8ZTEvkmu4uDe0m06wqNo91REUN7aWnk6GYi"
//         const val HOST_NAME = "https://sit-api.central.tech"
//         const val CLIENT_ACCESS_KEY = "AKIAIZBEAJOWKANSLLYA"
//         const val CLIENT_SECRET_KEY = "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"

        // UAT
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