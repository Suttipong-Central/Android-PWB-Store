package cenergy.central.com.pwb_store.utils

import cenergy.central.com.pwb_store.BuildConfig
import cenergy.central.com.pwb_store.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfigUtils {
    const val CONFIG_KEY_PAYMENT_OPTIONS_ON = "is_payment_options_on"
    const val CONFIG_KEY_SUPPORTED_PAYMENT_METHODS = "supported_payment_methods"
    const val CONFIG_KEY_SUPPORTED_DELIVERY_METHODS = "supported_delivery_methods"
    const val CONFIG_KEY_EORDERING_MEMBER_ON = "is_eordering_member_on"
    const val CONFIG_KEY_T1C_MEMBER_ON = "is_t1c_member_on"
    const val CONFIG_KEY_HDL_MEMBER_ON = "is_hdl_member_on"
    const val CONFIG_KEY_DISPLAY_SPECIAL_CATEGORY_ID = "display_special_category_ids"
    const val CONFIG_KEY_SUPPORT_COUPON_ON = "is_support_coupon_on"
    const val CONFIG_KEY_EORDERING_PAYMENT_ON = "is_eodering_payment_on"

    private var cacheExpiration: Long = 3600 // 1 hour in seconds.
    fun initFirebaseRemoteConfig(): FirebaseRemoteConfig {
       val fbRemoteConfig = FirebaseRemoteConfig.getInstance()

        if (!BuildConfig.IS_PRODUCTION) { // is not Production?
            cacheExpiration = 0
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .build()
        fbRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        fbRemoteConfig.setConfigSettingsAsync(configSettings)
        return fbRemoteConfig
    }
}