package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*


class Constants {
    companion object {
        // region magento
        const val BASE_URL_MAGENTO = "https://backend.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer ngvids7tnggs94sm81k8a3oxjgl9cd16"
        const val PWB_HOST_NAME = "backend.powerbuy.co.th"
        // endregion

        const val WEB_HOST_NAME = "https://www.powerbuy.co.th"

        const val CONTENT_TYPE = "Content-Type"
        const val APPTICATION_JSON = "application/json"

        // datalake
        const val DATALAKE_HOST_NAME = "datalake-api.powerbuy.co.th"
        const val DATALAKE_X_SUBJECT_ID_HEADER = "x-subject-id"
        const val DATALAKE_X_SUBJECT_ID_VALUE = "base64"
        const val DATALAKE_X_API_KEY_HEADER = "x-api-key"
        const val DATALAKE_X_API_KEY_VALUE = "4tsGla4Avj6ZyIL86O08h94B7DObJSHS4eIYdEgY"
        // end region datalake

        // region central
        const val CENTRAL_HOST_NAME = "https://api.central.tech"
        const val CLIENT_SERVICE_NAME = "execute-api"
        const val CLIENT_REGION = "ap-southeast-1"
        const val CLIENT_X_API_KEY = "lIrZy8ZTEvkmu4uDe0m06wqNo91REUN7aWnk6GYi"
        const val CLIENT_ACCESS_KEY = "AKIAJK27ORQKY42QRFWQ"
        const val CLIENT_SECRET_KEY = "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"
        // endregion

        // region formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // endregion

        val SPECIAL_CATEGORIES = arrayListOf("130639", "130704", "131187")
    }
}