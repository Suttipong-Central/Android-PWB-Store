package cenergy.central.com.pwb_store.model.body

import com.google.gson.annotations.SerializedName

data class ConsentBody (
        var email: String = "",
        var channel: String = "",
        @SerializedName("ref_id")
        var cartId: String = "",
        var partner: String = "",
        var brand: String = "",
        @SerializedName("consent_privacy_version")
        var privacyVersion: String = "",
        @SerializedName("consent_privacy_status")
        var privacyStatus: Boolean = false,
        @SerializedName("consent_marketing_status")
        var marketingStatus: Boolean = false
){
    companion object{
        private const val CHANEL = "EORDERING"
        private const val PARTNER = "PWB"
        private const val BRAND = "HQ"

        fun createBody(email: String, cartId: String, privacyVersion: String, isCheckConsent: Boolean): ConsentBody{
            return ConsentBody(
                    email = email,
                    channel = CHANEL,
                    cartId = "cart-id:$cartId",
                    partner = PARTNER,
                    brand = BRAND,
                    privacyVersion = privacyVersion,
                    privacyStatus = true,
                    marketingStatus = isCheckConsent
            )
        }
    }
}