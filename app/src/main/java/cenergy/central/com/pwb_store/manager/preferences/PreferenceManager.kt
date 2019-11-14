package cenergy.central.com.pwb_store.manager.preferences

import android.content.Context
import android.content.SharedPreferences
import cenergy.central.com.pwb_store.R

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

    fun getDefaultLanguage(): String = pref.getString(PREF_LANGUAGE, AppLanguage.TH.key) ?:  AppLanguage.TH.key

    private fun clearPreference() {
//        pref.edit().clear().apply()
        clearCartId()
    }

    private fun clearDefaultLanguage() {
        pref.edit().remove(PREF_LANGUAGE).apply()
    }

    companion object {
        const val PREF_CART_ID = "cart_id"
        const val PREF_ADDRESS_LOADED = "address_loaded"
        const val PREF_LANGUAGE = "pref_language"
        const val PREF_SPECIAL_CATEGORY_IDS = "pref_special_category_ids"
    }
}

enum class AppLanguage(val key: String) {
    TH("th") {
        override fun toString(): String = "th"
    },
    EN("en") {
        override fun toString(): String = "en"
    },
}