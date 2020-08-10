package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        // region magento
        const val BASE_URL_MAGENTO = "https://staging-mdc.powerbuy.co.th"
        const val MDC_HOST_NAME = "staging-mdc.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer ngvids7tnggs94sm81k8a3oxjgl9cd16"
        // endregion

        const val WEB_HOST_NAME = "https://staging-fe.powerbuy.co.th"

        // Central UAT
        const val CENTRAL_HOST_NAME = "https://uat-api.central.tech"

        // Consent
        const val CONSENT_HOSTNAME = "https://api-uat.central.tech/member/"
        const val CONSENT_CHANNEL = "EORDERING"
        const val CONSENT_PARTNER = "PWB"

        // Firebase
        const val FIRE_STORE_COLLECTION_NAME = "secret-key"
        const val FIRE_STORE_DOCUMENT_KEY = "staging"

        // region formatter
        val DATE_FORMATTER_VALUE = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        // endregion
    }
}