package cenergy.central.com.pwb_store

/**
 * Created by Anuphap Suwannamas on 23/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

class Constants {
    companion object {
        // region magento
        const val BASE_URL_MAGENTO = "https://staging.powerbuy.co.th"
        const val CLIENT_MAGENTO = "Bearer ba102y6thpckeoqgo196u82tllvlf50q"
        const val PWB_HOST_NAME = "staging.powerbuy.co.th"
        // endregion

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
    }
}