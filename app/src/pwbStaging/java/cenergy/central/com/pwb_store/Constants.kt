package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // region magento
        const val BASE_URL_MAGENTO = "https://staging-mdc.powerbuy.co.th"
        const val PWB_HOST_NAME = "staging-mdc.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer ngvids7tnggs94sm81k8a3oxjgl9cd16"
        // endregion

        const val WEB_HOST_NAME = "https://staging-fe.powerbuy.co.th"

        // datalake
        const val DATALAKE_HOST_NAME = "staging-datalake-api.powerbuy.co.th"
        const val DATALAKE_X_SUBJECT_ID_HEADER = "x-subject-id"
        const val DATALAKE_X_SUBJECT_ID_VALUE = "base64"
        const val DATALAKE_X_API_KEY_HEADER = "x-api-key"
        const val DATALAKE_X_API_KEY_VALUE = "dCbwYN80p5cCdGDeCcOVW6ojJdTblN41dzRKWHd0"
        // end region datalake

        // region central
        // SIT
//        const val CENTRAL_HOST_NAME = "https://sit-api.central.tech"
//        const val CLIENT_SERVICE_NAME = "execute-api"
//        const val CLIENT_REGION = "ap-southeast-1"
//        const val CLIENT_X_API_KEY = "lIrZy8ZTEvkmu4uDe0m06wqNo91REUN7aWnk6GYi"
//        const val CLIENT_ACCESS_KEY = "AKIAIZBEAJOWKANSLLYA"
//        const val CLIENT_SECRET_KEY = "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"

        // UAT
        const val CENTRAL_HOST_NAME = "https://uat-api.central.tech"
        const val FIRE_STORE_COLLECTION_NAME = "secret-key"
        const val FIRE_STORE_DOCUMENT_KEY = "staging"

        // endregion

        // region formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // endregion
    }
}