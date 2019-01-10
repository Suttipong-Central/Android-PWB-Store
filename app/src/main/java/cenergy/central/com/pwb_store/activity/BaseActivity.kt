package cenergy.central.com.pwb_store.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.manager.preferences.PreferenceManager
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import java.util.*


abstract class BaseActivity : AppCompatActivity(), LanguageButton.LanguageListener,
        NetworkReceiver.NetworkStateLister {

    val preferenceManager by lazy { PreferenceManager(this) }
    private val onNetworkReceived by lazy { NetworkReceiver(this) }
    private var currentLanguage = ""

    private var languageButton: LanguageButton? = null

    override fun onResume() {
        registerReceiver(onNetworkReceived, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) // register broadcast
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(onNetworkReceived) // unregister broadcast
        super.onPause()
    }

    fun handleChangeLanguage() {
        val language = preferenceManager.getDefaultLanguage()
        if (currentLanguage == language) {
            Log.d("BaseActivity", "same language")
            return
        }

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

    abstract fun getStateView(): NetworkStateView?

    // region {@link LanguageButton.LanguageListener}
    override fun onChangedLanguage(lang: AppLanguage) {
        preferenceManager.setDefaultLanguage(lang) // save language
        handleChangeLanguage()
        //        recreate();
    }
    // region

    override fun onNetworkStateChange(state: NetworkInfo.State) {
        getStateView()?.let { stateView ->
            when (state) {
                NetworkInfo.State.CONNECTED -> stateView.onConnected()
                NetworkInfo.State.UNKNOWN, NetworkInfo.State.CONNECTING -> stateView.onConnecting()
                NetworkInfo.State.DISCONNECTED -> stateView.onDisconnected()
                else -> stateView.onDisconnected()
            }
        }
    }

    companion object {
        // request update
        const val REQUEST_UPDATE_LANGUAGE = 4000
        const val RESULT_UPDATE_LANGUAGE = 4001
    }
}