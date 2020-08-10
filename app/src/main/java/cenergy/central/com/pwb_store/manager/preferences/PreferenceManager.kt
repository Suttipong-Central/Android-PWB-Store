package cenergy.central.com.pwb_store.manager.preferences

import android.content.Context
import android.content.SharedPreferences
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.SecretKey
import cenergy.central.com.pwb_store.model.response.LoginUserResponse

class PreferenceManager(private var context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences(this.context.getString(R.string.preference_file_name), Context.MODE_PRIVATE)

    val cartId: String?
        get() = pref.getString(PREF_CART_ID, null)

    fun setCartId(cartId: String?) {
        val editor = pref.edit()
        editor.putString(PREF_CART_ID, cartId)
        editor.apply()
    }

    //TODO: Store userInfo in sharepreference
    // region user
    val userToken: String?
        get() = pref.getString(PREF_USER_TOKEN, null)

    val userId: Long
        get() = pref.getLong(PREF_USER_ID, 0)

    val userStaffId: String?
        get() = pref.getString(PREF_USER_STAFF_ID, null)

    val userLevel = pref.getInt(PREF_USER_LEVEL_ID, 0)

    fun setUserInfo(userToken: String, userLoginResponse: LoginUserResponse) {
        val editor = pref.edit()
        editor.putString(PREF_USER_TOKEN, userToken)
        editor.putLong(PREF_USER_ID, userLoginResponse.userId)
        editor.putString(PREF_USER_STAFF_ID, userLoginResponse.staffId)
        editor.putInt(PREF_USER_LEVEL_ID, userLoginResponse.levelId.toInt())
        editor.apply()
    }

    private fun clearUserInfo() {
        val editor = pref.edit()
        editor.remove(PREF_USER_TOKEN)
        editor.remove(PREF_USER_ID)
        editor.remove(PREF_USER_STAFF_ID)
        editor.remove(PREF_USER_LEVEL_ID)
        editor.apply()
    }
    // endregion

    fun setSpecialCategoryIds(specialIds: String) {
        val editor = pref.edit()
        editor.putString(PREF_SPECIAL_CATEGORY_IDS, specialIds)
        editor.apply()
    }

    // "130639,130704,131187"
    fun getSpecialCategoryIds(): String {
        return pref.getString(PREF_SPECIAL_CATEGORY_IDS, "") ?: ""
    }

    fun clearCartId() {
        pref.edit().remove(PREF_CART_ID).apply()
    }

    fun userLogout() {
        clearPreference()
    }

    fun setDefaultLanguage(lang: AppLanguage) {
        val editor = pref.edit()
        editor.putString(PREF_LANGUAGE, lang.toString())
        editor.apply()
    }

    fun getDefaultLanguage(): String = pref.getString(PREF_LANGUAGE, AppLanguage.TH.key)
            ?: AppLanguage.TH.key

    fun setSecretKey(secretKey: SecretKey) {
        val editor = pref.edit()
        editor.putString(PREF_SECRET_KEY, secretKey.secretKey)
        editor.putString(PREF_ACCESS_KEY, secretKey.accessKey)
        editor.putString(PREF_REGION, secretKey.region)
        editor.putString(PREF_X_API_KEY, secretKey.xApiKey)
        editor.putString(PREF_SERVICE_NAME, secretKey.serviceName)
        editor.putString(PREF_X_API_KEY_CONSENT, secretKey.xApiKeyConsent)
        editor.apply()
    }

    val secretKey: String?
        get() = pref.getString(PREF_SECRET_KEY, null)
    val accessKey: String?
        get() = pref.getString(PREF_ACCESS_KEY, null)
    val region: String?
        get() = pref.getString(PREF_REGION, null)
    val xApiKey: String?
        get() = pref.getString(PREF_X_API_KEY, null)
    val serviceName: String?
        get() = pref.getString(PREF_SERVICE_NAME, null)
    val xApiKeyConsent: String?
        get() = pref.getString(PREF_X_API_KEY_CONSENT, null)

    private fun clearPreference() {
//        pref.edit().clear().apply()
        clearCartId()
        clearSecretKey()
        clearUserInfo()
    }

    private fun clearDefaultLanguage() {
        pref.edit().remove(PREF_LANGUAGE).apply()
    }

    private fun clearSecretKey() {
        pref.edit().remove(PREF_SECRET_KEY).apply()
        pref.edit().remove(PREF_ACCESS_KEY).apply()
        pref.edit().remove(PREF_REGION).apply()
        pref.edit().remove(PREF_X_API_KEY).apply()
        pref.edit().remove(PREF_SERVICE_NAME).apply()
        pref.edit().remove(PREF_X_API_KEY_CONSENT).apply()
    }

    companion object {
        const val PREF_CART_ID = "cart_id"
        const val PREF_LANGUAGE = "pref_language"
        const val PREF_SPECIAL_CATEGORY_IDS = "pref_special_category_ids"
        const val PREF_SECRET_KEY = "pref_secret_key"
        const val PREF_ACCESS_KEY = "pref_access_key"
        const val PREF_REGION = "pref_region"
        const val PREF_X_API_KEY = "pref_x_api_key"
        const val PREF_SERVICE_NAME = "pref_service_name"
        const val PREF_X_API_KEY_CONSENT = "pref_x_api_key_consent"

        const val PREF_USER_TOKEN = "pref_user_token"
        const val PREF_USER_ID = "pref_user_id"
        const val PREF_USER_STAFF_ID = "pref_user_staff_id"
        const val PREF_USER_LEVEL_ID = "pref_user_level_id"
    }
}

enum class AppLanguage(val key: String) {
    TH("th") {
        override fun toString(): String = "th"
    },
    EN("en") {
        override fun toString(): String = "en"
    };

    companion object {
        private val map = values().associateBy(AppLanguage::key)
        fun fromString(value: String) = map[value] ?: TH
    }
}