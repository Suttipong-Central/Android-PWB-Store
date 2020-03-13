package cenergy.central.com.pwb_store.manager.preferences

import android.content.Context
import android.content.SharedPreferences
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.model.SecretKey

class PreferenceManager(private var context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences(this.context.getString(R.string.preference_file_name), Context.MODE_PRIVATE)

    val cartId: String?
        get() = pref.getString(PREF_CART_ID, null)

    fun setCartId(cartId: String?) {
        val editor = pref.edit()
        editor.putString(PREF_CART_ID, cartId)
        editor.apply()
    }

    val isAddressLoaded: Boolean
        get() = pref.getBoolean(PREF_ADDRESS_LOADED, false)

    fun setAddressLoaded(loaded: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(PREF_ADDRESS_LOADED, loaded)
        editor.apply()
    }

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

    private fun clearPreference() {
//        pref.edit().clear().apply()
        clearCartId()
        clearSecretKey()
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
    }

    companion object {
        const val PREF_CART_ID = "cart_id"
        const val PREF_ADDRESS_LOADED = "address_loaded"
        const val PREF_LANGUAGE = "pref_language"
        const val PREF_SPECIAL_CATEGORY_IDS = "pref_special_category_ids"
        const val PREF_SECRET_KEY = "pref_secret_key"
        const val PREF_ACCESS_KEY = "pref_access_key"
        const val PREF_REGION = "pref_region"
        const val PREF_X_API_KEY = "pref_x_api_key"
        const val PREF_SERVICE_NAME = "pref_service_name"
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