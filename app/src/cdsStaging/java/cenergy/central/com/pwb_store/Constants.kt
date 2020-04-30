package cenergy.central.com.pwb_store

import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        // region endpoint
        const val BASE_URL_MAGENTO = "https://staging-mdc.central.co.th"
        const val CLIENT_MAGENTO = "Bearer jl7r6o9klp4gl1pt0w6to55hsqhg30nr"
        const val PWB_HOST_NAME = "staging-mdc.central.co.th"
        // ens region

        const val WEB_HOST_NAME = "https://staging.central.co.th"

        // region central
        const val CENTRAL_HOST_NAME = "https://uat-api.central.tech"

        // Consent
        // TODO change to use CDS consent later
        const val CONSENT_HOSTNAME = "https://api-uat.central.tech"
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