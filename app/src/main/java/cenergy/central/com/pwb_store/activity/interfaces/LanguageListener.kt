package cenergy.central.com.pwb_store.activity.interfaces

import cenergy.central.com.pwb_store.manager.preferences.AppLanguage

interface LanguageListener {
    fun onChangedLanguage(lang: AppLanguage)
}