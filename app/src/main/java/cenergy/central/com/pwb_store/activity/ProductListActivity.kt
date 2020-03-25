package cenergy.central.com.pwb_store.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import cenergy.central.com.pwb_store.R
import cenergy.central.com.pwb_store.fragment.ProductListFragment.Companion.newInstance
import cenergy.central.com.pwb_store.manager.preferences.AppLanguage
import cenergy.central.com.pwb_store.view.LanguageButton
import cenergy.central.com.pwb_store.view.NetworkStateView

class ProductListActivity : BaseActivity() {
    private var isSearch = false
    private var languageButton: LanguageButton? = null
    private var networkStateView: NetworkStateView? = null
    private var keyWord: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)
        languageButton = findViewById(R.id.switch_language_button)
        handleChangeLanguage()
        val mIntent = intent
        val extras = mIntent.extras
        if (extras != null) {
            isSearch = extras.getBoolean(ARG_SEARCH)
            keyWord = extras.getString(ARG_KEY_WORD)
        }
        initView()
        startProductListFragment(keyWord, isSearch)
    }

    override fun onChangedLanguage(lang: AppLanguage) {
        super.onChangedLanguage(lang)
        startProductListFragment(keyWord, isSearch)
    }

    override fun getSwitchButton(): LanguageButton? {
        return languageButton
    }

    private fun initView() {
        networkStateView = findViewById(R.id.network_state_View)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        mToolbar.setNavigationOnClickListener { finish() }
    }

    private fun startProductListFragment(keyWord: String?, isSearch: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
                .replace(R.id.container, newInstance(keyWord, isSearch, "0", "", null, keyWord))
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        setResult(RESULT_UPDATE_LANGUAGE)
        finish()
    }

    override fun getStateView(): NetworkStateView? {
        return networkStateView
    }

    companion object {
        const val ARG_SEARCH = "ARG_SEARCH"
        const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"
        const val ARG_KEY_WORD = "ARG_KEY_WORD"
    }
}