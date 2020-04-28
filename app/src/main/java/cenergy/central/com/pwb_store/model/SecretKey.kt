package cenergy.central.com.pwb_store.model

import com.google.gson.annotations.SerializedName

data class SecretKey(
        @SerializedName("access_key")
        val accessKey: String? = null,
        val secretKey: String? = null,
        val xApiKey: String? = null,
        val serviceName: String? = null,
        val region: String? = null,
        val xApiKeyConsent: String? = null
)