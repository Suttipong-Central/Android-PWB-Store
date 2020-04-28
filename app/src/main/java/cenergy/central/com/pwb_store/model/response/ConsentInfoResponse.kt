package cenergy.central.com.pwb_store.model.response

import com.google.gson.annotations.SerializedName

class ConsentInfoResponse(
        @SerializedName("marketing_display_text")
        var displayText: ConsentDisplayText,
        @SerializedName("privacy_policy")
        var privacy: PrivacyPolicy,
        @SerializedName("consent_privacy_version")
        var privacyVersion: String
)

class ConsentDisplayText(
        var th: String,
        var en: String
)

class PrivacyPolicy(
        var th: String,
        var en: String
)