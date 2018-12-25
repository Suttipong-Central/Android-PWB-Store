package cenergy.central.com.pwb_store.activity

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.view.LanguageButton
import java.util.*


abstract class BaseActivity : AppCompatActivity(), LanguageButton.LanguageListener {

    val preferenceManager by lazy { PreferenceManager(this) }
    var currentLanguage = ""

    private var languageButton: LanguageButton? = null

    fun handleChangeLanguage() {
        val language = preferenceManager.getDefaultLanguage()
        if (currentLanguage == language) {
            Log.d("BaseActivity", "same language")
            return
        }

        Toast.makeText(this, "Change language to $language", Toast.LENGTH_SHORT).show()

        if (languageButton == null && getSwitchButton() != null) {
            languageButton = getSwitchButton()
            languageButton!!.setDefaultLanguage(language)
            languageButton!!.setOnLanguageChangeListener(this)
        }

        val res = resources
        // Change locale settings in the app.
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(language))
        res.updateConfiguration(conf, dm)

        currentLanguage = language
    }

    abstract fun getSwitchButton(): LanguageButton?

    // region {@link LanguageButton.LanguageListener}
    override fun onChangedLanguage(lang: AppLanguage) {
        preferenceManager.setDefaultLanguage(lang) // save language
        handleChangeLanguage()
        //        recreate();
    }
    // region

    companion object {
        // request update
        const val REQUEST_UPDATE_LANGUAGE = 4000
    }
}