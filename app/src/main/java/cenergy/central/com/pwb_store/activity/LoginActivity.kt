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

class LoginActivity : BaseActivity() {

    // widget view
    private lateinit var networkStateView: NetworkStateView
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
        networkStateView = findViewById(R.id.network_state_View)
        //Load Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commit()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        EventBus.getDefault().unregister(this)
        super.onPause()
    }

    // region language button
    override fun getSwitchButton(): LanguageButton? {
        return languageButton
    }

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        // recreate view
        initView()
    }
    // endregion

    // region network state
    override fun getStateView(): NetworkStateView? = networkStateView
    // end region
}