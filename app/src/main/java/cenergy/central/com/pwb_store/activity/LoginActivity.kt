package cenergy.central.com.pwb_store.activity

import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.LoginFragment
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus
import cenergy.central.com.pwb_store.manager.network.NetworkReceiver
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class LoginActivity : BaseActivity(), NetworkReceiver.NetworkStateLister {

    companion object {
        const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    private val onNetworkReceived = NetworkReceiver(this)
    private lateinit var stateView: NetworkStateView
    private lateinit var languageButton: LanguageButton

    @Subscribe
    fun onEvent(loginSuccessBus: LoginSuccessBus) {
        if (loginSuccessBus.isSuccess) {
            val intent = Intent(this, MainActivity::class.java)
            ActivityCompat.startActivity(this, intent,
                    ActivityOptionsCompat
                            .makeBasic()
                            .toBundle())
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        languageButton = findViewById(R.id.switch_language_button)
        handleChangeLanguage()
        initView()
    }

    private fun initView() {
        stateView = findViewById(R.id.network_state_View)
        //Load Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commit()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(onNetworkReceived, IntentFilter(CONNECTIVITY_ACTION)) // register broadcast
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        unregisterReceiver(onNetworkReceived) // unregister broadcast
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    override fun onNetworkStateChange(state: NetworkInfo.State) {
        when (state) {
            NetworkInfo.State.CONNECTED -> stateView.onConnected()
            NetworkInfo.State.UNKNOWN, NetworkInfo.State.CONNECTING -> stateView.onConnecting()
            NetworkInfo.State.DISCONNECTED -> stateView.onDisconnected()
            else -> stateView.onDisconnected()
        }
    }

    override fun getSwitchButton(): LanguageButton? {
        return languageButton
    }

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        // recreate view
        initView()
    }
}