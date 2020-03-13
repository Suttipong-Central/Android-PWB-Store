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

        // datalake
        const val DATALAKE_HOST_NAME = "datalake-api.powerbuy.co.th"
        const val DATALAKE_X_SUBJECT_ID_HEADER = "x-subject-id"
        const val DATALAKE_X_SUBJECT_ID_VALUE = "base64"
        const val DATALAKE_X_API_KEY_HEADER = "x-api-key"
        const val DATALAKE_X_API_KEY_VALUE = "4tsGla4Avj6ZyIL86O08h94B7DObJSHS4eIYdEgY"
        // end region datalake

        // region central
        const val CENTRAL_HOST_NAME = "https://api.central.tech"
        const val FIRE_STORE_COLLECTION_NAME = "secret-key"
        const val FIRE_STORE_DOCUMENT_KEY = "production"
        // endregion

        // region formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // endregion
    }
}