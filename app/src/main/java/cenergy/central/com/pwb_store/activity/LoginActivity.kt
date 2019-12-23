package cenergy.central.com.pwb_store.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.LoginFragment
import cenergy.central.com.pwb_store.manager.bus.event.LoginSuccessBus
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.utils.Analytics
import cenergy.central.com.pwb_store.utils.DeepLink
import cenergy.central.com.pwb_store.utils.Screen
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginActivity : BaseActivity() {

    // widget view
    private lateinit var networkStateView: NetworkStateView
    private lateinit var languageButton: LanguageButton
    private val analytics by lazy { Analytics(this) }

    private var pathSegments = arrayOf("")
    private var uriDeepLink: Uri? = null
    private var link = ""

    @Subscribe
    fun onEvent(loginSuccessBus: LoginSuccessBus) {
        if (loginSuccessBus.isSuccess) {
            // tracking event
            val userInfo = loginSuccessBus.userInformation
            analytics.trackLoginSuccess(userInfo.store?.retailerId)

            if (pathSegments.isNotEmpty() && uriDeepLink != null && link.isNotEmpty()){
                DeepLink(this).checkIntent(pathSegments, uriDeepLink!!, link)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                ActivityCompat.startActivity(this, intent,
                        ActivityOptionsCompat
                                .makeBasic()
                                .toBundle())
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        languageButton = findViewById(R.id.switch_language_button)
        handleChangeLanguage()
        if (intent != null) {
            if (intent.hasExtra(DeepLink.DEEP_LINK_EXTRA_PATH_SEGMENTS)) {
                pathSegments = intent.getStringArrayExtra(DeepLink.DEEP_LINK_EXTRA_PATH_SEGMENTS) ?: arrayOf()
            }
            if (intent.hasExtra(DeepLink.DEEP_LINK_EXTRA_URI)) {
                uriDeepLink = intent.getParcelableExtra(DeepLink.DEEP_LINK_EXTRA_URI)
            }
            if (intent.hasExtra(DeepLink.DEEP_LINK_EXTRA_LINK)) {
                link = intent.getStringExtra(DeepLink.DEEP_LINK_EXTRA_LINK) ?: ""
            }
        }
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
        analytics.trackScreen(Screen.LOGIN)
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